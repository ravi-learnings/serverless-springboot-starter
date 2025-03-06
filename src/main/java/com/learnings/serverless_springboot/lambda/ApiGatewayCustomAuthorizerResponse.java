package com.learnings.serverless_springboot.lambda;

import java.util.Map;

public class ApiGatewayCustomAuthorizerResponse {
    private String principalId;
    private AuthPolicy.PolicyDocument policyDocument;
    private Map<String, String> context;

    public ApiGatewayCustomAuthorizerResponse(String principalId, AuthPolicy.PolicyDocument policyDocument, Map<String, String> context) {
        this.principalId = principalId;
        this.policyDocument = policyDocument;
        this.context = context;
    }

    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    public AuthPolicy.PolicyDocument getPolicyDocument() {
        return policyDocument;
    }

    public void setPolicyDocument(AuthPolicy.PolicyDocument policyDocument) {
        this.policyDocument = policyDocument;
    }

    public Map<String, String> getContext() {
        return context;
    }

    public void setContext(Map<String, String> context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return "ApiGatewayCustomAuthorizerResponse{" +
                "principalId='" + principalId + '\'' +
                ", policyDocument=" + policyDocument +
                ", getContext=" + context +
                '}';
    }
}
