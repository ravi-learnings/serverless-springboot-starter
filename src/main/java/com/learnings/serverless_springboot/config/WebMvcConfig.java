package com.learnings.serverless_springboot.config;

import com.learnings.serverless_springboot.interceptor.AuthenticationInterceptor;
import com.learnings.serverless_springboot.utils.EnvironmentUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthenticationInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/public/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(EnvironmentUtil.getAllowedOrigins())
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
