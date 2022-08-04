# Recipe Manager
## how to run the application
First you need to have maven and docker installed on the machine you want to run it on.

### build application
To (re)build the application (jar), execute the following:
Then you can execute the following:
```shell script
mvn package
```

### build docker image
To build a docker image that can run the application, execute the following:
```shell script
docker-compose build
```

### run docker image
To run the docker image, execute the following:
```shell script
docker-compose up
```
This will start both a postgres database that stores all data and the recipe manager.
The application has a swagger ui which can be used to call the API:
http://localhost:8080/swagger-ui/

## choices
The following choices were made when creating this application:
- Spring Boot
- Postgres DB: relational database
- Swagger: great tooling for generating API documentation
- Lombok: easy getters/setters generation, and more
- Docker: containerize the application together with the database for easy setup
- seperation of controller layer and service layer so there are no strong dependencies between frontend and database

## next steps
- add security to API
- filter on ingredients using sql instead of in code
- use docker volume to store postgres data outside the container