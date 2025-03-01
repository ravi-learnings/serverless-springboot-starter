# GitHub Actions Deployment for Spring Boot Lambda

This document explains how to set up GitHub Actions for automated deployment of your Spring Boot Lambda application using AWS SAM.

## Prerequisites

Before you can use the GitHub Actions workflow, you need to configure your GitHub repository with the necessary secrets and variables.
Since we are going to use an IAM role to deploy the application, we need to create an IAM Role and add it to secrets. 

### Required GitHub Secrets

Add these secrets in your GitHub repository settings (Settings → Secrets and variables → Actions → Repository secrets):

1. `AWS_ROLE_ARN` - Your AWS IAM Role with all permissions to create the resources.

> **Important**: The IAM Role should have permissions for CloudFormation, S3, Lambda, API Gateway, and IAM.

### Optional GitHub Variables

Add these variables to customize the deployment (Settings → Secrets and variables → Actions → Repository variables):

1. `AWS_REGION` - AWS region to deploy to (default: `us-east-1`)
2. `S3_BUCKET` - S3 bucket for deployment artifacts (default: `serverless-springboot-deployment-bucket`)
3. `STACK_NAME_PREFIX` - Prefix for CloudFormation stack name (default: `serverless-springboot`)
4. `TEMPLATE_FILE` - Path to SAM template file (default: `template.yaml`)

## How to Trigger Deployments

The workflow can be triggered in two ways:

1. **Automatic deployment**: Push to the `main` branch
2. **Manual deployment**: Use the "Run workflow" button in the Actions tab
    - When running manually, you can select the environment (`dev` or `prod`)

## Workflow Steps

The GitHub Actions workflow performs the following steps:

1. Checks out your code repository
2. Sets up Java 17 for building
3. Builds your Spring Boot application with Maven
4. Configures AWS credentials
5. Sets up the AWS SAM CLI
6. Packages your application
7. Deploys the application to AWS
8. Retrieves and displays the API Gateway URL
9. Verifies the deployment by calling the API
10. Provides a deployment summary

## Deployment Environments

The workflow supports multiple deployment environments:

- `dev` (default)
- `prod`

Each environment gets its own CloudFormation stack following the naming pattern:
`{STACK_NAME_PREFIX}-{environment}`

## Viewing Deployment Results

After each deployment:

1. The API Gateway URL is printed in the workflow logs
2. A deployment summary is available in the workflow summary
3. The workflow performs a basic verification by calling your API

## Customizing the Workflow

You can customize the workflow by:

1. Modifying the workflow file (`.github/workflows/deploy.yml`)
2. Setting different GitHub repository variables
3. Adding environment-specific configurations

## Troubleshooting

If the deployment fails:

1. Check the GitHub Actions logs for error messages
2. Verify your AWS credentials have the necessary permissions
3. Make sure your S3 bucket name is globally unique
4. Check the SAM template for any syntax errors

For more detailed troubleshooting, refer to the AWS SAM CLI documentation.