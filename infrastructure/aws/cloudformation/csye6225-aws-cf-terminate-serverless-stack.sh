echo "Enter the Stack name: "

read stack_name

echo "Initiating the script..."

echo "Checking if the stack already exists..."

if  aws cloudformation describe-stacks --stack-name $stack_name ; then


	echo -e "\nStack already exists, terminating a stack..."

	aws cloudformation delete-stack --stack-name $stack_name


	echo "\nWaiting for stack to be terminated ..."

	aws cloudformation wait stack-delete-complete --stack-name $stack_name
	 
	 echo "\nStack terminated successfully!"

else
		echo -e "\nStack does not exist!"
fi
