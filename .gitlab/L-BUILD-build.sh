#!/bin/bash

source .gitlab/common.sh
prepare-plugins-library
sbt -v compile
