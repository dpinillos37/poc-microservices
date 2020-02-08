# poc-microservices
Proof of concept of an API Rest with Java 8, Spring Boot, Docker, Hibernate Validator, Lombok, Mybatis, SQL, Flyway, Junit 5, Mockito, Cucumber...

## Start the application

To start the application, just start the Docker container with the database and start the Java application.

### Database

In the folder `docker` in this project, run the script:

```
start-db.sh
```

It will create:
* A **Docker container** with a **Postgresql** database
* A **Docker volume** which will store the database information, to keep it after closing the container
* A **web console** to manage the database in the URL localhost:8080

### Application 

Just launch the main class of the project as a Java application:
```
ServiceMain.java
```

It will create the database tables with Flyway on the first execution.

## Tests
The tests implemented are:

* **Unit tests** - done with Junit 5 and Mockito for almost all the classes, and SpringRunner for Hibernate validators used in the entities.
* **Acceptance tests** - done with Cucumber.

They cover more than 90% of the code.

To launch the tests use Maven:
```
mvn clean verify
```

The tests are launched against a Docker container of Postgresql which is started automatically at the begining, and pruned at the end of the test phase.
