The "csye6225-aws-cf-create-stack.sh" takes the stack name from the user, checks whether the satck exists or not.
If the stack exists then the message is displayed as the stack exists and terminates it.
If the stack does not exist then the stack with the given name exits in which the json template name is passed.
Finally once the stack is created successfully and the script is excuted the messages are displayed accordingly.
The script "csye6225-aws-cf-terminate-stack.sh" for termination also checks if the stack exists or not.
If the stack exists then the script terminates the stack resource and waits for the resource termination.
After successful completion of termination the message is displyed.
