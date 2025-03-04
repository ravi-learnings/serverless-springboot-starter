package com.learnings.serverless_springboot.rest;

public interface HealthCheckApi {
    String getPublicHealthStatus();

    String getHealthStatus();
}
