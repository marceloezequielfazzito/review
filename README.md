# Coupon service

Service for managing and applying coupons to Baskets 

### Prerequisites

#### [openjdk version 11](https://openjdk.org/projects/jdk/11/)

#### [Gradle 6.2.2](https\://services.gradle.org/distributions/gradle-6.2.2-bin.zip)

### Compiling, Building and Testing

On project root folder using gradle wrapper

```bash
./gradlew clean build
```

It will create coupon-app.jar executable jar file inside folder build/libs

### Running

once build

```bash
java -jar build/libs/coupon-app.jar
```

it will expose port 8080


# How to use

- The service is in charge of managing applying coupons to Baskets through restful API

# swagger server is available in url 

http://{server}:{port}/swagger-ui/index.html#/

example

http://localhost:8080/swagger-ui/index.html#/

### coupons by codes

Get all coupons by code 

```
GET http://[server]:[port]/api/v1/coupons?codes=[COUPON-CODE,COUPON-CODE]  - mandatory query params code

example 

http://localhost:8080/api/v1/coupons?codes=TEST1,TEST2,TEST3
```
response 200 ok

```json
[
  {
    "discount": 10.00,
    "code": "TEST1",
    "minBasketValue": 50.00
  },
  {
    "discount": 15.00,
    "code": "TEST2",
    "minBasketValue": 100.00
  },
  {
    "discount": 20.00,
    "code": "TEST3",
    "minBasketValue": 200.00
  }
]
```

### create new Coupon

creates a new coupon , coupon code must be unique

```
POST http://[server]:[port]/api/v1/coupons

example 

http://localhost:8080/api/v1/coupons

```
request body

```json
{
  "discount":50.00,       mandatory
  "code":"test4",         mandatory
  "minBasketValue":0.00   mandatory
}

```
response 200 OK

```json

{
  "discount": 50.00,
  "code": "TEST4",
  "minBasketValue": 0.00
}

```

response 400 BAD REQUEST Coupon code is invalid

response 409 CONFLICT Coupon already exists

### apply Coupon to Basket

apply coupon to basket 

- basket value must be greater than coupon minBasketValue 
- basket value must be greater than zero
- basket value must be greater than coupon discount

```
POST http://[server]:[port]/api/v1/coupons/{COUPON ID}/apply

example 

http://localhost:8080/api/v1/coupons/TEST3/apply

```
request body

```json

{
  "value":100.00
}

```
response 200 OK

```json

{
  "value": 90.00,
  "appliedDiscount": 10.00,
  "applicationSuccessful": true
}

```

response 400 BAD REQUEST Coupon not found

response 409 CONFLICT could not apply coupon to requested Basket

