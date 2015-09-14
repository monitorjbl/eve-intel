#!/bin/bash

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

$DIR/build.sh

echo "Creating lambda [eve-intel-load]"
aws lambda create-function \
--region us-east-1 \
--function-name eve-intel-load \
--zip-file fileb://./target/eve-intel.jar \
--role arn:aws:iam::904678522911:role/lambda_basic_execution  \
--handler com.thundermoose.eveintel.Loader::trigger \
--runtime java8 \
--timeout 60 \
--memory-size 512

echo "Creating lambda [eve-intel-load-request]"
aws lambda create-function \
--region us-east-1 \
--function-name eve-intel-load-request \
--zip-file fileb://./target/eve-intel.jar \
--role arn:aws:iam::904678522911:role/lambda_basic_execution  \
--handler com.thundermoose.eveintel.Requester::trigger \
--runtime java8 \
--timeout 30 \
--memory-size 512

$DIR/static.sh
