package com.learnings.serverless_springboot.lambda;

import java.util.*;

public class AuthPolicy {
    // IAM Policy Constants
    public static final String VERSION = "Version";
    public static final String STATEMENT = "Statement";
    public static final String EFFECT = "Effect";
    public static final String ACTION = "Action";
    public static final String NOT_ACTION = "NotAction";
    public static final String RESOURCE = "Resource";
    public static final String NOT_RESOURCE = "NotResource";
    public static final String CONDITION = "Condition";

    String principalId;

    transient PolicyDocument policyDocumentObject;
    Map<String, Object> policyDocument;
    Map<String, String> context;

    public AuthPolicy(String principalId, AuthPolicy.PolicyDocument policyDocumentObject, Map<String, String> context) {
        this.principalId = principalId;
        this.policyDocumentObject = policyDocumentObject;
        this.context = context != null ? context : new HashMap<>();
    }

    public Map<String, Object> generatePolicy() {
        Map<String, Object> policy = new HashMap<>();
        Map<String, Object> policyDocumentBody = new HashMap<>();
        policyDocumentBody.put(VERSION, this.policyDocumentObject.getVersion());
        policyDocumentBody.put(STATEMENT, this.policyDocumentObject.getStatement());
        policy.put("principalId", this.principalId);
        policy.put("policyDocument", policyDocumentBody);
        policy.put("context", this.context);
        return policy;
    }


    public AuthPolicy() {
    }

    public Map<String, String> getContext() {
        return context;
    }

    public void setContext(Map<String, String> context) {
        this.context = context;
    }

    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    /**
     * IAM Policies use capitalized field names, but Lambda by default will serialize object members using camel case
     *
     * This method implements a custom serializer to return the IAM Policy as a well-formed JSON document, with the correct field names
     *
     * @return IAM Policy as a well-formed JSON document
     */
    public Map<String, Object> getPolicyDocument() {
        Map<String, Object> serializablePolicy = new HashMap<>();
        serializablePolicy.put(VERSION, policyDocumentObject.Version);
        Statement[] statements = policyDocumentObject.getStatement();
        Map<String, Object>[] serializableStatementArray = new Map[statements.length];
        for (int i = 0; i < statements.length; i++) {
            Map<String, Object> serializableStatement = new HashMap<>();
            AuthPolicy.Statement statement = statements[i];
            serializableStatement.put(EFFECT, statement.Effect);
            serializableStatement.put(ACTION, statement.Action);
            serializableStatement.put(RESOURCE, statement.getResource());
            serializableStatement.put(CONDITION, statement.getCondition());
            serializableStatementArray[i] = serializableStatement;
        }
        serializablePolicy.put(STATEMENT, serializableStatementArray);
        return serializablePolicy;
    }

    public void setPolicyDocument(PolicyDocument policyDocumentObject) {
        this.policyDocumentObject = policyDocumentObject;
    }

    public static class PolicyDocument {
        static final String EXECUTE_API_ARN_FORMAT = "arn:aws:execute-api:%s:%s:%s/%s/%s/%s";

        String Version = "2012-10-17"; // override if necessary

        private Statement allowStatement;
        private Statement denyStatement;
        private List<Statement> statements;

        // context metadata
        transient String region;
        transient String awsAccountId;
        transient String restApiId;
        transient String stage;


        /**
         * Creates a new PolicyDocument with the given context,
         * and initializes two base Statement objects for allowing and denying access to API Gateway methods
         *
         * @param region the region where the RestApi is configured
         * @param awsAccountId the AWS Account ID that owns the RestApi
         * @param restApiId the RestApi identifier
         * @param stage and the Stage on the RestApi that the Policy will apply to
         */
        public PolicyDocument(String region, String awsAccountId, String restApiId, String stage) {
            this.region = region;
            this.awsAccountId = awsAccountId;
            this.restApiId = restApiId;
            this.stage = stage;
            allowStatement = Statement.getEmptyInvokeStatement("Allow");
            denyStatement = Statement.getEmptyInvokeStatement("Deny");
            this.statements = new ArrayList<>();
            statements.add(allowStatement);
            statements.add(denyStatement);
        }

        public String getVersion() {
            return Version;
        }

        public void setVersion(String version) {
            Version = version;
        }

        public List<Statement> getStatements() {
            return statements;
        }

        public void setStatements(List<Statement> statements) {
            this.statements = statements;
        }

        public Statement[] getStatement() {
            return statements.toArray(new AuthPolicy.Statement[statements.size()]);
        }

        public void allowMethod(HttpMethod httpMethod, String resourcePath) {
            addResourceToStatement(allowStatement, httpMethod, resourcePath);
        }

        public void denyMethod(HttpMethod httpMethod, String resourcePath) {
            addResourceToStatement(denyStatement, httpMethod, resourcePath);
        }

        public void addStatement(AuthPolicy.Statement statement) {
            statements.add(statement);
        }

        private void addResourceToStatement(Statement statement, HttpMethod httpMethod, String resourcePath) {
            // resourcePath must start with '/'
            // to specify the root resource only, resourcePath should be an empty string
            if (resourcePath.equals("/")) {
                resourcePath = "";
            }
            String resource = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
            String method = httpMethod == HttpMethod.ALL ? "*" : httpMethod.toString();
            statement.addResource(String.format(EXECUTE_API_ARN_FORMAT, region, awsAccountId, restApiId, stage, method, resource));
        }

        /**
         * Generates a new PolicyDocument with a single statement that allows the requested method/resourcePath
         *
         * @param region API Gateway region
         * @param awsAccountId AWS Account that owns the API Gateway RestApi
         * @param restApiId RestApi identifier
         * @param stage Stage name
         * @param method HttpMethod to allow
         * @param resourcePath Resource path to allow
         * @return new PolicyDocument that allows the requested method/resourcePath
         */
        public static PolicyDocument getAllowOnePolicy(String region, String awsAccountId, String restApiId, String stage, HttpMethod method, String resourcePath) {
            AuthPolicy.PolicyDocument policyDocument = new AuthPolicy.PolicyDocument(region, awsAccountId, restApiId, stage);
            policyDocument.allowMethod(method, resourcePath);
            return policyDocument;

        }


        /**
         * Generates a new PolicyDocument with a single statement that denies the requested method/resourcePath
         *
         * @param region API Gateway region
         * @param awsAccountId AWS Account that owns the API Gateway RestApi
         * @param restApiId RestApi identifier
         * @param stage Stage name
         * @param method HttpMethod to deny
         * @param resourcePath Resource path to deny
         * @return new PolicyDocument that denies the requested method/resourcePath
         */
        public static PolicyDocument getDenyOnePolicy(String region, String awsAccountId, String restApiId, String stage, HttpMethod method, String resourcePath) {
            AuthPolicy.PolicyDocument policyDocument = new AuthPolicy.PolicyDocument(region, awsAccountId, restApiId, stage);
            policyDocument.denyMethod(method, resourcePath);
            return policyDocument;
        }

        public static PolicyDocument getAllowAllPolicy(String region, String awsAccountId, String restApiId, String stage) {
            return getAllowOnePolicy(region, awsAccountId, restApiId, stage, HttpMethod.ALL, "*");
        }

        public static PolicyDocument getDenyAllPolicy(String region, String awsAccountId, String restApiId, String stage) {
            return getDenyOnePolicy(region, awsAccountId, restApiId, stage, HttpMethod.ALL, "*");
        }
    }

    public enum HttpMethod {
        GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS, ALL
    }

    public static class Statement {
        String Effect;
        String Action;
        Map<String, Map<String, Object>> Condition;
        private List<String> resourceList;

        public Statement() {

        }

        public Statement(String effect, String action, List<String> resourceList, Map<String, Map<String, Object>> condition) {
            this.Effect = effect;
            this.Action = action;
            this.resourceList = resourceList;
            this.Condition = condition;
        }

        public static Statement getEmptyInvokeStatement(String effect) {
            return new Statement(effect, "execute-api:Invoke", new ArrayList<>(), new HashMap<>());
        }

        public String getEffect() {
            return Effect;
        }

        public void setEffect(String effect) {
            Effect = effect;
        }

        public String getAction() {
            return Action;
        }

        public void setAction(String action) {
            Action = action;
        }

        public String[] getResource() {
            return resourceList.toArray(new String[resourceList.size()]);
        }

        public Map<String, Map<String, Object>> getCondition() {
            return Condition;
        }

        public void setCondition(Map<String, Map<String, Object>> condition) {
            Condition = condition;
        }

        public List<String> getResourceList() {
            return resourceList;
        }

        public void addResource(String resource) {
            resourceList.add(resource);
        }

        public void setResourceList(List<String> resourceList) {
            this.resourceList = resourceList;
        }

        public void addCondition(String operator, String key, Object value) {
            Condition.put(operator, Collections.singletonMap(key, value));
        }
    }

}
