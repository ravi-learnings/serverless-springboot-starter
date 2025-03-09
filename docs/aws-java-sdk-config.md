# AWS Java SDK Configuration

This document explains how to configure the AWS Java SDK in a Spring Boot application.

## Declare SDK as dependency

To use the AWS SDK for Java in your project, you need to declare it as a dependency in your project’s pom.xml file.

The archetype generates a BOM (bill of materials) artifact dependency for the software.amazon.awssdk group id. 
With a BOM, you do not have to specify the maven version for individual artifact dependencies that share the same group id.

```xml
<project>
  <properties>
    <aws.java.sdk.version>2.X.X</aws.java.sdk.version>
  </properties>
  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>bom</artifactId>
        <version>${aws.java.sdk.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>
</project>
```