## Proxy Gateway

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

### A spring cloud gateway application for development purposes.

### Requirements

- Java 25
- Maven 3.6.3+

Built with Spring Boot 4.0.9 and Spring Cloud 2025.1.4 (Spring Cloud Gateway 5.0.4).

To start the gateway, simply run in the repository root (location of pom.xml):
```
mvn spring-boot:run
```

Simply create config/application.yaml with the following content:
```
server:
    port: 18070
spring:
    cloud:
        gateway:
            server:
                webflux:
                    routes:
                    - id: route1
                      uri: "https://www.google.com"
                      predicates:
                      - Path=/api1/**
                      filters:
                      - StripPrefix=1
                      - AddRequestParameter=foo, bar
                      - AddResponseHeader=X-SomeHeader, Bar
```
Note: since Spring Cloud Gateway 5, routes are configured under `spring.cloud.gateway.server.webflux`. The old `spring.cloud.gateway.routes` key is ignored.

The example above makes the uri: http://localhost:18070/api1/ a gateway to google.
You can test retrieving the google home page as follows:
```
curl http://localhost:18070/api1/
```
