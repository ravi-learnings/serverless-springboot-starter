package com.learnings.serverless_springboot.interceptor;

import com.amazonaws.serverless.proxy.RequestReader;
import com.amazonaws.serverless.proxy.internal.servlet.AwsHttpServletRequest;
import com.amazonaws.serverless.proxy.model.ApiGatewayAuthorizerContext;
import com.amazonaws.serverless.proxy.model.AwsProxyRequestContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthenticationInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        AwsHttpServletRequest awsRequest = (AwsHttpServletRequest) request;
        AwsProxyRequestContext context = (AwsProxyRequestContext) awsRequest.getAttribute(RequestReader.API_GATEWAY_CONTEXT_PROPERTY);
        ApiGatewayAuthorizerContext authorizerContext = context.getAuthorizer();

        String userId = authorizerContext.getContextValue("user_id");

        if (userId != null) {
            return true;
        }

        throw new RuntimeException("Unauthorized");
    }
}
