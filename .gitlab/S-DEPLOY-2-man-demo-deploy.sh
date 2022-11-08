#!/bin/bash

source .gitlab/common.sh
preflight-check-service
deploy distribution-demo-system "$CI_COMMIT_TAG"