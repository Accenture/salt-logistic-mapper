#!/bin/bash

# Variables
FILE=.apidoc.yml
APIDOC_PROJECT="https://swugit1.salt-solutions.de/sce-public/api-doc.git"
APIDOC_NAMESPACE="sce-public"
APIDOC_PROJECT_NAME="api-doc"
PIPELINE_SERVICE_USER=pipeline-service-sce
PIPELINE_SERVICE_EMAIL=pipeline-service-sce@salt-software.de


source .gitlab/common.sh

printf "Checking whether file .apidoc.yml is available\n"
if [ -f "$FILE" ]; then
  printf "%s exists\n" "$FILE"

  printf "Cloning projet sce-public/api-doc\n"
  # shellcheck disable=SC2001
  authenticated_address="https://$PIPELINE_SERVICE_USER:$PIPELINE_SERVICE_TOKEN@$(echo "$APIDOC_PROJECT" | sed -e 's#https://##')"
  git clone "$authenticated_address"
  PROJECT_NAME="$(echo "$APIDOC_PROJECT" | awk -F "/" '{print $NF}' | awk -F "." '{print $1}')"
  cd "$PROJECT_NAME" || exit

  printf "Adding apidoc.yaml to the api-doc project as tag file\n"
  SCE_PROJECT=$(echo "$CI_PROJECT_NAME" | tr '[:upper:]' '[:lower:]')
  SCE_NAMESPACE=$(echo "$CI_PROJECT_NAMESPACE" | tr '[:upper:]' '[:lower:]')
  mkdir -p "$SCE_NAMESPACE"/"$SCE_PROJECT"
  cp -f ../.apidoc.yml "$SCE_NAMESPACE"/"$SCE_PROJECT"/"$CI_COMMIT_TAG".yml

  printf "Adding apidoc.yaml to the api-doc project as latest file\n"
  cp -f ../.apidoc.yml "$SCE_NAMESPACE"/"$SCE_PROJECT"/latest.yml

  printf "Pushing back project sce-public/api-doc\n"
  git config --global user.email "$PIPELINE_SERVICE_EMAIL"
  git config --global user.name "$PIPELINE_SERVICE_USER"
  git add .
  git commit -m "Release API Doc for $SCE_NAMESPACE/$SCE_PROJECT:$CI_COMMIT_TAG"
  git push origin master

  printf "=== Complete ===\n"
  printf "Fix link: https://swagger-ui.apps.d4s.salt-solutions.de/?url=https://cors-anywhere.apps.d4s.salt-solutions.de/https://swugit1.salt-solutions.de/%s/%s/-/raw/master/%s/%s/%s.yml\n" "$APIDOC_NAMESPACE" "$APIDOC_PROJECT_NAME" "$SCE_NAMESPACE" "$SCE_PROJECT" "$CI_COMMIT_TAG"
  printf "Dynamic link: https://swagger-ui.apps.d4s.salt-solutions.de/?url=https://cors-anywhere.apps.d4s.salt-solutions.de/https://swugit1.salt-solutions.de/%s/%s/-/raw/master/%s/%s/latest.yml\n" "$APIDOC_NAMESPACE" "$APIDOC_PROJECT_NAME" "$SCE_NAMESPACE" "$SCE_PROJECT"
else
  echo "$FILE does not exist, skipping the job"
fi
