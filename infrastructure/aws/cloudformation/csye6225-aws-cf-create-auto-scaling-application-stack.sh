echo "Enter the Stack name: "
read stack_name

echo "Initiating the script..."
echo "Checking if the stack already exists..."

if  aws cloudformation describe-stacks --stack-name $stack_name ; then

   echo -e "Stack already exists, terminating a stack..."
    
 else
    
   echo -e "Stack does not exist, creating a stack..."

   aws_domain_name=$(aws route53 list-hosted-zones --query 'HostedZones[0].Name' --output text)csye6225.com
   rs_domain_name=$(aws route53 list-hosted-zones --query 'HostedZones[0].Name' --output text)
   ssl_cert=$(aws acm list-certificates  --query 'CertificateSummaryList[0].CertificateArn' --output text)
   TravisUser="travis"

   aws_response=$(aws cloudformation create-stack --stack-name $stack_name --capabilities CAPABILITY_NAMED_IAM --template-body file://csye6225-cf-auto-scaling-application.json --parameters ParameterKey=fromPort1,ParameterValue="22" ParameterKey=toPort1,ParameterValue="22" ParameterKey=fromPort2,ParameterValue="80" ParameterKey=toPort2,ParameterValue="80" ParameterKey=fromPort3,ParameterValue="443" ParameterKey=toPort3,ParameterValue="443" ParameterKey=fromPort4,ParameterValue="8080" ParameterKey=toPort4,ParameterValue="8080" ParameterKey=fromDBPort,ParameterValue="5432" ParameterKey=toDBPort,ParameterValue="5432" ParameterKey=cidr,ParameterValue="0.0.0.0/0" ParameterKey=tableName,ParameterValue="csye6225" ParameterKey=dbName,ParameterValue="csye6225" ParameterKey=dbInstanceIdentifier,ParameterValue="csye6225-spring2018" ParameterKey=dbUsername,ParameterValue="csye6225master" ParameterKey=dbPassword,ParameterValue="csye6225password" ParameterKey=domainName,ParameterValue=${aws_domain_name} ParameterKey=RSDomainName,ParameterValue=${rs_domain_name} ParameterKey=SslCert,ParameterValue=${ssl_cert} ParameterKey=HostedZoneId,ParameterValue="Z1UJRXOUMOOFQ8" ParameterKey=TravisUser,ParameterValue=$TravisUser ParameterKey=CodeDeployAppName,ParameterValue="csye6225-fall2018" ParameterKey=AppDeploymentGroup,ParameterValue="csye6225-fall2018-DeploymentGroup" ParameterKey=TagKey,ParameterValue="csye6225-EC2-Key" ParameterKey=TagValue,ParameterValue="csye6225-EC2" ParameterKey=KeyName,ParameterValue="csye6225" --on-failure DELETE)

   echo "Waiting for stack to be created ..."
   aws cloudformation wait stack-create-complete --stack-name $stack_name 

   echo "Stack Id = $aws_response created successfully!"
    
 echo "Script completed successfully!"
fi
