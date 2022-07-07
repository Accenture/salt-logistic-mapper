#!/bin/bash

printf "Run sbt without tests...\n"
sbt 'set test in assembly := {}' -v assembly

SCE_PROJECT=$(echo "$CI_PROJECT_NAME" | tr '[:upper:]' '[:lower:]')
echo "$SCE_PROJECT"
CREATED_JAR_FILE=$(find $SHAREPOINT_JAR_FILE_PATH -name '*assembly*.jar'|cut -d"/" -f3-)

MAIN_FOLDER_NAME=$(cut -d'-' -f1 <<<$CREATED_JAR_FILE)
JAR_VERSION=$(cut -d'-' -f3 <<<$CREATED_JAR_FILE)
VERSION_FOLDER=${JAR_VERSION%%.jar*}


grant_type="$SHAREPOINT_GRANT_TYPE"
cl_id="$SHAREPOINT_CLIENT_ID"
cl_secret="$SHAREPOINT_CLIENT_SECRET"
res="$SHAREPOINT_RESOURCE"
url="$SHAREPOINT_TOKEN_URL"
content_type="$SHAREPOINT_CONTENT_TYPE"

# Take a new token, the token have to be a new token every time.
ACESS_TOKEN=$(curl -X POST -H $content_type --data-urlencode $grant_type --data-urlencode $cl_id --data-urlencode $cl_secret --data-urlencode $res -s $url | awk -F":" '{print $NF}' | tr -d '"}')


# add new main folder if not exist
curl -X POST "$SHAREPOINT_CREATE_FOLDER_URL" -H "Authorization: Bearer $ACESS_TOKEN" -H "Content-Type: application/json;odata=verbose" --data-raw "{
  \"__metadata\": {
    \"type\": \"SP.Folder\"
  },
  \"ServerRelativeUrl\": \"$SHAREPOINT_CREATE_FOLDER_URL_JSON$MAIN_FOLDER_NAME\"
}"

# add new folder with the new version number
curl -X POST "$SHAREPOINT_CREATE_FOLDER_URL" -H "Authorization: Bearer $ACESS_TOKEN" -H "Content-Type: application/json;odata=verbose" --data-raw "{
  \"__metadata\": {
    \"type\": \"SP.Folder\"
  },
  \"ServerRelativeUrl\": \"$SHAREPOINT_CREATE_FOLDER_URL_JSON$MAIN_FOLDER_NAME/$VERSION_FOLDER\"
}"

# push the created jar file
curl -X POST "$SHAREPOINT_CREATE_FILE_ABSOLUTE_URL/_api/web/GetFolderByServerRelativeUrl('$SHAREPOINT_CREATE_FILE_PLACE_URL$MAIN_FOLDER_NAME/$VERSION_FOLDER/')/Files/Add(url='$SHAREPOINT_CREATE_FILE_PLACE_URL$MAIN_FOLDER_NAME/$VERSION_FOLDER/$CREATED_JAR_FILE',overwrite=true)" -H "Authorization: Bearer $ACESS_TOKEN" --data-binary @"$SHAREPOINT_JAR_FILE_PATH/$CREATED_JAR_FILE"
