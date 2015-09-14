#!/bin/bash

echo "Syncing static content"
aws s3 sync src/main/webapp/ s3://eve-intel/