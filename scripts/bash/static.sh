#!/bin/bash

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

echo "Syncing static content"
aws s3 sync $DIR/../../src/main/webapp/ s3://monitorjbl.io/eve-intel/