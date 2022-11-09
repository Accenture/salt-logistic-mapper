#!/bin/sh

SCE_PROJECT=$(echo "$CI_PROJECT_NAME" | tr '[:upper:]' '[:lower:]')
SCE_NAMESPACE=$(echo "$CI_PROJECT_NAMESPACE" | tr '[:upper:]' '[:lower:]')

printf "======================================================================================\n"
printf "NOTE: This job is mostly done within 3 minutes or less, if it timed out, please RETRY.\n"
printf "      If retry did not help, contact wirayudha.rohandi@salt-software.de\n"
printf "======================================================================================\n"
printf "Prepare Image\n"
sed -i "s+{{executable-script-name}}+$SCE_PROJECT+g" .gitlab/packager.txt
cat .gitlab/packager.txt >> build.sbt

echo "{\"auths\":{\"$CI_REGISTRY\":{\"username\":\"$CI_REGISTRY_USER\",\"password\":\"$CI_REGISTRY_PASSWORD\"}}}" > /kaniko/.docker/config.json
kaniko-build -v=info --context "$CI_PROJECT_DIR"/target/docker/stage --dockerfile "$CI_PROJECT_DIR"/target/docker/stage/Dockerfile --destination "$CI_REGISTRY"/"$SCE_NAMESPACE"/"$SCE_PROJECT":"$CI_COMMIT_REF_NAME"
