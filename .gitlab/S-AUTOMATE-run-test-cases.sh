#!/bin/bash

# Angela end point, including protocol (http or https) and port
ENDPOINT="http://sce-angela.apps.d4s.salt-solutions.de:80" # Example: http://localhost:81

echo "Triggering test case id [$TESTCASE_ID]..."
RESULT=$(curl "$ENDPOINT/start-test-case?id=$TESTCASE_ID")
if [ "$RESULT" = "OK" ]; then
  echo "Test case [$TESTCASE_ID] succeeded"
  exit 0
else
  echo "Test case [$TESTCASE_ID] failed - Result: $RESULT"
  exit 1
fi