package com.learnings.serverless_springboot.lambda;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.model.SingleValueHeaders;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnings.serverless_springboot.ServerlessSpringbootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

public class LambdaHandler implements RequestStreamHandler {
    ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(LambdaHandler.class);

    private static final String BASE_PATH = "/app1"; // adjust to your base path

    private static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;
    static {
        try {
            handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(ServerlessSpringbootApplication.class);
            // If you are using HTTP APIs with the version 2.0 of the proxy model, use the getHttpApiV2ProxyHandler
            // method: handler = SpringBootLambdaContainerHandler.getHttpApiV2ProxyHandler(Application.class);
        } catch (ContainerInitializationException e) {
            // if we fail here. We re-throw the exception to force another cold start
            e.printStackTrace();
            throw new RuntimeException("Could not initialize Spring Boot application", e);
        }
    }

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

    private void excludeBasePath(AwsProxyRequest requestEvent) {
        String path = requestEvent.getPath();
        if (path != null && path.startsWith(BASE_PATH)) {
            handler.stripBasePath(BASE_PATH);
        }
    }
}
