## HL7 v2 Debatcher API

The HL7v2 Debatcher API utilizes the HL7-PET library to efficiently split batch of HL7 messages into individual messages and provides a total count of the messages in the report. This API is available for deployment via Docker for streamlined containerization and scaling.

## Sample Report
``` json
{
"total_items": 10,
"items": [
{
"item_number": 1,
"item_content": "MSH.....},
{
"item_number": 2,
"item_content": "MSH....}
....
]
}
```
## How to Submit A Message for Debatch

Messages can be submitted to the Debatcher API using any HTTP communication tool, e.g., Postman, Fiddler, etc. Callers must supply batch or single HL7 message in message body as raw plain text.

eg url:  http://localhost:8080/debatch