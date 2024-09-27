# HL7 v2 redactor API

The HL7v2 redactor API redacts PII data out of the HL7 message. Redacted fields are configurable and is available for deployment via Docker for containerization and scaling.

The Redactor API uses the HL7-PET library to perform redaction on specific fields based on configuration files,It inspects specific fields (defined as HL7 paths, e.g., PID[1]-11.9) for the presence of any value and, if populated, will substitute a value into the field based on the configured rule.

## Sample Report
``` json
{
 "redacted_message=": "(redacted HL7 message............."
,"redaction_report": {
    [
        {
            "path": "PID-5[1].1",
            "field_index": 1,
            "message": "Redacted PID-5[1].1 with empty value",
            "line_number":  3
        }
    ] 
    }
}
```

## Running the Docker images

This project is built as a docker image and deployed on Quay.io.
You can find the image here: https://quay.io/repository/us-cdcgov/cdc-dex/hl7-redactor

To run the image locally, simply pull the image

```
   docker pull quay.io/us-cdcgov/cdc-dex/hl7-redactor
```

and run it with a similar command below:
```
docker run -p 8080:8080 quay.io/us-cdcgov/cdc-dex/hl7-redactor
```

## How to Submit A Message for Redactor

Messages can be submitted to the Redactor API using any HTTP communication tool, e.g., Postman, Fiddler, etc. Callers must supply HL7 message in message body as raw plain text.

eg url:  http://localhost:8080/redactor

P.S.: a Postman collection is available under src/test/postman for you to import locally and try the service.