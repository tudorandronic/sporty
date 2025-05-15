# sporty

A service to start/stop the retrieval of scores from another mock service based
on a scheduler that calls for scores each 10 seconds.
KeyComponents:
* SportyEventController - starts/stops the retrieval once an event is received.
* SportyEventScheduler - retrieves the live events and calls for scores for each event every 10 second.
* ExternalMockService - a mock for the rest service that is called from sporty for scores.
* KafkaMessagePublisher - for each live event we get the score from the mock service and call the kafka publisher to send the message to the sporty-events-topic

## Pre-requisites
* Maven 3.8.6
* Java 21
* Kafka - you can follow the steps here https://manishankarjaiswal.medium.com/installing-and-running-apache-kafka-on-windows-a-step-by-step-guide-part-1-7e957942e841
* Postman/curl - to perform rest calls

## Build
```bash
mvn clean install
```

## Running the app locally
```bash
mvn spring-boot:run
```

## Running the tests
```bash
mvn test
```

## How to send a live/not live event to sporty service

### 1. Run the spring boot sporty app
```bash
mvn spring-boot:run
```

### 2. Start the Kafka Environment

For the bat files listed you need to search in your kafka instalation folder or follow 
the kafka link above.

#### Start zookeper

zookeeper-server-start.bat .\config\zookeeper.properties

#### Start the kafka server

kafka-server-start.bat .\config\server.properties

#### Create the event-scores-topic kafka topic

kafka-topics.bat --create --topic event-scores-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

### 3. Use Postman / curl

#### Postman
In Postman create a new POST request.
For the link use: http://localhost:8080/api/events/status .
For the body use raw and add the following example json 
{
"eventId":"1",
"status":"true"
} .
Click on Send and there you have it, you have a live sporty event created.
Each 10 seconds the app will poll the mock service for scores and send everything
to the event-scores-topic.

#### curl

```bash
curl --location "http://localhost:8080/api/events/status" ^
--header "Content-Type: application/json" ^
--data "{\"eventId\":\"1\",\"status\":\"true\"}"

```
