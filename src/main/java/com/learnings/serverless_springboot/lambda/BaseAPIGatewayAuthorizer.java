package com.learnings.serverless_springboot.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayCustomAuthorizerEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.ForbiddenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class BaseAPIGatewayAuthorizer implements RequestHandler<APIGatewayCustomAuthorizerEvent, AuthPolicy> {

    private static final Logger logger = LoggerFactory.getLogger(BaseAPIGatewayAuthorizer.class);
    @Override
    public AuthPolicy handleRequest(APIGatewayCustomAuthorizerEvent input, Context context) {
        // Token will be provided in cookies - so retrieve the cookies "sessionToken" from the input headers
        String sessionToken = getSessionTokenFromCookies(input);

        if (sessionToken == null || sessionToken.isEmpty()) {
            throw new RuntimeException("Unauthorized");
        }

       // TODO: Implement your authorization logic here
        String principalId = "xxxx";

        String methodArn = input.getMethodArn();
        String[] arnPartials = methodArn.split(":");
        String region = arnPartials[3];
        String awsAccountId = arnPartials[4];
        String[] apiGatewayArnPartials = arnPartials[5].split("/");
        String restApiId = apiGatewayArnPartials[0];
        String stage = apiGatewayArnPartials[1];

        AuthPolicy authPolicy;

      if (sessionToken.equals("allow")) {
            Map<String, String> authorizerContext = new HashMap<>();
            authorizerContext.put("user_id", "123456789");

            authPolicy = new AuthPolicy(principalId, AuthPolicy.PolicyDocument.getAllowAllPolicy(region, awsAccountId, restApiId, stage), authorizerContext);

        } else {
            authPolicy =  new AuthPolicy(principalId, AuthPolicy.PolicyDocument.getDenyAllPolicy(region, awsAccountId, restApiId, stage), null);
        }

        logGeneratedAuthPolicy(authPolicy);

        return authPolicy;

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

    private void logGeneratedAuthPolicy(AuthPolicy authPolicy) {


        Map<String, Object> result = authPolicy.generatePolicy();
        try {
            logger.info("Auth Policy: {}", new ObjectMapper().writeValueAsString(authPolicy));
            logger.info("Generated Policy: {}", new ObjectMapper().writeValueAsString(result));
        } catch (JsonProcessingException e) {

            logger.error("Error generating policy", e);
        }
    }
}
