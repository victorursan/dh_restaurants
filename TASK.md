# Coding Exercise

## 1. Goals				
Create a restful API using any framework of choice, which provides an interface for restaurants stored in a database. The API should be self contained and easily runnable. 

## 2. API Application
Create a restful api application with the endpoints below. A restaurant should support the following data fields:
 - Name
 - Phone number
 - Cuisines Offered
 - Address
 - Description

The API should have the following endpoints:

`GET /restaurants/<id>`
 - Gets a given restaurant

`GET /restaurants`
 - Gets the list of all stored available restaurants

`POST /restaurants`
 - Create a restaurant

`PUT /restaurants`
 - Updates a restaurant

`DELETE /restaurants`
 - Delete a restaurant

The API-Application can be a simple as possible. The restaurants can be saved in a storage of choice, whether it is a file, cloud, sqlite or any database.

## 3. Deliverables
 - add code in the github under your user account
 - provide a simple README file that explains how to use the application, including simple documentation about your API
 - use unit testing to test the various components of the application
 - ensure that the api responds with proper json  error codes and responses
 - use async code throughout
 - use a logger instead instead of print
 - use docker build and start the application
 - add a simple application health check endpoint `/v1/healthcheck`

## 4. Conclusion
We value code quality and functional code. Thank you for the time you invested in the exercise.

