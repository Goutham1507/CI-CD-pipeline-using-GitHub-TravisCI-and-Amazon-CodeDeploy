echo "Enter the Stack name: "
read stack_name

echo "Initiating the script..."
echo "Checking if the stack already exists..."

if  aws cloudformation describe-stacks --stack-name $stack_name ; then

   echo -e "Stack already exists, terminating a stack..."
    
 else
    
   echo -e "Stack does not exist, creating a stack..."

    aws_domain_name=$(aws route53 list-hosted-zones --query 'HostedZones[0].Name' --output text)
    S3Bucketlambda=lambda.${aws_domain_name:0:-1}
    aws_bucket_list=$(aws s3api list-buckets --query "Buckets[].Name")

    for i in $aws_bucket_list
	do
		j=${i//\"}
		if [ $j == $S3Bucketlambda ]
		then	
			bucket_name=$j
		fi
	done	

    aws_response=$(aws cloudformation create-stack --stack-name $stack_name --template-body file://csye6225-cf-serverless.json --parameters ParameterKey=LambdaFunctionName,ParameterValue="index" ParameterKey=LambdaHandler,ParameterValue="index.handler" ParameterKey=domainName,ParameterValue=${aws_domain_name:0:-1} ParameterKey=S3Bucketlambda,ParameterValue=${bucket_name} ParameterKey=LambdaZipFile,ParameterValue="LambdaApp.zip" --on-failure DELETE)
    echo "Waiting for stack to be created ..."
    aws cloudformation wait stack-create-complete --stack-name $stack_name 

    echo "Stack Id = $aws_response created successfully!"
    
echo "Script completed successfully!"
fi