# RecipeVault

Recipe Vault is a Quarkus and Vue.js based Recipe Manager application

# RecipeVault - Requirements

 - RESTEasy to expose the REST endpoints
 - Hibernate ORM with Panache to perform the CRUD operations on the database
 - A PostgreSQL database - (Uses Dev Services for local testing)
 - Image storage with S3 compatible storage 
 - Docker Desktop (I'm on a mac) to support Test Containers/Quarkus Dev Services
 -


Tools required
- S3 cli tool. I used S3 CMD - https://github.com/s3tools/s3cmd
- jq - commandline JSON processor [version 1.6]
- oc - OpenShift CLI

### Create recipevault project
`oc new-project recipevault`

### Deploy ODF and Configure Bucket

- [ODF Install](./odf/README.md)


### Deploy Postgres DB (using template)
`oc new-app --name=postgresql --template=postgresql-persistent \
-p DATABASE_SERVICE_NAME=postgresql \
-p POSTGRESQL_USER=recipevault \
-p POSTGRESQL_PASSWORD=recipevault \
-p POSTGRESQL_DATABASE=recipevaultdb `


### Deploy Recipe Rest Service

Deploy app from existing image

Obtain and Apply S3 Route in [rest-recipe-deployment.yaml](./rest-recipe/rest-recipe-deployment.yaml)

`oc get route s3 -n openshift-storage -o json | jq -r '.spec.host'`

`oc apply -f ./deploy/rest-recipe/`

### Update Config Map
Update VUE_APP_RECIPE_DATA_SERVICE with REST_RECIPE_ROUTE 

`oc get route/rest-recipe`

Update VUE_APP_IMAGE_SERVER_URL with S3 Public Endpoint

`oc get route s3 -n openshift-storage -o json | jq -r '.spec.host'`

Deploy app

`oc apply -f ./deploy/ui-recipevault/`

Access the UI and create recipes!!


