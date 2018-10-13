echo "Enter the Stack name: "
read stack_name

echo "Initiating the script..."
echo "Checking if the stack already exists..."

if  aws cloudformation describe-stacks --stack-name $stack_name ; then

   echo -e "Stack already exists, terminating a stack..."
    
 else
    
   echo -e "Stack does not exist, creating a stack..."

   aws_domain_name=$(aws route53 list-hosted-zones --query 'HostedZones[0].Name' --output text)
   aws_response=$(aws cloudformation create-stack --stack-name $stack_name --template-body file://csye6225-cf-application.json --parameters ParameterKey=fromPort1,ParameterValue="22" ParameterKey=toPort1,ParameterValue="22" ParameterKey=fromPort2,ParameterValue="80" ParameterKey=toPort2,ParameterValue="80" ParameterKey=fromPort3,ParameterValue="443" ParameterKey=toPort3,ParameterValue="443" ParameterKey=fromDBPort,ParameterValue="5432" ParameterKey=toDBPort,ParameterValue="5432" ParameterKey=cidr,ParameterValue="0.0.0.0/0" ParameterKey=tableName,ParameterValue="csye6225" ParameterKey=dbName,ParameterValue="csye6225" ParameterKey=dbInstanceIdentifier,ParameterValue="csye6225-spring2018" ParameterKey=dbUsername,ParameterValue="csye6225master" ParameterKey=dbPassword,ParameterValue="csye6225password" ParameterKey=domainName,ParameterValue=${aws_domain_name:0:-1} --on-failure DELETE)

   echo "Waiting for stack to be created ..."
   aws cloudformation wait stack-create-complete --stack-name $stack_name 

   echo "Stack Id = $aws_response created successfully!"
    
 echo "Script completed successfully!"
fi
