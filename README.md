# DeliveryHero Restaurants CRUD service
Task description: ![task](TASK.md)
 
## Solution
I wanted to create a lightweight system that provides the full functionality but without the bloatware.
To achieve this I took the the decision not to use a framework like Play Framework but instead a more lightweight suite of libraries like:
 - `Akka-Http` - for the server side communication because its lightweight and fast
 - `Slick` - a functional way to write SQL commands
 - `HikariCP` - a high performance JDBC connection pool
 - `Forklift` 
   - A database migration tool that can work with Slick's DSL
   - I wanted to try the Database migration that has the longest paragraph on Slick's page: http://slick.lightbend.com/doc/3.2.2/migrations.html
       - I am  not a huge fan of it :)
 - I decided not to go with `cats`/`scalaz` because they bring to much overhead for this simple app
 - I chose `akka-http-spray-json` instead of `circe` or other marshallers and unmarshallers because it's lightweight and doesn't depend on `cats`/`scalaz`
 - No third party dependency injection libraries
  
# Prerequisites
 - clone the project
 - docker with docker-compose
 - scala and sbt (only for development)

# Running
 - ensure that docker is running
 - check local port `9000` is free
 - In the root directory perform: `docker-compose up` ( add `-d` option to run in detach mode )

# For development
 - bring up a postgres container(or other jdbc compatible databases)
    `docker run  -e "POSTGRES_PASSWORD=mysecretpassword" -p 5432:5432 -d postgres:10`
    - all the database connection configuration can be changed in `app/src/main/resources/application.conf`
 - start `sbt` (+1.0) in the root folder (`dh_restaurants`)
 - init the migration table with the command `mg init`
 - perform continuous migrations with the command `~mg migrate`
 - start Http server by calling `run`
 - if everything went well you should see the following line in the sbt logs:
    ```INFO  com.victor.restaurant.HttpService$ - ServerBinding(/0:0:0:0:0:0:0:0:9000)```

# New docker image
 - to create a new docker image with the code change:
    - run in sbt `docker:publishLocal`
    - change the service accordingly in `docker-coompose.yml`

# Testing
 - integration tests:
    - in `app\src\it\scala`
    - docker daemon should be running
    - it will bring up a postgres container up
    - run them by calling `it:test`
 - test:
    - in `app\src\test\scala`
    - the RestaurantRepo is Mocked with a `TrieMap`
    - run them by calling `test` 

## API documentation
By default the service is running on `localhost:9000` 

`GET /api/v1/restaurants`
  - retrieved all the restaurants in the database
  - in future versions if the database has to many entries, this should be paginated
  - response:
    - `OK(200)` ->
```js
[
	{
		"id": 1,
		"name": "Restaurant Name 1",
		"phoneNumber": "0123456789",
		"description": "Restaurant Description 1",
		"address": "Address 1",
		"cuisinesOffered": "CuisinesOffered 1"
	},
	{
		"id": 2,
		"name": "Restaurant Name 2",
		"phoneNumber": "0123456789",
		"description": "Restaurant Description 2",
		"address": "Address 2",
		"cuisinesOffered": "CuisinesOffered 2"
	},
	...
]
```
 
`GET /api/v1/restaurants/{Long}`
  - get restaurant specified by id(Long) from the database
  - response:
    - `OK(200)` ->
```js
{
       "id": 1,
       "name": "Restaurant Name 1",
       "phoneNumber": "0123456789",
       "description": "Restaurant Description 1",
       "address": "Address 1",
       "cuisinesOffered": "CuisinesOffered 1"
}
```

    - `NotFound(404)` -> if no restaurant with the specified id was found

```js
{
		"message": "No restaurant found for the given id.",
		"code": "no_restaurant_found"
}
```

`POST /api/v1/restaurants`
  - add restaurant to the database
  - basic validation provided, should be extended with time
  - request body:
```js
{
		"name": "Restaurant Name 1",
		"phoneNumber": "0123456789",
		"description": "Restaurant Description 1",
		"address": "Address 1",
		"cuisinesOffered": "CuisinesOffered 1"
}
```
  - response:
    - `OK(200)` -> the id of the restaurant in the database (debatable if it should be `Created(201)`)
```js
{
	"restaurantId": 23
}
```
    - `BadRequst(400)` -> if any validation of the restaurant failed(all fields should be non empty and there should be no 'id')
```js
[
	{
		"message": "The 'phoneNumber' canno't be empty.",
		"code": "invalid_empty_phoneNumber"
	},
	{
		"message": "The 'cuisinesOffered' canno't be empty.",
		"code": "invalid_empty_cuisinesOffered"
	}
	...
]
```
   
`PUT /api/v1/restaurants`
  - update a restaurant in the database base on its id
  - request body:
```js
{
        "id": 1,
		"name": "Restaurant Name 5", 
		"phoneNumber": "0123456729",
		"description": "Restaurant Description 321",
		"address": "Address 12",
		"cuisinesOffered": "CuisinesOffered 3"
}
```
  - response:
    - `OK(200)` -> if the updated succeeded, the saved entity in the database
```js
{
        "id": 1,
		"name": "Restaurant Name 5", 
		"phoneNumber": "0123456729",
		"description": "Restaurant Description 321",
		"address": "Address 12",
		"cuisinesOffered": "CuisinesOffered 3"
}
```
    - `NotFound(404)` -> if no restaurant with the specified id was found
    
```js
{
		"message": "No restaurant found for the given id.",
		"code": "no_restaurant_found"
}
```
    - `BadRequest(400)` -> if any validation of the restaurant failed(all fields should be non empty including the 'id')
```js
[
	{
		"message": "The 'phoneNumber' canno't be empty.",
		"code": "invalid_empty_phoneNumber"
	},
	{
		"message": "The 'cuisinesOffered' canno't be empty.",
		"code": "invalid_empty_cuisinesOffered"
	}
	...
]
```

`DELTE /api/v1/restaurants/{Long}`
  - delete the restaurant with the specific id
  - response:
    - `OK(200)` ->  the id of the deleted restaurant
```js
{
	"restaurantId": 6
}
```
    - `NotFound(404)` -> if no restaurant with the specified id was found
```js
{
		"message": "No restaurant found for the given id.",
		"code": "no_restaurant_found"
}
```
`GET /api/v1/healthcheck`
    - checks the state of the database, its Configuration and the uptime of the service
    - response:
       - `OK(200)` ->
```js
{
	"upTime": 86737,
	"dbUp": true, //or false
	"dbConfig": "..."
}
```
 *All request might also return other `5xx` if there are issues with the db or other services*

# Next
 - Add swagger support
 - Create a simple way for the dev environment
 - Do the migrations in the dev environment via forklift (maybe decide for a nicer migration tool)
