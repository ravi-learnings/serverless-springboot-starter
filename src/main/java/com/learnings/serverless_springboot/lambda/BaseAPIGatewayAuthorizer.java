package com.learnings.serverless_springboot.lambda;

import com.amazonaws.serverless.proxy.model.ApiGatewayAuthorizerContext;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayCustomAuthorizerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class BaseAPIGatewayAuthorizer implements RequestHandler<APIGatewayCustomAuthorizerEvent, ApiGatewayCustomAuthorizerResponse> {

    private static final Logger logger = LoggerFactory.getLogger(BaseAPIGatewayAuthorizer.class);
    @Override
    public ApiGatewayCustomAuthorizerResponse handleRequest(APIGatewayCustomAuthorizerEvent input, Context context) {
        // Token will be provided in cookies - so retrieve the cookies "sessionToken" from the input headers
        String sessionToken = getSessionTokenFromCookies(input);

        if (sessionToken == null || sessionToken.isEmpty()) {
            throw new RuntimeException("Unauthorized");
        }

       // TODO: Implement your authorization logic here
        // Look up the session-db using the sessionToken, if available add the actual access token in request context and add allow all policy.
        // If sessionToken is not available or invalid, return deny policy.

        // this could be accomplished in a number of ways:
        // 1. Call out to OAuth provider
        // 2. Decode a JWT token in-line
        // 3. Lookup in a self-managed DB
        String principalId = "xxxx"; // Decode the access token and use student id or name as principal id

        // if the client token is not recognized or invalid
        // you can send a 401 Unauthorized response to the client by failing like so:
        // throw new RuntimeException("Unauthorized");

        // if the token is valid, a policy should be generated which will allow or deny access to the client

        // if access is denied, the client will receive a 403 Access Denied response
        // if access is allowed, API Gateway will proceed with the back-end integration configured on the method that was called

        String methodArn = input.getMethodArn();
        String[] arnPartials = methodArn.split(":");
        String region = arnPartials[3];
        String awsAccountId = arnPartials[4];
        String[] apiGatewayArnPartials = arnPartials[5].split("/");
        String restApiId = apiGatewayArnPartials[0];
        String stage = apiGatewayArnPartials[1];
//        String httpMethod = apiGatewayArnPartials[2];
//        String resource = ""; // root resource
//        if (apiGatewayArnPartials.length == 4) {
//            resource = apiGatewayArnPartials[3];
//        }

        // this function must generate a policy that is associated with the recognized principal user identifier.
        // depending on your use case, you might store policies in a DB, or generate them on the fly

        // keep in mind, the policy is cached for 5 minutes by default (TTL is configurable in the authorizer)
        // and will apply to subsequent calls to any method/resource in the RestApi
        // made with the same token

        AuthPolicy.PolicyDocument policyDocument = AuthPolicy.PolicyDocument.getAllowAllPolicy(region, awsAccountId, restApiId, stage);

        logger.info("Generated policy document: {}", policyDocument.getStatements().get(0));

        Map<String, String> authorizerContext = new HashMap<>();

        authorizerContext.put("idToken", "token retrieved from session-db");

        ApiGatewayCustomAuthorizerResponse response = new ApiGatewayCustomAuthorizerResponse(principalId, policyDocument, authorizerContext);

        logger.info(response.toString());

        return response;
    }

    private String getSessionTokenFromCookies(APIGatewayCustomAuthorizerEvent input) {
        Map<String, String> headers = input.getHeaders();
        String cookieHeader = getCookieFromHeaders(headers);

        Map<String, String> cookies = parseCookies(cookieHeader);

        return cookies.get("sessionToken");
    }

    private String getCookieFromHeaders(Map<String, String> headers) {
        return headers.get("Cookie");
    }

    private Map<String, String> parseCookies(String cookieHeader) {
        Map<String, String> cookieMap = new HashMap<>();

        if (cookieHeader != null && !cookieHeader.isEmpty()) {
            String[] cookies = cookieHeader.split(";");

            for (String cookie : cookies) {
                int indexOfEq = cookie.indexOf('=');
                if (indexOfEq > 0) {
                    String name = cookie.substring(0, indexOfEq).trim();
                    String value = cookie.substring(indexOfEq + 1).trim();
                    cookieMap.put(name, value);
                }
            }
        }

        return cookieMap;
    }
}
