## Proxy Gateway

### A spring cloud gateway application for development purposes.

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
The example above makes the uri: http://localhost:18070/api1/ a gateway to google.
You can test retrieving the google home page as follows:
```
curl http://localhost:18070/api1/
```
