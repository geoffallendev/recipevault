project=recipevault
bucket=recipevault-images
oc wait --for=delete ns/recipevault -A
oc new-project recipevault

# database
oc new-app --name=postgresql --template=postgresql-persistent -p DATABASE_SERVICE_NAME=postgresql -p POSTGRESQL_USER=recipevault -p POSTGRESQL_PASSWORD=recipevault -p POSTGRESQL_DATABASE=recipevaultdb
oc wait --for=jsonpath='{.status.availableReplicas}'=1 dc -l app=postgresql
oc wait --for=jsonpath='{.status.phase}'=Running pod -l name=postgresql


# S3 storage
perl -pe "s|(http(s)?://).*$|http://$(oc get route/s3 -n openshift-storage -o jsonpath='{.spec.host}')|" -i .//rest-recipe/rest-recipe-deployment.yaml
oc apply -f .//s3/obc.yaml
oc wait --for=jsonpath='{.status.phase}'=Bound obc/$bucket
perl -pe "s|(\"Resource\": \".*?:::)[^\"/]*|\$1$bucket|" -i .//s3/public_s3.json
export AWS_ACCESS_KEY_ID=$(oc get secret -o go-template --template="{{.data.AWS_ACCESS_KEY_ID|base64decode}}" $bucket)
export AWS_SECRET_ACCESS_KEY=$(oc get secret -o go-template --template="{{.data.AWS_SECRET_ACCESS_KEY|base64decode}}" $bucket)
s3route=$(oc get route/s3 -n openshift-storage -o jsonpath='{.spec.host}')

aws s3api put-bucket-policy --no-verify-ssl --endpoint-url "https://$s3route" --bucket $bucket --policy "$(cat ./s3/public_s3.json)"

# backend
oc apply -f ./rest-recipe/
oc wait --for=jsonpath='{.status.phase}'=Running pod -l app=rest-recipe

# frontend
perl -pe "s|(VUE_APP_IMAGE_SERVER_URL:).*$|\$1 http://$(oc get route/s3 -n openshift-storage -o jsonpath='{.spec.host}')/$bucket/|" -i .//ui-recipevault/recipevault-ui-config.yaml
perl -pe "s|(VUE_APP_RECIPE_DATA_SERVICE:).*$|\$1 https://$(oc get route/rest-recipe -o jsonpath='{.spec.host}')|" -i .//ui-recipevault/recipevault-ui-config.yaml
oc apply -f ./ui-recipevault/

oc wait --for=jsonpath='{.status.availableReplicas}'=1 deployment -l app=ui-recipe

echo "Recipe Vault UI: http://$(oc get route/ui-recipe -n recipevault -o jsonpath='{.spec.host}')"
