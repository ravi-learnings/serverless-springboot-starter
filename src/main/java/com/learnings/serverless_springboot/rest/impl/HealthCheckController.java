package com.learnings.serverless_springboot.rest.impl;

import com.learnings.serverless_springboot.rest.HealthCheckApi;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController implements HealthCheckApi {
    @Override
    @RequestMapping(value = "/api/v1/health-check", method = RequestMethod.GET)
    public String getHealthStatus(HttpServletRequest request) {
        return "OK";
    }

    @Override
    @RequestMapping(value = "/public/api/v1/health-check", method = RequestMethod.GET)
    public String getPublicHealthStatus() {
        return "PUBLIC OK";
    }
}
