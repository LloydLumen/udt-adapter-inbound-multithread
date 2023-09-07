
# Adaptor Inbound Logic
MQ to MDM VIA Adaptor

1. Poll XML mesaages from IBM MQ: Done
2. Parse incoming XML to know what type of message it is (it is applicable only for Customer)
3. Convert incoming XML to MDM XML (using XSLT)
4. Convert MDM XML into MDM JSON
5. Upload MDM json to blob storage
6. Using retry mechanism framework invoke CDI service. 
   i. Request is successful, end the workflow 
   ii. Request is unsuccessful, log the message, end the workflow

Start Inbound Adapter application with an externalised application.properties
> java -Dfile.encoding=UTF-8 -jar target\adaptor.inbound.jar --spring.config.location=file:/E:/udt-adaptor-inbound-properties/application.properties
> java -Dfile.encoding=UTF-8 -jar target\adaptor.inbound.jar --spring.config.location=file:/C:/Users/U100257x/Documents/udt-adaptor-inbound-properties/application.properties

## URL FOR IBM MQ LOCAL
https://localhost:9443/ibmmq/console/


## Exclusion List related updates
1.Resource folder both volvo and UDT XSL and ExclusionList folder
2. message.properties
2. code 