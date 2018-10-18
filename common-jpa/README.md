# About
This is a common library to work with Spring JPA

All microservices for JPA usage should have it as a compile dependency.

# How to migrate DB structure
This is example how to make DB structure changes for user-service:
1. change JPA model
2. override following properties:
```
spring.jpa.hibernate.ddlAuto = create
spring.liquibase.enabled = false
```
3. create new DB:
```
psql -h localhost -U postgres
drop database user_db;
create database user_db;
```
4. start user-service:
```
gradle :user-service:bootRun
```
5. drop Flyway history table:
```
\c user_db
drop table flyway_schema_history;
```
6. generate changelog using Liquibase gradle plugin:
```
gardle :user-service:generateChangelog
```
7. copy created file to initial
8. revert properties override from item 2
9. recreate DB before start application (see item 3)
