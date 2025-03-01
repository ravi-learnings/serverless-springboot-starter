package com.learnings.serverless_springboot.rest.impl;

import com.learnings.serverless_springboot.rest.HealthCheckApi;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController implements HealthCheckApi {
    @Override
    @RequestMapping(value = "/api/v1/health-check", method = RequestMethod.GET)
    public String getHealthStatus() {
        return "OK";
    }
}
