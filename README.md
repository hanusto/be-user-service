# user-service

Demo of user service to fetch user profile data.

## Modules

* provider - it is library to wrap data provider to fetch user profile
* cli - it provides executable JAR 
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
