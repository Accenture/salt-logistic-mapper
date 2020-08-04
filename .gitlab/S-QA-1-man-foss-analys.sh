#!/bin/bash

source .gitlab/common.sh
prepare-plugins-service
foss-analysis
printf "Watiting for results...\n"
fossa test --timeout 600 --revision "$CI_COMMIT_SHA"
