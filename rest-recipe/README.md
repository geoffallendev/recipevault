# RecipeVault:Quarkus

 - RESTEasy to expose the REST endpoints
 - Hibernate ORM with Panache to perform the CRUD operations on the database
 - A PostgreSQL database - (Uses Dev Services for local testing)
 - Image storage with S3 compatible storage

## Requirements

To compile and run this demo you will need:

- JDK 11+
- GraalVM (if you want to compile to native)

# Development Guide

## Local S3 Emulator via Localstack

User script to deploy and configure local S3 (uses podman in script)
[Localstack S3 ](../deploy/s3/setup_local_s3.sh)

### S3 local instance - Manually Steps
## start local S3 emulator
`podman run --rm --name local-s3 -p 4566:4566 -p 4572:4572 -e SERVICES=s3 -e START_WEB=0 -d localstack/localstack`

## Create bucket
`aws s3 mb s3://recipevault-images --endpoint-url=http://localhost:4566`
make_bucket: recipevault-images

## Confirm 'recipe-images' Bucket is listed
`aws s3 ls --endpoint-url=http://localhost:4566`
2022-01-14 10:01:21 recipevault-images

### Apply Read-Anonymous to Bucket
`aws s3api put-bucket-policy --no-verify-ssl --endpoint-url "http://localhost:4566" --bucket recipevault-images --policy "$(cat ../deploy/s3/public_s3.json)"`

### Put a test object/image
`aws s3 cp --endpoint-url "http://localhost:4566" ../../rest-recipe/src/test/resources/payloads/images/blueberry_kuechen.jpg s3://recipevault-images/blueberry.jpg`

### Validate the URL
http://localhost:4566/recipevault-images/blueberry.jpg

### Delete the test object/image
`aws s3api delete-object --bucket recipevault-images --endpoint-url "http://localhost:4566" --key blueberry_kuechen.jpg`


## PostgreSQL - Should be handled by DevServices/TestContainers in Quarkus


## Live coding with Quarkus

The Maven Quarkus plugin provides a development mode that supports
live coding. To try this out:

`cd rest-recipe`

`./mvnw quarkus:dev`


## Build Jar
Build the Jar to deploy on OCP

`mvn clean package -DskipTests -Dquarkus.package.type=uber-jar`

Package for building image

`./mvnw -DskipTests=true package`

## Build a container image:

`podman build -f src/main/docker/Dockerfile.jvm -t rest-recipe .`

`podman tag rest-recipe quay.io/<QUAY_REGISTRY>/rest-recipe`

`podman login quay.io`

`podman push quay.io/<QUAY_REGISTRY>/rest-recipe`


## Running in native
You can compile the application into a native executable using:

`./mvn -DskipTests clean package -Pnative`

and run with:

`./target/` 
