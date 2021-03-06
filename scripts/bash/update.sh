#!/bin/bash

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

$DIR/build.sh

echo "Updating lambda [eve-intel-load]"
aws lambda update-function-code \
  --function-name eve-intel-load \
  --zip-file fileb://./target/eve-intel.jar

echo "Updating lambda [eve-intel-load-request]"
aws lambda update-function-code \
  --function-name eve-intel-load-request \
  --zip-file fileb://./target/eve-intel.jar

$DIR/static.sh