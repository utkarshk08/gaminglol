# Spring Boot Login and Registration example with MongoDB

Build a Spring Boot Auth with HttpOnly Cookie, JWT, Spring Security and Spring Data MongoDB. You'll know:
- Appropriate Flow for User Login and Registration with JWT
- Spring Boot Rest API Architecture with Spring Security
- How to configure Spring Security to work with JWT
- How to define Data Models and association for User Login and Registration
- Way to get and generate Cookies for Token
- Way to use Spring Data MongoDB to interact with MongoDB Database

## User Registration, Login and Authorization process.

![spring-boot-mongodb-login-example-flow](spring-boot-mongodb-login-example-flow.png)

## Spring Boot Rest API Architecture with Spring Security
You can have an overview of our Spring Boot Server with the diagram below:

![spring-boot-mongodb-login-example-architecture](spring-boot-mongodb-login-example-architecture.png)

For more detail, please visit:
> [Spring Boot Login and Registration example with MongoDB](https://www.bezkoder.com/spring-boot-mongodb-login-example/)

Working with Front-end:
> [Angular 12](https://www.bezkoder.com/angular-12-jwt-auth-httponly-cookie/) / [Angular 13](https://www.bezkoder.com/angular-13-jwt-auth-httponly-cookie/) / [Angular 14](https://www.bezkoder.com/angular-14-jwt-auth/) / [Angular 15](https://www.bezkoder.com/angular-15-jwt-auth/) / [Angular 16](https://www.bezkoder.com/angular-16-jwt-auth/) / [Angular 17](https://www.bezkoder.com/angular-17-jwt-auth/)

> [React](https://www.bezkoder.com/react-login-example-jwt-hooks/) / [React Redux](https://www.bezkoder.com/redux-toolkit-auth/)

More Practice:
> [Spring Boot with MongoDB CRUD example using Spring Data](https://www.bezkoder.com/spring-boot-mongodb-crud/)

> [Spring Boot MongoDB Pagination & Filter example](https://www.bezkoder.com/spring-boot-mongodb-pagination/)

> [Spring Boot + GraphQL + MongoDB example](https://www.bezkoder.com/spring-boot-graphql-mongodb-example-graphql-java/)

> [Spring Boot Repository Unit Test with @DataJpaTest](https://bezkoder.com/spring-boot-unit-test-jpa-repo-datajpatest/)

> [Spring Boot Rest Controller Unit Test with @WebMvcTest](https://www.bezkoder.com/spring-boot-webmvctest/)

> Validation: [Spring Boot Validate Request Body](https://www.bezkoder.com/spring-boot-validate-request-body/)

> Documentation: [Spring Boot and Swagger 3 example](https://www.bezkoder.com/spring-boot-swagger-3/)

> Caching: [Spring Boot Redis Cache example](https://www.bezkoder.com/spring-boot-redis-cache-example/)

Fullstack:
> [Vue.js + Spring Boot + MongoDB example](https://www.bezkoder.com/spring-boot-vue-mongodb/)

> [Angular 8 + Spring Boot + MongoDB example](https://www.bezkoder.com/angular-spring-boot-mongodb/)

> [Angular 10 + Spring Boot + MongoDB example](https://www.bezkoder.com/angular-10-spring-boot-mongodb/)

> [Angular 11 + Spring Boot + MongoDB example](https://www.bezkoder.com/angular-11-spring-boot-mongodb/)

> [Angular 12 + Spring Boot + MongoDB example](https://www.bezkoder.com/angular-12-spring-boot-mongodb/)

> [Angular 13 + Spring Boot + MongoDB example](https://www.bezkoder.com/angular-13-spring-boot-mongodb/)

> [Angular 14 + Spring Boot + MongoDB example](https://www.bezkoder.com/spring-boot-angular-14-mongodb/)

> [Angular 15 + Spring Boot + MongoDB example](https://www.bezkoder.com/spring-boot-angular-15-mongodb/)

> [Angular 16 + Spring Boot + MongoDB example](https://www.bezkoder.com/spring-boot-angular-16-mongodb/)

> [Angular 17 + Spring Boot + MongoDB example](https://www.bezkoder.com/spring-boot-angular-17-mongodb/)

> [React + Spring Boot + MongoDB example](https://www.bezkoder.com/react-spring-boot-mongodb/)

Run both Back-end & Front-end in one place:
> [Integrate Angular with Spring Boot Rest API](https://www.bezkoder.com/integrate-angular-spring-boot/)

> [Integrate React with Spring Boot Rest API](https://www.bezkoder.com/integrate-reactjs-spring-boot/)

> [Integrate Vue with Spring Boot Rest API](https://www.bezkoder.com/integrate-vue-spring-boot/)

## Run Spring Boot application
```
mvn spring-boot:run
```

## Word Hunt API (simple demo)

Base path: `/api/wordhunt`

- `GET /api/wordhunt/daily?date=YYYY-MM-DD&size=4`
  - Public. Returns deterministic daily grid and seed.
  - Response:
    ```json
    { "seed": 20250917, "size": 4, "grid": [["A","B","C","D"], ["E","F","G","H"], ["I","J","K","L"], ["M","N","O","P"]] }
    ```

- `GET /api/wordhunt/leaderboard?date=YYYY-MM-DD&limit=50`
  - Public. Returns top scores for the given day.
  - Response:
    ```json
    { "date": "2025-09-17", "top": [{ "username": "alice", "score": 23, "wordsFound": 8 }] }
    ```

- `POST /api/wordhunt/submit?date=YYYY-MM-DD`
  - Body:
    ```json
    { "username": "alice", "words": ["tree", "stone", "note"] }
    ```
  - Server validates against the grid and a small built-in dictionary, deduplicates, requires length >= 3, computes score server-side, and caps to one submission per user per day.
  - Response:
    ```json
    { "accepted": true, "message": "OK", "score": 10, "wordsFound": 3, "words": ["TREE","STONE","NOTE"] }
    ```

- `GET /api/wordhunt/me?date=YYYY-MM-DD&username=alice`
  - Returns the current user's score for the day if submitted.
  - Response:
    ```json
    { "date": "2025-09-17", "username": "alice", "score": 10, "wordsFound": 3, "words": ["TREE","STONE","NOTE"] }
    ```

Notes:
- Grid is generated deterministically from date; default size is 4.
- Dictionary is a minimal in-memory set for demo; replace with a larger source for production use.

## Frontend (static demo)

- Open `http://localhost:8080/` after starting the app.
- Enter a player name at the top and play; no sign-in required.
- Daily grid can be changed via date and size controls; leaderboard and "My Score" panels have their own date pickers.
- Enter words separated by spaces/newlines and click Submit. Server validates and computes the score.
