#!/bin/bash

source .gitlab/common.sh

# Fetch all environments needed from configmap sce-environment-unit-test
ENVIRONMENTS=$(curl -k -H "Authorization: Bearer $SCE_OPENSHIFT_SERVICE_ACCOUNT_TOKEN" https://cluster.d4s.salt-solutions.de:8443/api/v1/namespaces/logistic-integration/configmaps/sce-environment-unit-test | jq '. | .data')
echo "Setting up environment variables from openshift's configmap (logistic-integration/configmaps/sce-environment-unit-test):"
echo "$ENVIRONMENTS"
for env in $(echo "$ENVIRONMENTS" | jq -r "to_entries|map(\"\(.key)=\(.value|tostring)\")|.[]" ); do
  export "${env?}"
done

unit-test
