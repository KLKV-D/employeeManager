Employee manager
===

Simple employee management system. Allows to use CRUD operation on positions, departments, employees and users.

Users roles are: ADMIN, MANAGER, USER. 

USER can operate with positions, departments and employees. Also USER can edit own profile and password.

MANAGER can do the same as USER and use CRUD operations on users with role USER.

ADMIN can do the same as MANAGER and use CRUD operations on users with role MANAGER.

User with role ADMIN creates at startup with username and password admin.

How to run
---

You need docker and docker-compose to be installed.

1. Clone this repository
```agsl
git clone https://github.com/KLKV-D/employee-manager.git
```
2. Start containers
```agsl
cd employee-manager
docker-compose up
```
3. Application is available on
```agsl
localhost:8080/api/home
```

## Built With
* [Java 17]
* [Spring Boot 3]
* [Spring Framework 6]
* [PostgreSQL]
* [Thymeleaf]
* [Docker]