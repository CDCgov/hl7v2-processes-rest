# HL7 v2 Validation API

The HL7v2 Validation API is designed to structure validate DAART messages against the applicable profiles and standards. 5 DAART profiles are loaded under resources.

If a message is submitted to validate, the full report of structural errors and warnings is returned back to the caller. This detailed report includes the location and description of each error and warning found within the message.

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
"stackTrace": null,
"metaData": null
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
## How to Submit A Message to Validate

Messages can be submitted to the Validator API using any HTTP communication tool, e.g., Postman, Fiddler, etc. Callers must supply batch or single HL7 message in message body as raw plain text.

eg url:  http://localhost:8080/validate