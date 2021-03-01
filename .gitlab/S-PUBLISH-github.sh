#!/bin/bash

# Variables
PROJECT="https://swugit1.salt-solutions.de/sce/common/mapper.git"
PROJECT_NAME="mapper"
GITHUB_COMMIT_MESSAGE="Publish in GitHub."

source .gitlab/common.sh

printf "Project name: %s\n" "$PROJECT_NAME"
printf "GIT repo: %s\n" "$PROJECT"

printf "Clone git repository...\n"
gitlab_address="https://$PIPELINE_SERVICE_USER:$PIPELINE_SERVICE_TOKEN@$(echo "$PROJECT" | sed -e 's#https://##')"
git clone "$gitlab_address"

printf "Change directory to $PROJECT_NAME...\n"
cd "$PROJECT_NAME" || exit

printf "Delete pipeline tags - if exists...\n"
for tag in $(git tag | grep pipeline); do
  git tag -d "$tag"
  git push --delete origin "$tag"
done

printf "Delete .gitlab-ci, README_INTERN.md files and gitlab folder...\n"
git rm .gitlab-ci.yml
git rm README_INTERN.md
git rm -rf .gitlab

github_address="https://$GITHUB_USER:$GITHUB_PASSWORD@$(echo "$GITHUB_URL" | sed -e 's#https://##')"
printf "Copy into $github_address\n"

git config --global user.email "$GITHUB_USER_EMAIL"
git config --global user.name "$GITHUB_USER"
git remote add github "$github_address"
git commit -m "$GITHUB_COMMIT_MESSAGE"
git push github master --tags -f

cd ..
printf "\n"
printf "==============================================================================================================\n"
printf "Done, The $PROJECT_NAME project has successfully into GitHub($GITHUB_URL) copied! \n"
printf "==============================================================================================================\n"