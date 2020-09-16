#!/bin/bash

source .gitlab/common.sh
change_timezone_to_berlin

printf "Reduce java security, Reduce DH (Diffie Hellman) key from 2048 to 1024\n"
sed -i "s/DH keySize < 2048, /DH keySize < 1024, /" /etc/crypto-policies/back-ends/java.config

# Fetch all environments needed from configmap sce-environment
ENVIRONMENTS=$(curl -k -H "Authorization: Bearer $SCE_OPENSHIFT_SERVICE_ACCOUNT_TOKEN" https://cluster.d4s.salt-solutions.de:8443/api/v1/namespaces/logistic-integration/configmaps/sce-environment | jq '. | .data')
echo "Setting up environment variables from openshift's configmap (logistic-integration/configmaps/sce-environment):"
echo "$ENVIRONMENTS"
for env in $(echo "$ENVIRONMENTS" | jq -r "to_entries|map(\"\(.key)=\(.value|tostring)\")|.[]" ); do
  export "${env?}"
done

printf "Prepare JUnit Interface\n"
cat .gitlab/junit-interface.txt >> build.sbt
sbt 'testOnly de.salt.*IntegrationSpec'