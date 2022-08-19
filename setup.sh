oc new-project recipevault
oc new-app --name=postgresql --template=postgresql-persistent -p DATABASE_SERVICE_NAME=postgresql -p POSTGRESQL_USER=recipevault -p POSTGRESQL_PASSWORD=recipevault -p POSTGRESQL_DATABASE=recipevaultdb
perl -pe "s|(VUE_APP_IMAGE_SERVER_URL:).*$|\$1 http://$(oc get route/s3 -n openshift-storage -o jsonpath='{.spec.host}')/recipevault-images/|" -i deploy/ui-recipevault/recipevault-ui-config.yaml
perl -pe "s|(VUE_APP_RECIPE_DATA_SERVICE:).*$|\$1 https://$(oc get route/rest-recipe -o jsonpath='{.spec.host}')|" -i deploy/ui-recipevault/recipevault-ui-config.yaml
perl -pe "s|(http(s)?://).*$|http://$(oc get route/s3 -n openshift-storage -o jsonpath='{.spec.host}')|" -i deploy/rest-recipe/rest-recipe-deployment.yaml
oc apply -f deploy/s3/obc.yaml
oc apply -f deploy/rest-recipe/
oc apply -f deploy/ui-recipevault/
