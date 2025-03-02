# Cors Configuration for SpringBoot application

This document explains how to configure CORS (Cross-Origin Resource Sharing) for a Spring Boot application.

## What is CORS?

CORS is a mechanism that allows a web application to allow access from another domain.

When a web application makes a request to a different domain, the browser blocks the request due to the same-origin policy. CORS allows the server to specify who can access its resources.

## How to Configure CORS in Spring Boot

To configure CORS in Spring Boot, you need to add the following configuration to your Spring Boot application:

1. Create a WebMvcConfig (@Configuration class) that extends `WebMvcConfigurer`. 
2. Override the `addCorsMappings` method to configure CORS.
3. Set the allowed origins, allowed methods, allowed headers, and exposed headers.
4. The CorsUtil class will return the allowed origins, methods, headers, and max age based on the environment.


```java
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(CORSUtil.getAllowedOrigins())
                .allowedMethods(CORSUtil.getAllowedMethods())
                .allowedHeaders(CORSUtil.getAllowedHeaders())
                .allowCredentials(true)
                .maxAge(Long.parseLong(CORSUtil.getMaxAge()));
    }
```

> **Important**: When allowCredentials is true, allowedOrigins cannot contain the special value "*" since that cannot be set on the "Access-Control-Allow-Origin" response header. To allow credentials to a set of origins, list them explicitly or consider using "allowedOriginPatterns" instead.


# Cors Config for an API Gateway

For a Lambda proxy integration or HTTP proxy integration, your backend is responsible for returning the Access-Control-Allow-Origin, Access-Control-Allow-Methods, and Access-Control-Allow-Headers headers, 
because a proxy integration doesn't return an integration response.

Since SAM by default uses a proxy integration, you need to configure CORS in your backend service.
