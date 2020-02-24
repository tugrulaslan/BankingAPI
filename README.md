# BankingAPI
This simple API tests account retrieval and money transfer

# Requirements
- Java 8+
- Maven 3+

# Building the Project
Use maven command line `mvn clean install` to build the project. The same command will run all the Unit and Integration Tests.

# Running the Project
The project had embbeded Tomcat 9. Once the above build step is done, you can easily issue java command `java -jar BankingAPI.jar` in the target folder.

# Endpoints
The project consists of two endpoints that will perform different operations. Each endpoint and its respective test cases are given.
The examples utilize the CURL tool. You can preferably use any of your favorite 

## Data Retrieval
- Success Case `curl -i http://localhost:8080/accounts/1`
- Error Case – Account Does Not Exist `curl -i http://localhost:8080/accounts/3`

## Money Transfer
- Success Case `curl -i -X POST http://localhost:8080/accounts/1/transfers/ -d "{\"targetAccountId\":\"2\",\"amount\":\"530.11\"}"`
- Error Case – Source Account Does Not Exist `curl -i -X POST http://localhost:8080/accounts/3/transfers/ -d "{\"targetAccountId\":\"2\",\"amount\":\"530.11\"}"`
- Error Case – Target Account Does Not Exist `curl -i -X POST http://localhost:8080/accounts/1/transfers/ -d "{\"targetAccountId\":\"3\",\"amount\":\"530.11\"}"`
- Error Case – Source Account Has Insufficient Funds `curl -i -X POST http://localhost:8080/accounts/1/transfers/ -d "{\"targetAccountId\":\"2\",\"amount\":\"95530.11\"}"`
