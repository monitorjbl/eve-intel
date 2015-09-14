#!/bin/bash

mvn clean package \
  -Daws.accessKey="`cat ~/.aws/credentials | grep aws_access_key | sed 's/.*= //'`" \
  -Daws.secretKey="`cat ~/.aws/credentials | grep aws_secret_access_key | sed 's/.*= //'`"
