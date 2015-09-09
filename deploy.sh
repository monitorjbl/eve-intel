aws lambda create-function \
--region us-east-1 \
--function-name eve-intel-load \
--zip-file fileb://./target/eve-intel.jar \
--role arn:aws:iam::904678522911:role/lambda_basic_execution  \
--handler com.thundermoose.eveintel.Loader::trigger \
--runtime java8 \
--timeout 60 \
--memory-size 512

aws lambda create-function \
--region us-east-1 \
--function-name eve-intel-load-request \
--zip-file fileb://./target/eve-intel.jar \
--role arn:aws:iam::904678522911:role/lambda_basic_execution  \
--handler com.thundermoose.eveintel.Requester::trigger \
--runtime java8 \
--timeout 30 \
--memory-size 512

mvn clean package && \
aws lambda update-function-code \
--function-name eve-intel-load \
--zip-file fileb://./target/eve-intel.jar &&
aws lambda update-function-code \
--function-name eve-intel-load-request \
--zip-file fileb://./target/eve-intel.jar