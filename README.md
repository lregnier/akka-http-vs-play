# Akka HTTP vs Play Framework

The aim of this project is to show a brief comparison of design and perfomance when building an HTTP API both with Akka HTTP and Play technologies.

The structure of the project is as follows:
- api-akka-http: core services exposed through a built-Akka HTTP API
- api-play: core services exposed through a built-Play API
- core: core services and domain
- load-test: load tests which hit the APIs endpoints

## Akka HTTP API
To run api-akka-http server:

```sh
sbt 'project api-akka-http' aspectj-runner:run
```

## Play Framework API
To run api-play server:

```sh
sbt 'project api-play' aspectj-runner:run
```
## Load Testing
To run load-test:
For running the load tests either the Akka HTTP or the Play API must be running first:
```sh
sbt 'project load-test' test
```
