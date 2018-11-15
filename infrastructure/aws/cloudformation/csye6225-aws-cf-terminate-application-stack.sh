echo "Enter the Stack name: "

read stack_name

echo "Initiating the script..."

echo "Checking if the stack already exists..."

if  aws cloudformation describe-stacks --stack-name $stack_name ; then


	echo -e "\nStack already exists, terminating a stack..."

	aws cloudformation delete-stack --stack-name $stack_name


	echo "\nWaiting for stack to be terminated ..."

	aws_domain_name=$(aws route53 list-hosted-zones --query 'HostedZones[0].Name' --output text)csye6225.com
	aws_bucket_list=$(aws s3api list-buckets --query "Buckets[].Name")
	
	for i in $aws_bucket_list
	do
		j=${i//\"}
		j=${i//\,}
		if [ $j == $aws_domain_name ]
		then	
			bucket_name=$j
		fi
	done	
	echo $bucket_name
	aws s3 rb s3://$bucket_name --force
	aws cloudformation wait stack-delete-complete --stack-name $stack_name
	 
	 echo "\nStack terminated successfully!"

else
		echo -e "\nStack does not exist!"
fi
