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