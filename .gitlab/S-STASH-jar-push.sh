#!/bin/bash

printf "Run sbt without tests...\n"
sbt 'set test in assembly := {}' -v assembly

JAR_FILE_PATH="target/scala-2.12"
SCE_PROJECT=$(echo "$CI_PROJECT_NAME" | tr '[:upper:]' '[:lower:]')

# Create a folder
printf "Create folder for %s...\n" "$SCE_PROJECT"
curl -X MKCOL -u "$OWNCLOUD_USER":"$OWNCLOUD_TOKEN" "$OWNCLOUD_ENDPOINT"/"$SCE_PROJECT"

printf "Create folder for %s...\n" "$CI_COMMIT_TAG"
curl -X MKCOL -u "$OWNCLOUD_USER":"$OWNCLOUD_TOKEN" "$OWNCLOUD_ENDPOINT"/"$SCE_PROJECT"/"$CI_COMMIT_TAG"

# Upload
for file in ./"$JAR_FILE_PATH"/*.jar; do
     FILENAME=${file##*/}
     FULL_FILENAME=$(pwd)/$JAR_FILE_PATH/$FILENAME
     printf "Uploading file: %s" "$FULL_FILENAME"

     curl -X PUT \
     -u "$OWNCLOUD_USER":"$OWNCLOUD_TOKEN" \
     "$OWNCLOUD_ENDPOINT"/"$SCE_PROJECT"/"$CI_COMMIT_TAG"/"$FILENAME" \
     -F "$FILENAME"=@"$FULL_FILENAME"
done
