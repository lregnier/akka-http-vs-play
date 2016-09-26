# Akka HTTP vs Play Framework

The aim of this project is to show a brief comparison of design and perfomance when building an HTTP API both with [Akka HTTP] and [Play Framework].

The structure of the project is as follows:
- api-akka-http: core services exposed through a built-Akka HTTP API
- api-play: core services exposed through a built-Play API
- core: core services and domain
- load-test: load tests which hit the APIs endpoints

## Akka HTTP API
To run the Akka HTTP API execute the following:

```sh
sbt 'project api-akka-http' aspectj-runner:run
```

## Play Framework API
To run the Play Framework API execute the following:

```sh
sbt 'project api-play' aspectj-runner:run
```

## Core
It contains the domain, persistence and services modules common to both APIs. 


## Load Test
Before running the load tests either the Akka HTTP or the Play API must be running first. Then, execute the following:
```sh
sbt 'project load-test' test
```

If you wanna check how the load test went just copy and paste the final path coming out from the run, or grab the `index.html` of any of the files under `/gatling-reports`

[Akka HTTP]: <http://doc.akka.io/docs/akka/2.4.4/scala/http/>
[Play Framework]: <https://playframework.com/>
