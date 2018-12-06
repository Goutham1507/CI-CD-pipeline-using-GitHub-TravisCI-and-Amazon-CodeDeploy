A. Team Members
    Srno. Name              Email
    1.    Vaibhavi Kamani   kamani.v@husky.neu.edu
    2.    Jigar Dedhia      dedhia.j@husky.neu.edu
    3.    Hiren Shah        shah.hi@husky.neu.edu
    4.    Mayank Gangrade   gangrade.m@husky.neu.edu 
    

B. Pre-requisites
1. You need to have "Postman" installed

C. Build and Deploy instructions
1. Clone the git repository.
2. Traverse to the folder /csye6225-fall2018/webapp


D. Instructions to run application

Make Unauthenticated HTTP Request
Execute following command on your bash shell

$ curl http://{EC2_hostname}:8080

Expected Response:

{"message":"you are not logged in!!!"}

Authenticate for HTTP Request
Execute following command on your bash shell

$ curl -u user:password http://{EC2_hostname}:8080

where user is the username and password is the password.
Expected Response:

{"message":"you are logged in. current time is Tue Sep 19 20:03:49 EDT 2017"}


Execute following command on your bash shell

$ curl -u user:password http://{EC2_hostname}:8080/user/register

where user is the username and password is the password.
Expected Response:

Registered successfully



