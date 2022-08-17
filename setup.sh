oc new-project recipevault
oc new-app --name=postgresql --template=postgresql-persistent -p DATABASE_SERVICE_NAME=postgresql -p POSTGRESQL_USER=recipevault -p POSTGRESQL_PASSWORD=recipevault -p POSTGRESQL_DATABASE=recipevaultdb
perl -pe "s|(VUE_APP_IMAGE_SERVER_URL:).*$|\$1 https://$(oc get route/s3 -n openshift-storage -o jsonpath='{.spec.host}')/receipevault|" -i deploy/ui-recipevault/recipevault-ui-config.yaml
perl -pe "s|(VUE_APP_RECIPE_DATA_SERVICE:).*$|\$1 https://$(oc get route/rest-recipe -o jsonpath='{.spec.host}')|" -i deploy/ui-recipevault/recipevault-ui-config.yaml
perl -pe "s|(https://).*$|\$1$(oc get route/s3 -n openshift-storage -o jsonpath='{.spec.host}')|" -i deploy/rest-recipe/rest-recipe-deployment.yaml
