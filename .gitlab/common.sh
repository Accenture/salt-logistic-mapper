#!/bin/bash

# Preflight Checks
printf "%s\n" "Running preflight checks..."
ERROR=0
if [ -z "$SBT_OPTS" ]; then
    printf "%s\n" "Variable SBT_OPTS is unset"; ERROR=1
fi
if [ -z "$NEXUS_TOKEN" ]; then
    printf "%s\n" "Variable NEXUS_TOKEN is unset"; ERROR=1
fi
if [ -z "$NEXUS_USER" ]; then
    printf "%s\n" "Variable NEXUS_USER is unset"; ERROR=1
fi
if [ -z "$FOSSA_API_KEY" ]; then
    printf "%s\n" "Variable FOSSA_API_KEY is unset"; ERROR=1
fi
if [ -z "$PIPELINE_SERVICE_TOKEN" ]; then
    printf "%s\n" "Variable PIPELINE_SERVICE_TOKEN is unset"; ERROR=1
fi
# shellcheck disable=SC2181
if [[ $ERROR -ne 0 ]]; then
    printf "%s\n" "Please set all variables mentioned above on GitLab CI/CD Settings"; exit 1
fi

function preflight-check-service() {
  if [ -z "$DISTRIBUTION_PIPELINE_MANAGER_TOKEN" ]; then
      printf "%s\n" "Variable DISTRIBUTION_PIPELINE_MANAGER_TOKEN is unset"; exit 1
  fi
}

function prepare-image-configuration() {
  EXECUTABLE_SCRIPT_NAME=$1
  printf "Prepare Image\n"
  sed -i "s+{{executable-script-name}}+$EXECUTABLE_SCRIPT_NAME+g" .gitlab/packager.txt
  cat .gitlab/packager.txt >> build.sbt

  printf "Checking whether special packager is needed\n"
  FILE=packager-$EXECUTABLE_SCRIPT_NAME.txt
  if test -f .gitlab/"$FILE"; then
    echo "$FILE exists, applying these additional packager configs"
    cat .gitlab/"$FILE" >> build.sbt
  fi
}

function prepare-plugins-common() {
  # sbt-dependency-graph
  sed -i.original '/sbt-dependency-graph/d' project/plugins.sbt
  printf "%s\n" "Add sbt-dependency-graph into plugins.sbt"
  printf "\n%s" 'libraryDependencies += ("net.virtual-void" % "sbt-dependency-graph_2.12_1.0" % "HEAD+20191104-1352")' >> project/plugins.sbt
  # sbt-assembly
  printf "\n%s" 'addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.10")' >> project/plugins.sbt

  # JUnit 5 Jupiter sbt interface
  sed -i.original '/sbt-jupiter-interface/d' project/plugins.sbt
  printf "%s\n" "Add JUnit 5 Jupiter sbt interface into plugins.sbt"
  printf "\n%s" 'addSbtPlugin("net.aichler" % "sbt-jupiter-interface" % "0.8.3")' >> project/plugins.sbt
}

function prepare-plugins-library() {
  prepare-plugins-common
}

function prepare-plugins-service() {
  PROJECT_NAME=$1
  prepare-image-configuration "$PROJECT_NAME"

  prepare-plugins-common

  # sbt-native-packager
  sed -i.original '/sbt-native-packager/d' project/plugins.sbt
  printf "%s\n" "Add sbt-native-packager into plugins.sbt"
  printf "\n%s" 'addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.7.2")' >> project/plugins.sbt
}

function unit-test() {
  printf "Change timezone to Europe/Berlin\n"
  change_timezone_to_berlin

  printf "Prepare JUnit Interface\n"
  cat .gitlab/junit-interface.txt >> build.sbt
  sbt coverage 'testOnly de.salt.*UnitSpec' coverageReport
}

function prepare-fossa() {
  printf "Prepare FOSSA\n"
  cp -f .gitlab/fossa.yml .fossa.yml
  sed -i "s+{{project-name}}+${CI_PROJECT_PATH//\//-}+g" .fossa.yml
  printf "%s\n" "Get the latest version from internet"
  curl -H 'Cache-Control: no-cache' https://raw.githubusercontent.com/fossas/fossa-cli/master/install.sh | bash
  fossa --version
}

function foss-analysis() {
  prepare-fossa
  printf "Perform Fossa Analysis\n"
  fossa analyze --verbose -T "SSG Logistik"
}

function prepare-nexus() {
  printf "Prepare Nexus\n"
  sed -i "s+{{nexus-user}}+$NEXUS_USER+g" .gitlab/publisher.txt
  sed -i "s+{{nexus-token}}+$NEXUS_TOKEN+g" .gitlab/publisher.txt
  cat .gitlab/publisher.txt >> build.sbt
}

function code-push() {
  prepare-nexus
  sbt -v publish
}

function foss-report() {
  prepare-fossa
  fossa test --timeout 600 --revision "$CI_COMMIT_SHA"
  EXIT_CODE=$?
  printf "WARNING: FOSSA Reports will NOT be dowloaded and sent to smes-documentation repo, this process has to be done manually..."
  exit $EXIT_CODE
}

function check-deploy-prerequisite() {
  ERROR=0
  if [ -z "$SCE_OPENSHIFT_SERVICE_ACCOUNT_TOKEN" ]; then
    printf "%s\n" "Variable SCE_OPENSHIFT_SERVICE_ACCOUNT_TOKEN is unset"; ERROR=1
  fi
  # shellcheck disable=SC2181
  if [[ $ERROR -ne 0 ]]; then
    printf "%s\n" "Please set all variables above on GitLab CI/CD Settings"; exit 1
  fi
}

function deploy() {
  OPENSHIFT_NAMESPACE=$1
  IMAGE_TAG=$2
  SCE_PROJECT=$(echo "$CI_PROJECT_NAME" | tr '[:upper:]' '[:lower:]' | tr '\_' '\-')
  SCE_NAMESPACE=$(echo "$CI_PROJECT_NAMESPACE" | tr '[:upper:]' '[:lower:]')
  oc login $CLUSTER_ADDRESS --token=$DISTRIBUTION_PIPELINE_MANAGER_TOKEN
  oc delete deployment $SCE_PROJECT -n $OPENSHIFT_NAMESPACE || true
  oc delete service $SCE_PROJECT -n $OPENSHIFT_NAMESPACE || true

  # Service Creating
  echo "Fetching all port(s) needed from configmap service-$SCE_PROJECT (namespace: $OPENSHIFT_NAMESPACE)"
  SCESERVICES=`curl -k -H "Authorization: Bearer $DISTRIBUTION_PIPELINE_MANAGER_TOKEN" https://api.nur-okd.salt-solutions.de:6443/api/v1/namespaces/$OPENSHIFT_NAMESPACE/configmaps/service-$SCE_PROJECT | jq '. | .data'`
  echo "Services: $SCESERVICES"
  if [ "$SCESERVICES" = "null" ]; then
  echo "ConfigMap service-$SCE_PROJECT is not set, creating service skipped"
  else
  echo "ConfigMap service-$SCE_PROJECT is set, creating service"
  cat <<EOF > service.yaml
  apiVersion: v1
  kind: Service
  metadata:
    name: $SCE_PROJECT
  spec:
    selector:
      app: $SCE_PROJECT
    type: ClusterIP
    ports:
EOF

  for entry in $(echo $SCESERVICES | jq -r "to_entries|map(\"\(.)\")|.[]" ); do
  unset KEY && unset VALUE;
  echo $entry;
  KEY=`echo $entry | jq '. | .key'`;
  VALUE=`echo $entry | jq '. | (.value)|tonumber'`;
  cat <<EOF2 >> service.yaml
    - name: $KEY
      port: $VALUE
      protocol: TCP
EOF2
  done
  cat service.yaml
  oc apply -f service.yaml -n $OPENSHIFT_NAMESPACE
  rm service.yaml
  fi
  
  cat <<EOF3 > deployment.yaml
  kind: Deployment
  apiVersion: apps/v1
  metadata:
    name: $SCE_PROJECT
  spec:
    replicas: 1
    selector:
      matchLabels:
        app: $SCE_PROJECT
    template:
      metadata:
        labels:
          app: $SCE_PROJECT
      spec:
        imagePullSecrets:
          - name: "swugit1-image-puller"
        serviceAccountName: anyuid
        containers:
          - name: $SCE_PROJECT
            image: $CI_REGISTRY/$SCE_NAMESPACE/$SCE_PROJECT:$IMAGE_TAG
            imagePullPolicy: IfNotPresent
            envFrom:
              - configMapRef:
                  name: sce-environment
            resources:
              requests:
                cpu: "50m"
                memory: "250Mi"
              limits:
                cpu: "2000m"
                memory: "4000Mi"
            volumeMounts:
              - name: tls-bundle
                mountPath: /etc/pki/ca-trust/extracted/pem
        restartPolicy: Always
        volumes:
          - name: tls-bundle
            configMap:
              name: redhat-ca-bundle
EOF3
  printf "\nContent of deployment.yaml: \n"
  cat deployment.yaml
  oc create -f deployment.yaml -n "$OPENSHIFT_NAMESPACE"
  rm deployment.yaml
}

change_timezone_to_berlin() {
  rm /etc/localtime
  ln -s /usr/share/zoneinfo/Europe/Berlin /etc/localtime
  date
}
