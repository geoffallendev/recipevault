oc wait --for=delete ns/recipevault -A
oc new-project recipevault
# database
oc new-app --name=postgresql --template=postgresql-persistent -p DATABASE_SERVICE_NAME=postgresql -p POSTGRESQL_USER=recipevault -p POSTGRESQL_PASSWORD=recipevault -p POSTGRESQL_DATABASE=recipevaultdb
oc wait --for=jsonpath='{.status.availableReplicas}'=1 dc -l app=postgresql
oc wait --for=jsonpath='{.status.phase}'=Running pod -l name=postgresql

# S3 storage
perl -pe "s|(http(s)?://).*$|http://$(oc get route/s3 -n openshift-storage -o jsonpath='{.spec.host}')|" -i deploy/rest-recipe/rest-recipe-deployment.yaml
oc apply -f deploy/s3/obc.yaml
perl -pe "s|(\"Resource\": \".*?:::)[^\"/]*|\$1recipevault-images|" -i deploy/s3/public_s3.json
aws s3api put-bucket-policy --no-verify-ssl --endpoint-url https://s3-openshift-storage.apps.ocp.webwim.com --bucket recipevault-images --policy "$(cat deploy/s3/public_s3.json)"

# backend
oc apply -f deploy/rest-recipe/
oc wait --for=jsonpath='{.status.phase}'=Running pod -l app=rest-recipe

# frontend
perl -pe "s|(VUE_APP_IMAGE_SERVER_URL:).*$|\$1 http://$(oc get route/s3 -n openshift-storage -o jsonpath='{.spec.host}')/recipevault-images/|" -i deploy/ui-recipevault/recipevault-ui-config.yaml
perl -pe "s|(VUE_APP_RECIPE_DATA_SERVICE:).*$|\$1 https://$(oc get route/rest-recipe -o jsonpath='{.spec.host}')|" -i deploy/ui-recipevault/recipevault-ui-config.yaml
oc apply -f deploy/ui-recipevault/
