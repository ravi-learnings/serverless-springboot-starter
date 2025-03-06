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

# Accessing Authorizer Context in backend lambda function

1. Once the Lambda Authorizer verifies the user request, it returns and IAM policy and a principal identifier. In addition, we can also pass context variables in the response from the Lambda Authorizer.
2. These context variables can be accessed in the backend Lambda like below:

```java
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    AwsHttpServletRequest awsRequest = (AwsHttpServletRequest) request;
    AwsProxyRequestContext context = (AwsProxyRequestContext) awsRequest.getAttribute(RequestReader.API_GATEWAY_CONTEXT_PROPERTY);
    
    ApiGatewayAuthorizerContext authorizerContext = context.getAuthorizer();

    String userId = authorizerContext.getContextValue("user_id");

    if (userId != null) {
        return true;
    }
}
```

> **Important**: The context variables are not available if we use the proxyStream method in SpringBootLambdaContainerHandler. 
For applications that leverage context values from custom authorizers, we recommend using a stream handler: The framework uses Jackson's @JsonAnySetter/Getter annotations to extract custom values from the authorizer context, the serializer included in AWS Lambda does not process annotated fields. In all our samples, we use the RequestStreamHandler interface and the proxyStream method of the Serverless Java Container library. With a POJO-based handler, you can use the proxy method of the handler object directly.

# Example

```java
    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
            throws IOException {
        logger.info("Handling Request");

        AwsProxyRequest requestEvent = mapper.readValue(inputStream, AwsProxyRequest.class);

        logger.info("AwsRequestEvent is {}",mapper.writeValueAsString(requestEvent));

        excludeBasePath(requestEvent);

        AwsProxyResponse response = handler.proxy(requestEvent, context);
        mapper.writeValue(outputStream, response);
    }
```


