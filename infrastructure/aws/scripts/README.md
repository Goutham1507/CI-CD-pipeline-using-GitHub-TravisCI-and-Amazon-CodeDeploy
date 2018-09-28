# Task : Infrastructure setup

1. Create a Virtual Private Cloud (VPC).

2. Create subnets in your VPC. You must create 3 subnets, each in different availability zone in the same region under same VPC.

3. Create Internet Gateway resource.

4. Attach the Internet Gateway to the created VPC.

5. Create a public Route Table. Attach all subnets created above to the route table.

6. Create a public route in the public route table created above with destination CIDR block 0.0.0.0/0 and internet gateway creted above as the target.



# Objective:

1. Install and setup AWS command line interface.

2. Create shell script csye6225-aws-networking-setup.sh to create and configure required networking resources using AWS CLI. Script should take all required values as parameter and should not contain environment specific hardcoded values.

3. Create shell script csye6225-aws-networking-teardown.sh to delete networking resources using AWS CLI. Script should take all required values as parameter and should not contain environment specific hardcoded values.

4. Should one of the resource creation fail, your script should print out proper error message and exit gracefully. Your scripts are not required to rollback already created resources.

5. Resources are only created if the previous command is successful.



# How to use script:

To Setup a new infrastructure : 

Run script : csye6225-aws-networking-setup.sh

Script will ask for VPC name. Provide VPC name and complete infrastructure is created by given name and a VPC-id associated with it.


# To Delete infrastructure:

Run script : csye6225-aws-networking-teardown.sh

Script will ask for VPC-id. Provide VPC-id. If provided VPC-id matches with any of the available VPC-id, it will delete all the dependecied associated with VPC and then VPC itself. 
