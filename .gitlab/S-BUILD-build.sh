#!/bin/bash

source .gitlab/common.sh
prepare-plugins-service "$CI_PROJECT_NAME"
sbt -v compile docker:stage
