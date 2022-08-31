# Start localstack in podman

echo 'Start localstack S3 via Podman'
podman run --rm --name local-s3 -p 4566:4566 -p 4572:4572 -e SERVICES=s3 -e START_WEB=0 -d localstack/localstack

sleep 5

echo 'Create Bucket'
# Create bucket
aws s3 mb s3://recipevault-images --endpoint-url=http://localhost:4566

# Confirm 'recipe-images' Bucket is listed
#aws s3 ls --endpoint-url=http://localhost:4566

echo 'Apply Policy to S3 Bucket'
# Apply Read-Anonymous to Bucket
aws s3api put-bucket-policy --no-verify-ssl --endpoint-url "http://localhost:4566" --bucket recipevault-images --policy "$(cat ./public_s3.json)"