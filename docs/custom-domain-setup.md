# API Gateway Custom Domain Setup

This document explains how to set up a custom domain for your API Gateway.

## Prerequisites

1. Before setting up a custom domain name for an API, you must have an SSL/TLS certificate ready in AWS Certificate Manager.
2. To use an ACM certificate with a Regional custom domain name, you must request or import the certificate in the same Region as your API.
3. To use an ACM certificate with an edge-optimized custom domain name, you must request or import the certificate in the US East (N. Virginia) – us-east-1 Region.

## Steps to Set Up a Custom Domain Name

Once we have the certificate ready in ACM, we can set up a custom domain name for our API Gateway.

Add the following configuration to your SAM template:

```template.yml
Resources:
    restApi:
        Type: AWS::Serverless::Api
        Properties:
            StageName: !Ref Environment
            Domain:
                DomainName: api.example.com
                CertificateArn: arn:aws:acm:us-east-1:123456789012:certificate/12345678-1234-1234-1234-123456789012
                EndpointConfiguration: REGIONAL
```

In the above configuration:
- `api.example.com` is the custom domain name you want to use.
- `arn:aws:acm:us-east-1:123456789012:certificate/12345678-1234-1234-1234-123456789012` is the ARN of the SSL/TLS certificate in ACM.
- `REGIONAL` is the endpoint configuration for the custom domain.

Now, you can use the custom domain name in your API Gateway.

# Custom Domain with Base path Mapping

If you want to use a base path mapping for your custom domain, you can add the following configuration to your SAM template:

```template.yml
Resources:
    restApi:
        Type: AWS::Serverless::Api
        Properties:
            StageName: !Ref Environment
            Domain: 
                BasePath: serverless-springboot
                DomainName: api.example.com 
                CertificateArn: arn:aws:acm:us-east-1:123456789012:certificate/12345678-1234-1234-1234-123456789012
                EndpointConfiguration: REGIONAL
```

# Using same Custom Domain with multiple microservices

If you want to use same custom domain with multiple micro services, we need to create a separate stack to create the Custom Domain using AWS cloudformation "AWS::ApiGateway::DomainName" resource.


```template.yml 
# domain-stack.yaml
AWSTemplateFormatVersion: '2010-09-09'
Transform: AWS::Serverless-2016-10-31
Resources:
  MyCustomDomain:
    Type: AWS::ApiGateway::DomainName
    Properties:
      DomainName: api.example.com
      CertificateArn: !Ref CertificateArn
      EndpointConfiguration:
        Types:
          - EDGE
      SecurityPolicy: TLS_1_2

Parameters:
  CertificateArn:
    Type: String
    Description: ARN of the ACM certificate for the custom domain
```

Create an Output for this domain name so that can be imported in the other stacks.

```template.yml
Outputs:
  CustomDomainName:
    Description: "The domain name"
    Value: !Ref MyCustomDomain
    Export:
      Name: !Sub "${AWS::StackName}-CustomDomainName"
```

Create individual stacks for each microservice and import the Custom Domain Name in the respective stacks.

```template.yml
# users-service.yaml
AWSTemplateFormatVersion: '2010-09-09'
Transform: AWS::Serverless-2016-10-31
Resources:
  UsersApi:
    Type: AWS::Serverless::Api
    Properties:
      StageName: Prod

  UsersBasePathMapping:
    Type: AWS::ApiGateway::BasePathMapping
    Properties:
      DomainName: !ImportValue domain-stack-CustomDomainName
      RestApiId: !Ref UsersApi
      Stage: Prod
      BasePath: users
```