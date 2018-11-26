echo "Enter the Stack name: "
read stack_name

echo "Initiating the script..."
echo "Checking if the stack already exists..."

if  aws cloudformation describe-stacks --stack-name $stack_name ; then

   echo -e "Stack already exists, terminating a stack..."
    
 else
    
   echo -e "Stack does not exist, creating a stack..."

aws_domain_name=$(aws route53 list-hosted-zones --query 'HostedZones[0].Name' --output text)
S3BucketCodeDeployDomain="code-deploy.${aws_domain_name:0:-1}"
S3BucketApp="${aws_domain_name}csye6225.com"

   aws_response=$(aws cloudformation create-stack --stack-name $stack_name --capabilities CAPABILITY_NAMED_IAM --template-body file://csye6225-cf-cicd.json --parameters   ParameterKey=S3BucketCodeDeployDomain,ParameterValue=$S3BucketCodeDeployDomain ParameterKey=S3BucketApp,ParameterValue=$S3BucketApp ParameterKey=TagKey,ParameterValue="csye6225-EC2-Key" ParameterKey=TagValue,ParameterValue="csye6225-EC2" ParameterKey=dynamoDBTable,ParameterValue="csye6225" ParameterKey=SesDomainName,ParameterValue=${aws_domain_name:0:-1} --on-failure DELETE)

   echo "Waiting for stack to be created ..."
   aws cloudformation wait stack-create-complete --stack-name $stack_name 

   echo "Stack Id = $aws_response created successfully!"
    
 echo "Script completed successfully!"
fi
