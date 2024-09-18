## HL7 v2 Debatcher API

The HL7v2 Debatcher API uses the [HL7-PET](https://github.com/cdcgov/hl7-pet) library to efficiently split batch of HL7 messages into individual messages and provides a total count of the messages in the report. This API is available for deployment via Docker for streamlined containerization and scaling.

### Sample Report
``` json
{
  "total_items": 10,
  "items": [
    {
      "item_number": 1,
      "item_content": "MSH....."
    },
    {
      "item_number": 2,
      "item_content": "MSH....
    }
    ....
  ]
}
```

## Running the Docker images

This project is built as a docker image and deployed on Quay.io.
You can find the image here: https://quay.io/repository/us-cdcgov/cdc-dex/hl7-debatcher

To run the image locally, simply pull the image 

```
   docker pull quay.io/us-cdcgov/cdc-dex/hl7-debatcher
```

and run it with a similar command below:
```
docker run -p 8080:8080 quay.io/us-cdcgov/cdc-dex/hl7-debatcher
```

## How to Submit A Message for Debatch

Messages can be submitted to the Debatcher API using any HTTP communication tool, e.g., Postman, Fiddler, etc. Callers must supply batch or single HL7 message in message body as raw plain text.

eg url:  http://localhost:8080/debatch

P.S.: a Postman collection is available under src/test/postman for you to import locally and try the service.