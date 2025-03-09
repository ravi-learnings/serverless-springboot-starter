package com.learnings.serverless_springboot.utils;

public class EnvironmentUtil {
    private static final String DEFAULT_ALLOWED_ORIGINS = "*";

    public static String getEnvironment() {
        return System.getenv("ENVIRONMENT");
    }

    public static String getEnvironmentWithDefaultDev() {
        String environment = getEnvironment();
        return environment == null ? "dev" : environment;
    }

    public static String getRegion() {
        return System.getenv("REGION");
    }

    public static String getRegionWithDefaultUsEast1() {
        String region = getRegion();
        return region == null ? "us-east-1" : region;
    }

    public static String[] getAllowedOrigins() {
        if (System.getenv("ALLOWED_ORIGINS") == null) {
            return new String[]{DEFAULT_ALLOWED_ORIGINS};
        }
        return System.getenv("ALLOWED_ORIGINS").split(",");
    }
}
