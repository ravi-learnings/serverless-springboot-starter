package com.learnings.serverless_springboot.rest;

import jakarta.servlet.http.HttpServletRequest;

public interface HealthCheckApi {
    String getPublicHealthStatus();

    String getHealthStatus(HttpServletRequest request);
}
