# Custom Authorizer for API Gateway

Use a Lambda authorizer (formerly known as a custom authorizer) to control access to your API. When a client makes a request your API's method, 
API Gateway calls your Lambda authorizer. The Lambda authorizer takes the caller's identity as the input and returns an IAM policy as the output.

# How it works

1. The client calls a method on an API Gateway API, passing a bearer token or request parameters.

2. API Gateway checks if the method request is configured with a Lambda authorizer. If it is, API Gateway calls the Lambda function.

3. The Lambda function authenticates the caller. The function can authenticate in the following ways:
   1.By calling out to an OAuth provider to get an OAuth access token. 
   2.By calling out to a SAML provider to get a SAML assertion. 
   3.By generating an IAM policy based on the request parameter values.
   4.By retrieving credentials from a database.

4. The Lambda function returns an IAM policy and a principal identifier. If the Lambda function does not return that information, the call fails.

5. API Gateway evaluates the IAM policy. 
   1.If access is denied, API Gateway returns a suitable HTTP status code, such as 403 ACCESS_DENIED.
   2.If access is allowed, API Gateway invokes the method.

If you enable authorization caching, API Gateway caches the policy so that the Lambda authorizer function isn't invoked again.

# Types of Lambda authorizers

There are two types of Lambda authorizers:

1. Request authorizer: receives the caller's identity in a combination of headers, query string parameters,stageVariables, and $context variables.
2. Token authorizer: receives the caller's identity in a bearer token passed in the Authorization header.

For the input format of event sent to lambda function from API gateway refer: https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-lambda-authorizer-input.html

For proxy integration, refer: https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html#api-gateway-simple-proxy-for-lambda-input-format

Output format of the response from lambda function to API gateway: https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-lambda-authorizer-output.html

