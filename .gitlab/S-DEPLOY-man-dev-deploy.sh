#!/bin/bash

source .gitlab/common.sh
preflight-check-service
deploy distribution-development-system master-snapshot
