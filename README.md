# user-service

Demo of user service to fetch user profile data.

## Modules

* provider - it is library to wrap data provider to fetch user profile
* cli - it provides executable JAR 
* service - spring webflux service that provides REST interface
* test - module to support testing

## Getting started

This project use maven for packaging. It wraps public user data info service. 

To build all artifacts, use command:
```
mvn clean package
```

### CLI

It is command line interface to fetch profile of specified user.

To run use command (userId is number, identifier of user):

```
java -jar cli/target/user-service-ci.jar --userId=1
```

### Service

Wraps functionality into deployable micro-service. It is build on the top of spring-webflux (SpringBoot).

To run use command:

```
java -jar service/target/user-service-deployment.jar
```

By default, web server starts on http://localhost:8080

To fetch user data, use for example: ``http://localhost:8080/profiles/1``

### Details

It is possible to configure user data provider with following props:

```
hanusto.user-service.data-provider.baseUrl = URL of service, default is: https://jsonplaceholder.typicode.com
hanusto.user-service.data-provider.usersRelativePath = context of users resource, default is: users
hanusto.user-service.data-provider.postsRelativePath = context of posts resource, default is: posts
```

By default execution of calling remote web server is cached, to disable use ``hanusto.user-service.cache.enabled=false``.
