# HL7 v2 redactor API

The HL7v2 redactor API redacts PII data out of the HL7 message. Redacted fields are configurable and is available for deployment via Docker for containerization and scaling.

The Redactor API uses the HL7-PET library to perform redaction on specific fields based on configuration files,It inspects specific fields (defined as HL7 paths, e.g., PID[1]-11.9) for the presence of any value and, if populated, will substitute a value into the field based on the configured rule.

## Sample Report
``` json
{
"report": "(redacted HL7 message.............
,
[RedactInfo(PID-5[
1
].1,Redacted PID-5[
1
].1 with empty value,
null,
3)
])" }

```

## How to Submit A Message for Redactor

Messages can be submitted to the Redactor API using any HTTP communication tool, e.g., Postman, Fiddler, etc. Callers must supply HL7 message in message body as raw plain text.

eg url:  http://localhost:8080/redactor