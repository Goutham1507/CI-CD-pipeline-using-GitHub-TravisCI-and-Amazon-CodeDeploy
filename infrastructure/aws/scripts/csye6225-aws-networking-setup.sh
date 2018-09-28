# to make file executable : chmod 777 or 755 file_name.sh
# to run the script ./file_name.sh
# sudo dnf install jq


# create-aws-vpc 
#variables used in script:

echo "Part 1.1 - Programming Infrastructure Using AWS Command Line Interface"
echo enter stack name
read STACK_NAME

echo "Collecting Available Zones Start.."
zone_response=$(aws ec2 describe-availability-zones --filters "Name=state,Values=available" "Name=region-name,Values=us-east-1")
#echo $zone_response
availabilityZone1=$(echo -e "$zone_response" |  /usr/bin/jq '.AvailabilityZones[0].ZoneName' | tr -d '"')
availabilityZone2=$(echo -e "$zone_response" |  /usr/bin/jq '.AvailabilityZones[1].ZoneName' | tr -d '"')
availabilityZone3=$(echo -e "$zone_response" |  /usr/bin/jq '.AvailabilityZones[2].ZoneName' | tr -d '"')

vpcName="$STACK_NAME-VPC"
vpcCidrBlock="10.0.0.0/16"

subnetName1="$STACK_NAME-Public_Subnet_1"
subNetCidrBlock1="10.0.0.0/24"
# availabilityZone1="us-east-1a"

subnetName2="$STACK_NAME-Public_Subnet_2"
subNetCidrBlock2="10.0.1.0/24"
# availabilityZone2="us-east-1b"

subnetName3="$STACK_NAME-Public_Subnet_1"
subNetCidrBlock3="10.0.2.0/24"
# availabilityZone3="us-east-1c"

gatewayName="$STACK_NAME-Gateway"

routeTableName="$STACK_NAME-Route-Table"

subNetCidrBlock4="10.0.3.0/24"
subNetCidrBlock5="10.0.4.0/24"
subNetCidrBlock6="10.0.5.0/24"

destinationCidrBlock="0.0.0.0/0"

echo "Step 1 : Creating VPC Start..."
#create vpc with cidr block /16
aws_response=$(aws ec2 create-vpc --cidr-block "$vpcCidrBlock" --output json)
#echo $aws_response
vpcId=$(echo -e "$aws_response" |  /usr/bin/jq '.Vpc.VpcId' | tr -d '"')
aws ec2 create-tags --resources "$vpcId" --tags Key=Name,Value="$vpcName"

echo "vpc id : $vpcId vpc name :  $vpcName"


echo "Step 2.1 : Creating Private Subnets Start..."

subnet_response=$(aws ec2 create-subnet --vpc-id "$vpcId" --cidr-block "$subNetCidrBlock1" --availability-zone "$availabilityZone1")
subnetId1=$(echo -e "$subnet_response" |  /usr/bin/jq '.Subnet.SubnetId' | tr -d '"')
aws ec2 create-tags --resources "$subnetId1" --tags Key=Name,Value="$vpcName-Private-Subnet-1"
echo "Private subnet 1 created >> subnet ID : $subnetId1"


subnet_response=$(aws ec2 create-subnet --vpc-id "$vpcId" --cidr-block "$subNetCidrBlock2" --availability-zone "$availabilityZone2")
subnetId2=$(echo -e "$subnet_response" |  /usr/bin/jq '.Subnet.SubnetId' | tr -d '"')
aws ec2 create-tags --resources "$subnetId2" --tags Key=Name,Value="$vpcName-Private-Subnet-2"
echo "Private subnet 2 created >> subnet ID : $subnetId2"

subnet_response=$(aws ec2 create-subnet --vpc-id "$vpcId" --cidr-block "$subNetCidrBlock3" --availability-zone "$availabilityZone3")
subnetId3=$(echo -e "$subnet_response" |  /usr/bin/jq '.Subnet.SubnetId' | tr -d '"')
aws ec2 create-tags --resources "$subnetId3" --tags Key=Name,Value="$vpcName-Private-Subnet-3"
echo "Private subnet 3 created >> subnet ID : $subnetId3"

echo "Step 2.2 : Creating Public Subnets Start.."

subnet_response=$(aws ec2 create-subnet --vpc-id "$vpcId" --cidr-block "$subNetCidrBlock4" --availability-zone "$availabilityZone1")
subnetId4=$(echo -e "$subnet_response" |  /usr/bin/jq '.Subnet.SubnetId' | tr -d '"')
aws ec2 modify-subnet-attribute --subnet-id "$subnetId4" --map-public-ip-on-launch
aws ec2 create-tags --resources "$subnetId4" --tags Key=Name,Value="$vpcName-Public-Subnet-1"
echo "Public subnet 1 created >> subnet ID : $subnetId4"

subnet_response=$(aws ec2 create-subnet --vpc-id "$vpcId" --cidr-block "$subNetCidrBlock5" --availability-zone "$availabilityZone2")
subnetId5=$(echo -e "$subnet_response" |  /usr/bin/jq '.Subnet.SubnetId' | tr -d '"')
aws ec2 modify-subnet-attribute --subnet-id "$subnetId5" --map-public-ip-on-launch
aws ec2 create-tags --resources "$subnetId5" --tags Key=Name,Value="$vpcName-Public-Subnet-2"
echo "Public subnet 2 created >> subnet ID : $subnetId5"

subnet_response=$(aws ec2 create-subnet --vpc-id "$vpcId" --cidr-block "$subNetCidrBlock6" --availability-zone "$availabilityZone3")
subnetId6=$(echo -e "$subnet_response" |  /usr/bin/jq '.Subnet.SubnetId' | tr -d '"')
aws ec2 modify-subnet-attribute --subnet-id "$subnetId6" --map-public-ip-on-launch
aws ec2 create-tags --resources "$subnetId6" --tags Key=Name,Value="$vpcName-Public-Subnet-3"
echo "Public subnet 3 created >> subnet ID : $subnetId6"

echo "Step 3 : Create Internet Gateway resource Start..."
gateway_respponse=$(aws ec2 create-internet-gateway)
InternetGatewayId=$(echo -e "$gateway_respponse" |  /usr/bin/jq '.InternetGateway.InternetGatewayId' | tr -d '"')
aws ec2 create-tags --resources "$InternetGatewayId" --tags Key=Name,Value="$gatewayName"
echo "Internet Gateway created >> InternetGatewayId : $InternetGatewayId"

echo "Step 4 : Attach the Internet Gateway to the created VPC Start..."
attach_gateway_respponse=$(aws ec2 attach-internet-gateway --internet-gateway-id "$InternetGatewayId" --vpc-id "$vpcId")
echo $attach_gateway_respponse

echo "Step 5.1 : Create a public Route Table Start..."
routeTable_response=$(aws ec2 create-route-table --vpc-id "$vpcId")
RouteTableId=$(echo -e "$routeTable_response" |  /usr/bin/jq '.RouteTable.RouteTableId' | tr -d '"')
aws ec2 create-tags --resources "$RouteTableId" --tags Key=Name,Value="$routeTableName"
echo "Route Table created >> RouteTableId : $RouteTableId"

echo "Step 5.2 : Attach all subnets created above to the route table Start..."

echo "Attaching Subnet 1 Start..."
res1=$(aws ec2 associate-route-table --route-table-id "$RouteTableId" --subnet-id "$subnetId1")
echo $res1

echo "Attaching Subnet 2 Start..."
res2=$(aws ec2 associate-route-table --route-table-id "$RouteTableId" --subnet-id "$subnetId2")
echo $res2

echo "Attaching Subnet 3 Start..."
res3=$(aws ec2 associate-route-table --route-table-id "$RouteTableId" --subnet-id "$subnetId3")
echo $res3

echo "Attaching Subnet 4 Start..."
res1=$(aws ec2 associate-route-table --route-table-id "$RouteTableId" --subnet-id "$subnetId4")
echo $res1

echo "Attaching Subnet 5 Start..."
res2=$(aws ec2 associate-route-table --route-table-id "$RouteTableId" --subnet-id "$subnetId5")
echo $res2

echo "Attaching Subnet 6 Start..."
res3=$(aws ec2 associate-route-table --route-table-id "$RouteTableId" --subnet-id "$subnetId6")
echo $res3


echo "Step 6 : Create a public route in the public route table created above with destination CIDR block 0.0.0.0/0 and internet gateway creted above as the target Start..."

res4=$(aws ec2 create-route --route-table-id "$RouteTableId" --destination-cidr-block "$destinationCidrBlock" --gateway-id "$InternetGatewayId")
echo $res4
echo "Process Completed..."
