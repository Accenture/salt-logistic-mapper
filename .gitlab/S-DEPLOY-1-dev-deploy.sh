#!/bin/bash

source .gitlab/common.sh
deploy logistic-dev "$CI_COMMIT_TAG"
