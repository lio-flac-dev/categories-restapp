# Categories Microservice REST API (CMRA)

A simple Spring Boot application with MySQL and Redis running inside Docker containers. This API provides the following basic categories CRUD operations: 

1. Clients can create a new category.
2. Clients can retrieve a category by an id or a slug.
3. Clients can retrieve a tree of children under a specific category.
4. Clients can update the visibility status of a specific category.

## Design considerations
- The API supports two levels of security by using Spring Security and JSON Web Token.

- Two levels of caching are provided, one at the client level (ETags) for GET retrieval operations and the other at the application level (Redis to cache the category and their children).

- Logging: SL4J was used to decouple from any specific implementation. The underlying logging is provided by Log4J. 
  To avoid any locking with a specific filesystem location, the generated logs are sent to the STDOUT console.

- Exception handling: all the errors and exceptions are gracefully managed.

- Documentation: all the classes are intradocumented (Javadocs) and also the API is exposed via Swagger.

- Retrieving keys: the category retrieving can be done by categoryId or slug. However, when the client provides both keys, the categoryId prevails over the slug.

- No hard coded values. All the config properties are defined in the `application.properties` file

- This application and its dependencias has been totally containerized by using Docker and Docker Compose.

- Primary key modeling: in order to improve the DB compare performance dramatically, as well as reducing storage, the UUID primary key is modeled in the DB as a BINARY(16). 
  The required conversions between the app (UUID) and the DB (BINARY(16)), are made seamlessly by using MySQL built-in functions BIN_TO_UUID and uuid_to_bin.

## Stack
- Redis
- Docker
- Docker Compose
- Java 8
- Spring Boot 2.0.5
- MySQL
- SL4J & Log4J
- Maven

## Getting Started

In order to start the application and run the microservices, you should execute from a command line the following instruction:

`docker-compose up -d`

You must be inside the directory with the docker-compose.yml file in order to execute the previous Compose command.

After this the Categories Microservice REST API (CMRA) will start.

### Prerequisites

A version of Docker CE and Docker Compose should be installed in order to run the application.

### Running the web services

1. To get access to the API, you should get a token in the following endpoint:

   `http://localhost:8080/categoryApi/generateToken`

   HTTP method: POST
   
   Passing as parameter:
   ```
   {
    "parameter":"categorySubject"
   }
   ```

   Using BASIC auth with the following credentials:

        username=user
        password=flaconi@2018
      
   And adding to the header:
     
        Content-Type=application/json


   After invoking these endpoint you will get something like this:

   ```
   {
       "code": "SUCCESS",
       "result": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3ZWF0aGVyU3ViamVjdCIsImV4cCI6MTUzOTU2NzI0OX0.J3Xli1EV-T_cP-nQ_uJbkYGcYJdGINSvlmrwC6cSiHY"
   }
   ```


   You should copy all the result value (this is the generated token) in order to invoke the other secured endpoints.

2. After generating the token, you can create a category by invoking:

   `http://localhost:8080/categoryApi/create`
   
    HTTP method: POST
    
    Header parameters:
            ```
            Authorization:Basic dXNlcjpmbGFjb25pQDIwMTg=
            ```
            ```
            Content-Type:application/json
            ```
            ```
            authorizationToken:eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjYXRlZ29yeVN1YmplY3QiLCJleHAiOjE1NDQzMTU1NjZ9.D-_UsI_YiAWSTKXxAlyDc9aQhOZwP71HZzJC4dyeYM0
            ```
    
    Passing as parameter, one of the following JSON payloads:

    2.1 Category without parent category:        
        Request:        
        ```
        {
            "parameter":{"name":"perfume", "slug":"pfm-001", "visible":1}
        }
        ```        
        
    Response:
        ```
        {
            "code": "SUCCESS",
            "result": {
                "id": "f35de9a1-524c-4f5b-a9a3-f2beadf758a3",
                "name": "perfume",
                "slug": "pfm-001",
                "visible": true
            }
        }
        ```
  
    2.2 Category with a parent category:   
        Request:
        `
        {
         "parameter":{"name":"Spicy perfume", "slug":"spicy-pfm-001", "visible":1, "parentCategory":{"id":"0d3114cf-f382-4298-8dd1-b4b4e6e1a3e9"}}
        }
       `
        Response:
     `
        {
            "code": "SUCCESS",
            "result": {
                "id": "f35de9a1-524c-4f5b-a9a3-f2beadf758a3",
                "name": "perfume",
                "slug": "pfm-001",
                "parentCategory": {
                    "id": "0d3114cf-f382-4298-8dd1-b4b4e6e1a3e9",
                    "visible": false
                },
                "visible": true
            }
        }
      `
                
3. Getting a category:
    
    By categoryId: 
        ```
        http://localhost:8080/categoryApi/category?categoryId=0d3114cf-f382-4298-8dd1-b4b4e6e1a3e9
        ```
        
    By slug:
        ```
        http://localhost:8080/categoryApi/category?slug=eau-col-001
        ```
    
    HTTP method: GET
    
    Header parameters:
         ```
         Authorization:Basic dXNlcjpmbGFjb25pQDIwMTg=
         ```
         ```
         Content-Type:application/json
         ```
         ```
         authorizationToken:eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjYXRlZ29yeVN1YmplY3QiLCJleHAiOjE1NDQzMTU1NjZ9.D-_UsI_YiAWSTKXxAlyDc9aQhOZwP71HZzJC4dyeYM0
         ```
        
     For subsequent callings, the client must include the If-None-Match request header with the response header ETag value returned from the server in the previous getting invocation:
        ```
        If-None-Match:"075226ef5962ee906f722f6ad191ad265"
        ```
        
    Response:
 ```
    {
        "code": "SUCCESS",
        "result": {
            "id": "0d3114cf-f382-4298-8dd1-b4b4e6e1a3e9",
            "name": "Eau Cologne",
            "slug": "eau-col-001",
            "parentCategory": {
                "id": "7892955c-8755-4661-bf6b-9acc6b8f2ff4",
                "name": "Cologne",
                "slug": "col-001",
                "visible": false
            },
            "visible": true
        }
    }
```
   If the Resource has not changed on the server, the Response will contain no body and a status code of: 304 - Not Modified

4. Getting the children of a specific category:       
       By categoryId:
            ```
            http://localhost:8080/categoryApi/getCategoryChildren?categoryId=0d3114cf-f382-4298-8dd1-b4b4e6e1a3e9
            ```
       
       By slug:
            `http://localhost:8080/categoryApi/getCategoryChildren?slug=eau-col-001`
      
    HTTP method: GET
       
     Header parameters:
             ```
             Authorization:Basic dXNlcjpmbGFjb25pQDIwMTg=
             ```
             ```
             Content-Type:application/json
             ```
             ```
             authorizationToken:eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjYXRlZ29yeVN1YmplY3QiLCJleHAiOjE1NDQzMTU1NjZ9.D-_UsI_YiAWSTKXxAlyDc9aQhOZwP71HZzJC4dyeYM0
             ```
                    
     For subsequent callings, the client must include the If-None-Match request header with the response header ETag value returned from the server in the previous getting invocation:
  ```
            If-None-Match:"075226ef5962ee906f722f6ad191ad265"
  ```
         
   Response:
```
       {
           "code": "SUCCESS",
           "result": [
               {
                   "id": "2b5aa221-5a19-461a-b83e-10e03b676939",
                   "name": "Floral Cologne",
                   "slug": "flo-col-001",
                   "parentCategory": {
                       "id": "7892955c-8755-4661-bf6b-9acc6b8f2ff4",
                       "name": "Cologne",
                       "slug": "col-001",
                       "visible": false
                   },
                   "visible": true
               },
               {
                   "id": "0d3114cf-f382-4298-8dd1-b4b4e6e1a3e9",
                   "name": "Eau Cologne",
                   "slug": "eau-col-001",
                   "parentCategory": {
                       "id": "7892955c-8755-4661-bf6b-9acc6b8f2ff4",
                       "name": "Cologne",
                       "slug": "col-001",
                       "visible": false
                   },
                   "visible": true
               }
           ]
       }
```
       
   If the Resource has not changed on the server, the Response will contain no body and a status code of: 304 - Not Modified
    
5. Updating the visibility of a given category:

```
    http://localhost:8080/categoryApi/updateVisibility
```

   HTTP method: PATCH
    
   Header parameters:
         ```
         Authorization:Basic dXNlcjpmbGFjb25pQDIwMTg=
         ```
         ```
         Content-Type:application/json
         ```
         ```
         authorizationToken:eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjYXRlZ29yeVN1YmplY3QiLCJleHAiOjE1NDQzMTU1NjZ9.D-_UsI_YiAWSTKXxAlyDc9aQhOZwP71HZzJC4dyeYM0
         ```

   Request:
    ```
    {
     "parameter":{"id":"7892955c-8755-4661-bf6b-9acc6b8f2ff4", "slug":"spicy-pfm-001", "visible":0}
    }
    ```
    
   Response:    
    ```
    {
        "code": "SUCCESS",
        "result": {
            "id": "7892955c-8755-4661-bf6b-9acc6b8f2ff4",
            "slug": "spicy-pfm-001",
            "visible": false
        }
    }
    ```


## More info

You can check all the functionalities exposed by this API in: `http://localhost:8080/swagger-ui.html`

![Categories Microservice REST API (CMRA)](swagger.png "Categories Microservice REST API (CMRA)")


## Built With

* [Maven](https://maven.apache.org/) - Dependency Management


## Authors

* **Liodegar Bracamonte** - *Initial work* - [liodegar@gmail.com)


## License

Apache License 2.0.

## Acknowledgments

* To the all open source software contributors.


