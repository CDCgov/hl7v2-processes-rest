# HL7 v2 JSON Transformer API

The HL7v2 JsonTransformer API is designed to generate HL7 Json for a given HL7 message.

Lib-bumblebee was used to transform HL7 into JSON based on a pre-defined template and a HL7 Profile, and uses HL7-PET to extract information out of the HL7 to populate the JSON template.

Currently, the HL7-JSON uses a single profile.The Profile is native to the HL7 PET, i.e., the HL7-PET library knows how to parse and understand a profile and use it for multiple functionalities.
The driver of the transformation is the profile being used. Therefore, HL7 JSON can only transform hl7 segments exists in profile.

## How to Submit A Message to jsonTranformer

Messages can be submitted to the transformer API using any HTTP communication tool, e.g., Postman, Fiddler, etc. Callers must supply HL7 message in message body as raw plain text.

eg url:  http://localhost:8080/jsonTransformer
