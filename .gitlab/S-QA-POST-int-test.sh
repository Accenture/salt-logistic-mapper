#!/bin/bash

source .gitlab/common.sh
change_timezone_to_berlin

printf "Reduce java security, Reduce DH (Diffie Hellman) key from 2048 to 1024\n"
sed -i "s/DH keySize < 2048, /DH keySize < 1024, /" /etc/crypto-policies/back-ends/java.config

# Fetch all environments needed from configmap sce-environment
ENVIRONMENTS=$(curl -k -H "Authorization: Bearer $DISTRIBUTION_PIPELINE_MANAGER_TOKEN" https://api.nur-okd.salt-solutions.de:6443/api/v1/namespaces/distribution-development-system/configmaps/sce-environment | jq '. | .data')
echo "Setting up environment variables from openshift's configmap (sce-environment):"
echo "$ENVIRONMENTS"
for env in $(echo "$ENVIRONMENTS" | jq -r "to_entries|map(\"\(.key)=\(.value|tostring)\")|.[]" ); do
  export "${env?}"
done

printf "Prepare JUnit Interface\n"
cat .gitlab/junit-interface.txt >> build.sbt
sbt 'testOnly de.salt.*IntegrationSpec'