# HL7 v2 Validation API

This project serves as an example of how to perform validation on HL7 messages based on IGAMT profiles.
In this implementation, we loaded 5 DAART profiles that are used depending of the values on MSH-21.1 and MSH-6.

This project is dependent of the [lib-hl7v2-nist-validator](https://github.com/cdcgov/lib-hl7v2-nist-validator)
We are using the default profileFetcher to fetch profiles from the project's resource folders.
Other Profile Fetchers can be implemented to retrieve profiles form cloud storage or databases.

If a message is submitted to validate, the full report of structure errors and warnings is returned back 
to the caller. This detailed report includes the location and description of each error and warning found 
within the message.

## Sample Report
``` json
{
"entries": {
  "structure": [
    {
        "line": 8,
        "column": 1,
        "path": "OBX[1]-25[1]",
        "description": "Field OBX-25 (Performing Organization Medical Director) is missing. Depending on the use case and data availability it may be appropriate to value this element (Usage is RE, Required, but may be Empty).",
        "category": "Usage",
        "classification": "Warning",
    }
    .....
    ],
        "content": [],
        "value-set": []
    },
    "error-count": {
        "structure": 0,
        "value-set": 0,
        "content": 0
    },
    "warning-count": {
        "structure": 416,
        "value-set": 0,
        "content": 0
    },
    "status": "VALID_MESSAGE"
}
```



## Running the Docker images

This project is built as a docker image and deployed on Quay.io.
You can find the image here: https://quay.io/repository/us-cdcgov/cdc-dex/hl7-validator

To run the image locally, simply pull the image

```
   docker pull quay.io/us-cdcgov/cdc-dex/hl7-validator
```

and run it with a similar command below:
```
docker run -p 8080:8080 quay.io/us-cdcgov/cdc-dex/hl7-validator
```
## How to Submit A Message to Validate

Messages can be submitted to the Validator API using any HTTP communication tool, e.g., Postman, Fiddler, etc. Callers must supply batch or single HL7 message in message body as raw plain text.

eg url:  http://localhost:8080/validate

P.S.: a Postman collection is available under src/test/postman for you to import locally and try the service.