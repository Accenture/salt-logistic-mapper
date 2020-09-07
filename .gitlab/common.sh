#!/bin/bash

# Preflight Checks
printf "%s\n" "Running preflight checks..."
ERROR=0
if [ -z "$SBT_OPTS" ]; then
    printf "%s\n" "Variable SBT_OPTS is unset"; ERROR=1
fi
if [ -z "$ARTIFACTORY_REALM" ]; then
    printf "%s\n" "Variable ARTIFACTORY_REALM is unset"; ERROR=1
fi
if [ -z "$ARTIFACTORY_TOKEN" ]; then
    printf "%s\n" "Variable ARTIFACTORY_TOKEN is unset"; ERROR=1
fi
if [ -z "$ARTIFACTORY_USER" ]; then
    printf "%s\n" "Variable ARTIFACTORY_USER is unset"; ERROR=1
fi
if [ -z "$FOSSA_API_KEY" ]; then
    printf "%s\n" "Variable FOSSA_API_KEY is unset"; ERROR=1
fi
if [ -z "$SONAR_TOKEN" ]; then
    printf "%s\n" "Variable SONAR_TOKEN is unset"; ERROR=1
fi
if [ -z "$SONAR_URL" ]; then
    printf "%s\n" "Variable SONAR_URL is unset"; ERROR=1
fi
if [ -z "$OWNCLOUD_ENDPOINT" ]; then
    printf "%s\n" "Variable OWNCLOUD_ENDPOINT is unset"; ERROR=1
fi
if [ -z "$OWNCLOUD_USER" ]; then
    printf "%s\n" "Variable OWNCLOUD_USER is unset"; ERROR=1
fi
if [ -z "$OWNCLOUD_TOKEN" ]; then
    printf "%s\n" "Variable OWNCLOUD_TOKEN is unset"; ERROR=1
fi
if [ -z "$PIPELINE_SERVICE_TOKEN" ]; then
    printf "%s\n" "Variable PIPELINE_SERVICE_TOKEN is unset"; ERROR=1
fi
# shellcheck disable=SC2181
if [[ $ERROR -ne 0 ]]; then
    printf "%s\n" "Please set all variables mentioned above on GitLab CI/CD Settings"; exit 1
fi

function prepare-image-configuration() {
  EXECUTABLE_SCRIPT_NAME=$1
  printf "Prepare Image\n"
  sed -i "s+{{executable-script-name}}+$EXECUTABLE_SCRIPT_NAME+g" .gitlab/packager.txt
  cat .gitlab/packager.txt >> build.sbt
}

function prepare-plugins-common() {
  # sbt-dependency-graph
  sed -i.original '/sbt-dependency-graph/d' project/plugins.sbt
  printf "%s\n" "Add sbt-dependency-graph into plugins.sbt"
  printf "\n%s" 'addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "HEAD+20191104-1352")' >> project/plugins.sbt
  # sbt-assembly
  printf "\n%s" 'addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.10")' >> project/plugins.sbt

  # sbt-sonar-scanner-plugin
  sed -i.original '/sbt-sonar-scanner-plugin/d' project/plugins.sbt
  printf "%s\n" "Add sbt-sonar-scanner-plugin into plugins.sbt"
  printf "\n%s" 'addSbtPlugin("com.olaq" % "sbt-sonar-scanner-plugin" % "1.3.0")' >> project/plugins.sbt

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
  sed -i "s+{{project-name}}+$CI_PROJECT_NAME+g" .fossa.yml
  printf "%s\n" "Get the latest version from internet"
  curl -H 'Cache-Control: no-cache' https://raw.githubusercontent.com/fossas/fossa-cli/master/install.sh | bash
  fossa --version
}

function foss-analysis() {
  prepare-fossa
  printf "Perform Fossa Analysis\n"
  fossa analyze --verbose -T "SSG Logistik"
}

function prepare-sonarqube() {
  printf "Prepare SonarQube\n"
  sed -i "s+{{sonar-url}}+$SONAR_URL+g" .gitlab/sonar.txt
  sed -i "s+{{sonar-token}}+$SONAR_TOKEN+g" .gitlab/sonar.txt
  sed -i "s+{{sonar-project-key}}+$CI_PROJECT_NAME+g" .gitlab/sonar.txt
  cat .gitlab/sonar.txt >> build.sbt
}

function code-analysis() {
  prepare-sonarqube
  sbt -v sonar
}

function prepare-artifactory() {
  printf "Prepare Artifactory\n"
  sed -i "s+{{artifactory-realm}}+$ARTIFACTORY_REALM+g" .gitlab/publisher.txt
  sed -i "s+{{artifactory-user}}+$ARTIFACTORY_USER+g" .gitlab/publisher.txt
  sed -i "s+{{artifactory-token}}+$ARTIFACTORY_TOKEN+g" .gitlab/publisher.txt
  cat .gitlab/publisher.txt >> build.sbt
}

function code-push() {
  prepare-artifactory
  sbt -v publish
}

function foss-report() {
  prepare-fossa
  fossa test --timeout 600 --revision "$CI_COMMIT_SHA"
  printf "WARNING: FOSSA Reports will NOT be dowloaded and sent to smes-documentation repo, this process has to be done manually..."
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
  oc login https://cluster.d4s.salt-solutions.de:8443 --certificate-authority .gitlab/d4s-openshift-ca.crt --token="$SCE_OPENSHIFT_SERVICE_ACCOUNT_TOKEN"
  oc delete deploymentconfig "$SCE_PROJECT" -n "$OPENSHIFT_NAMESPACE" || true
  oc delete imagestream "$SCE_PROJECT" -n "$OPENSHIFT_NAMESPACE" || true
  oc delete service "$SCE_PROJECT" -n "$OPENSHIFT_NAMESPACE" || true
  printf "%s\n" "Fetching all port(s) needed from configmap service-$SCE_PROJECT (namespace: $OPENSHIFT_NAMESPACE)"
  SCESERVICES=$(curl -k -H "Authorization: Bearer $SCE_OPENSHIFT_SERVICE_ACCOUNT_TOKEN" https://cluster.d4s.salt-solutions.de:8443/api/v1/namespaces/"$OPENSHIFT_NAMESPACE"/configmaps/service-"$SCE_PROJECT" | jq '. | .data')
  if [[ -z "${SCESERVICES}" ]] || [[ $SCESERVICES == "null" ]]; then
    echo "ConfigMap service-$SCE_PROJECT is not set, creating service skipped"
  else
    # Service
    echo "ConfigMap service-$SCE_PROJECT is set, creating service"
    printf "%s\n" "Service: $SCESERVICES"
    cat <<EOF > service.yaml
apiVersion: v1
kind: Service
metadata:
  name: $SCE_PROJECT
spec:
  selector:
    deploymentconfig: $SCE_PROJECT
  sessionAffinity: None
  type: ClusterIP
  ports:
EOF
    for entry in $(echo "$SCESERVICES" | jq -r "to_entries|map(\"\(.)\")|.[]" ); do
      unset key && unset value;
      printf "%s\n" "Entry: $entry"
      key=$(echo "$entry" | jq '. | .key')
      value=$(echo "$entry" | jq '. | (.value)|tonumber')
    cat <<EOF >> service.yaml
  - name: $key
    port: $value
    targetPort: $value
    protocol: TCP
EOF
    done
    printf "\nContent of service.yaml: \n"
    cat service.yaml
    oc create -f service.yaml -n "$OPENSHIFT_NAMESPACE"
  fi


  # ImageStream
  cat <<EOF >> imagestream.yaml
apiVersion: image.openshift.io/v1
kind: ImageStream
metadata:
  labels:
    app: $SCE_PROJECT
  name: $SCE_PROJECT
  namespace: $OPENSHIFT_NAMESPACE
spec:
  lookupPolicy:
    local: false
  tags:
    - from:
        kind: DockerImage
        name: $CI_REGISTRY/$SCE_NAMESPACE/$CI_PROJECT_NAME:$IMAGE_TAG
      generation: 2
      importPolicy: {}
      name: $IMAGE_TAG
      referencePolicy:
        type: Source
EOF
  printf "\nContent of imagestream.yaml: \n"
  cat imagestream.yaml
  oc create -f imagestream.yaml -n "$OPENSHIFT_NAMESPACE"

  # Deployment
  cat <<EOF >> deployment.yaml
apiVersion: v1
kind: DeploymentConfig
metadata:
  labels:
    app: $SCE_PROJECT
  name: $SCE_PROJECT
  namespace: $OPENSHIFT_NAMESPACE
spec:
  minReadySeconds: 0
  paused: false
  replicas: 1
  revisionHistoryLimit: 2
  strategy:
    activeDeadlineSeconds: 21600
    recreateParams:
      timeoutSeconds: 600
    resources: {}
    type: Recreate
  template:
    metadata:
      labels:
        name: $SCE_PROJECT
    spec:
      containers:
      - args:
        - /bin/sh
        - -c
        envFrom:
        - configMapRef:
            name: sce-environment
        image: $CI_REGISTRY/$SCE_NAMESPACE/$CI_PROJECT_NAME:$IMAGE_TAG
        imagePullPolicy: IfNotPresent
        name: $SCE_PROJECT
EOF
  printf "\nContent of deployment.yaml: \n"
  cat deployment.yaml
  oc create -f deployment.yaml -n "$OPENSHIFT_NAMESPACE"
}

change_timezone_to_berlin() {
  rm /etc/localtime
  ln -s /usr/share/zoneinfo/Europe/Berlin /etc/localtime
  date
}