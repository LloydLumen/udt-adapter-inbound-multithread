package com.udtrucks.adaptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.udtrucks.adaptor.services.MDMService;
import com.udtrucks.adaptor.services.Helpers;
import com.udtrucks.adaptor.services.queue.QueueWorkflowService;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.util.Strings;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;

import static com.udtrucks.adaptor.constants.Constants.*;
import static com.udtrucks.adaptor.services.EmailService.sendEmail;
import static com.udtrucks.adaptor.services.SessionCheck.check;
import static com.udtrucks.adaptor.services.SessionCheck.validateToken;

@Component
public class QueueController {

    private static final Logger LOGGER = LoggerFactory.getILoggerFactory().getLogger("QueueController");
    private String CDIRunID = Strings.EMPTY;
    static String value;

    static {
        try {
            LOGGER.info("Logging into MDM....");
            value = check();
            if (value.equals("")){
                LOGGER.error("Login token is empty, please check the credentials and connectivity.");
                sendEmail("Unable to login into the MDM. Please check the credentials and connectivity.", "Detailed error: Login token is empty.");
            }
            LOGGER.info("Logged into MDM.");
        } catch (UnsupportedEncodingException | JsonProcessingException e) {
            sendEmail("Unable to login into the MDM. Please check the credentials and connectivity.", "Detailed error: "+e.getMessage());
            LOGGER.error("Unable to login the MDM. Please check the credentials and connectivity.");
            LOGGER.error("\nDetailed error: "+e.getMessage());
        }
    }

    @Autowired
    QueueWorkflowService iQueueWorkflowService;
    @Autowired
    MDMService serv;

    String CDI_URI = (String) properties.get("mdm.cdi.customer.url");
    String BROWSED_MESSAGE_FILE_PATH = (String) properties.get("xml.browsed.message.file.path");
    String ERROR_PATH_XML = (String) properties.get("xml.error.file.path");
    String BASE_PATH_XML_FILE = (String) properties.get("xml.base.file.path");
    String TRANSFORM_PATH_XML_FILE = (String) properties.get("xml.transformed.file.path");
    String PRE_TRANSFORM_XML_FILE = (String) properties.get("xml.pre.transformed.file.path");
    String CDI_REQUEST_JSON_FILE = (String) properties.get("mdm.cdi.request.file.path");
    String CDI_REQUEST_append_JSON_FILE = (String) properties.get("mdm.cdi.request.append.file.path");
    String CUSTOMER_INBOUND_BLOB_PATH = (String) properties.get("azure.blob.inbound.path");
    String RURELATION_QUEUE = (String) properties.get("project.queue.rurelation");
    String CUSTOMER_QUEUE = (String) properties.get("project.queue.customer");
    String ACCOUNT_QUEUE = (String) properties.get("project.queue.account");
    String ENV = (String) properties.get("envi.name");

    StringBuilder transformedJSONMessageAppendSendToCDI = new StringBuilder();
    List<Message> AccCustmessageList = new ArrayList<>();
    List<Message> RUmessageList = new ArrayList<>();
    List<String> processedMessageList = new ArrayList<>();

    public List<String> startCustomer(String queueNames) throws Exception {
        long filesTimeStamp = new Date().getTime();
        List<String> fileList = new ArrayList<>();
        List<String> ruFileList = new ArrayList<>();
        transformedJSONMessageAppendSendToCDI = new StringBuilder();
        AccCustmessageList = new ArrayList<>();
        RUmessageList = new ArrayList<>();
        processedMessageList = new ArrayList<>();


        String queueName = Strings.EMPTY;

        String[] queueList = queueNames.split(",");
        //Start polling the messages
        for (String q : queueList) {
            LOGGER.info("Processing queue(s)  --> {}", q);
            //project.mq.poll.queues = q1--q2,q3
            String[] qList = q.split("--");
            try {
                for (String queue : qList) {
                    queueName = queue;
                    if (q.contains("--")) {
                        AccCustmessageList.addAll(iQueueWorkflowService.getMessageListFromMQ(queue));
                    } else {
                        RUmessageList.addAll(iQueueWorkflowService.getMessageListFromMQ(queue));
                    }
                }
            } catch (JMSException e) {
                LOGGER.error("Exception while polling message from the queue - {} because: {}", queueName, e.getMessage());
                sendEmail("Exception while polling message from the queue - "+queueName, "Detailed error: "+e.getMessage());
                return new ArrayList<>();
            }
        }
        LOGGER.info(LOG_FINISHED_POLL);
        LOGGER.info(String.format(LOG_POLLED_MESSAGES, AccCustmessageList.size() + RUmessageList.size()));
        if ((AccCustmessageList.size() + RUmessageList.size()) == 0) {
            return new ArrayList<>();
        }

        /// Write polled messages to File
        try {
            if (AccCustmessageList.size() != 0) {
                fileList = iQueueWorkflowService.writeMessageToFile(AccCustmessageList, BASE_PATH_XML_FILE, filesTimeStamp);
                processedMessageList.addAll(processMessagesFileList(fileList, filesTimeStamp, ACCOUNT_QUEUE));
            }
            if (RUmessageList.size() != 0) {
                ruFileList = iQueueWorkflowService.writeMessageToFile(RUmessageList, BASE_PATH_XML_FILE, filesTimeStamp);
                processedMessageList.addAll(processMessagesFileList(ruFileList, filesTimeStamp, RURELATION_QUEUE));
            }
        } catch (Exception e) {
            LOGGER.error("Exception while writing messages polled from the queue into files because: {}", e.getMessage());
            sendEmail("Exception while writing messages polled from the queue into files.", "Detailed error: "+e.getMessage());
        }
        return processedMessageList;
    }

    public List<String> startOrganization(String queueNames) throws Exception {
        LOGGER.info("Organization");
        return null;
    }

    public List<String> startVehicle(String queueNames) throws Exception {
        LOGGER.info("Vehicle");
        return null;
    }

    public List<String> processBrowsedMessages() {
        long filesTimeStamp = new Date().getTime();
        List<String> fileList = new ArrayList<>();
        List<String> processedMessageList = new ArrayList<>();
        HashMap<String, ArrayList<String>> browseMessageList = iQueueWorkflowService.getBrowsedMessages();
        for (String msgDir : browseMessageList.keySet()) {
            try {
                fileList.addAll(iQueueWorkflowService.writeBrowsedMessageToFile(browseMessageList.get(msgDir), BASE_PATH_XML_FILE, filesTimeStamp));
                processedMessageList.addAll(processMessagesFileList(fileList, filesTimeStamp, msgDir.replace("-", ".")));
            } catch (Exception e) {
                LOGGER.error("Exception while processing browsed messages because: {}", e.getMessage());
                sendEmail("Exception while processing browsed messages.", "Detailed error: "+e.getMessage());
            }
        }
        return processedMessageList;
    }

    private List<String> processMessagesFileList(List<String> fileList, long filesTimeStamp, String queueName) {
        Map<String, String> failedMessageMap = new HashMap<>();
        ArrayList<Document> RUDocs = new ArrayList<>();
        List<String> RUfileList = new ArrayList<>();
        List<String> successMessageList = new ArrayList<>();
        /// Iterate over the polled messages
        fileList.forEach(messageFile -> {
            // Validate Message using XSD
            // TODO: Replace HashMap with a simple DS Eg: ArrayList
            HashMap<Boolean, String> validMap;
            boolean isValid = false;
            String docType = Strings.EMPTY;
            try {
                LOGGER.info("This Block is executed to process SyncAccount and SyncCustomer.");
                validMap = iQueueWorkflowService.validateAndIdentifyDocType(messageFile);
                for (Map.Entry<Boolean, String> val : validMap.entrySet()) {
                    isValid = val.getKey();
                    docType = val.getValue();
                }
                if (!isValid) {
                    LOGGER.error(LOG_XML_VALIDATION_FAILED);
                    failedMessageMap.put(messageFile, LOG_XML_VALIDATION_FAILED);
                    sendEmail(LOG_XML_VALIDATION_FAILED, "Input payload file: "+messageFile);
                    return;
                }
            } catch (Exception e) {
                failedMessageMap.put(messageFile, LOG_XML_VALIDATION_FAILED);
                LOGGER.error(LOG_XML_VALIDATION_FAILED + " because: {}", e.getMessage());
                sendEmail(LOG_XML_VALIDATION_FAILED, "Detailed error: "+e.getMessage());
                return;
            }

            String inputXML = Strings.EMPTY;
            /// Identify the message type
            String messageType;
            try {
                long startTime = System.currentTimeMillis();
                messageType = iQueueWorkflowService.identifyMessage(messageFile);
                LOGGER.info("Identifying the message type of the input message took {}ms.", System.currentTimeMillis() - startTime);
            } catch (Exception e) {
                failedMessageMap.put(messageFile, LOG_MSG_NOT_IDENTIFIED);
                LOGGER.error(LOG_MSG_NOT_IDENTIFIED + " because: {}", e.getMessage());
                sendEmail(LOG_MSG_NOT_IDENTIFIED, "Detailed error: "+e.getMessage());
                return;
            }

            /// Read the input XML message

            if (messageFile != null) {
                long startTime = System.currentTimeMillis();
                Reader fileReader = null;
                try {
                    fileReader = new FileReader(messageFile);
                    BufferedReader bufReader = new BufferedReader(fileReader);
                    StringBuilder sb = new StringBuilder();
                    String line = bufReader.readLine();
                    while (line != null) {
                        sb.append(line).append("\n");
                        line = bufReader.readLine();
                    }
                    inputXML = sb.toString();
                    bufReader.close();
                    LOGGER.info("Reading the Input XML message took {}ms.", System.currentTimeMillis() - startTime);
                } catch (IOException e) {
                    LOGGER.error("Exception while reading the input message because of {}-{}\n", e.getClass().toString(), e.getMessage());
                    sendEmail("Exception while reading the input payload.", "Input payload file: "+messageFile+"\nDetailed error: "+e.getMessage());
                    failedMessageMap.put(messageFile, "UNABLE TO READ THE INPUT MESSAGE");
                    return;
                } finally {
                    try {
                        if (fileReader != null) {
                            fileReader.close();
                        }
                    } catch (IOException e) {
                        return;
                    }
                }
            }

            /// Get Root Element for message classification - Only for RU - Condition - SyncAccount + ResponsibleUnit Tags
            String partyID;
            String customerpartyID;
            Document doc;

            try {
                long startTime = System.currentTimeMillis();
                doc = iQueueWorkflowService.getXMLDocument(inputXML);
                LOGGER.info("Extracting XML DOC instance took {}ms.", System.currentTimeMillis() - startTime);
            } catch (ParserConfigurationException | IOException | SAXException e) {
                LOGGER.error("Exception while extracting XML DOC instance because of {}-{}\n", e.getClass().toString(), e.getMessage());
                sendEmail("Exception while extracting XML DOC instance.", "Input payload file: "+messageFile+"\nDetailed error: "+e.getMessage());
                return;
            }
            customerpartyID = iQueueWorkflowService.getCustomerPartyID(doc, messageType, filesTimeStamp);
            LOGGER.info("The customer PartyID is --> {} || Action: {}.", customerpartyID, messageType);
            if (messageType.equals("SyncAccount")) {
                try {
                    // check if the tag "md:ResponsibleUnitRelationships" is present in the XML input string
                    NodeList nodeList = doc.getElementsByTagName("md:ResponsibleUnitRelationships");
                    if (nodeList.getLength() > 0) {
                        messageType = "SyncRURelationship";
                        RUDocs.add(doc);
                        RUfileList.add(messageFile);
                    }
                } catch (Exception e) {
                    String errorMsg = "Exception while pre-processing the input message for PartyID - " + customerpartyID + " because ---> \n" + e.getClass() + "\nDetailed error: "+e.getMessage();
                    LOGGER.error(errorMsg);
                    failedMessageMap.put(messageFile, "EXCEPTION WHILE PRE-PROCESSING");
                    sendEmail("Exception while pre-processing the input message for PartyID - " + customerpartyID, "Input payload file: "+messageFile+"\nDetailed error: "+e.getMessage());
                    return;
                }
            }

            if (!messageType.equals("SyncRURelationship")) {
                // Fetch XSL File path from Message Properties
                String XSLFilePath = Strings.EMPTY;
                try {
                    XSLFilePath = iQueueWorkflowService.prepareMessageProperties(docType + "." + messageType);
                } catch (Exception e) {
                    failedMessageMap.put(messageFile, LOG_MSG_NOT_IDENTIFIED);
                    LOGGER.error(LOG_MSG_NOT_IDENTIFIED + " because ---> {}", e.getMessage());
                    sendEmail(LOG_MSG_NOT_IDENTIFIED, "Input payload file: "+messageFile+"\nDetailed error: "+e.getMessage());
                    return;
                }

                if (XSLFilePath == null) {
                    LOGGER.error(LOG_CONFIG_NOT_FOUND);
                    failedMessageMap.put(messageFile, LOG_CONFIG_NOT_FOUND);
                    sendEmail(LOG_CONFIG_NOT_FOUND +" for message type: "+messageType, "Input payload file: "+messageFile);
                    return;
                }

                /// Transform Message using XSL
                String transformedXML = Strings.EMPTY;
                try {
                    long startTime = System.currentTimeMillis();
                    if (isValid) {
                        if (!inputXML.isEmpty()) {
                            Path preTransformXMLFilePath = iQueueWorkflowService.writeDataToFile(inputXML, PRE_TRANSFORM_XML_FILE, SUFFIX_XML, customerpartyID, filesTimeStamp);
                            transformedXML = iQueueWorkflowService.transformMessage(TRANSFORM_PATH_XML_FILE, preTransformXMLFilePath, XSLFilePath, customerpartyID, filesTimeStamp, ENV);
                            if (!transformedXML.equals("") && new File(transformedXML).exists()) {
                                LOGGER.info(LOG_XML_TRANSFORM_COMPLETE);
                            } else {
                                LOGGER.error("The transformed XML data is empty.");
                                sendEmail("The transformed XML data is empty.", "Input payload file: "+messageFile);
                                failedMessageMap.put(messageFile, "TRANSFORMED XML IS EMPTY");
                                return;
                            }
                            LOGGER.info("Transforming input XML using respective XSL took {}ms.", System.currentTimeMillis() - startTime);
                        }
                    }
                } catch (Exception e) {
                    failedMessageMap.put(messageFile, LOG_XML_TRANSFORM_FAILED);
                    LOGGER.error(LOG_XML_TRANSFORM_FAILED + " because ---> {}", e.getMessage());
                    sendEmail(LOG_XML_TRANSFORM_FAILED, "Input payload file: "+messageFile+"\nDetailed error: "+e.getMessage());
                    return;
                }

                /// Convert Message to JSON
                JSONObject transformedXMLtoJSON = new JSONObject();
                if (!transformedXML.equals("") && !transformedXML.trim().isEmpty()) {
                    try {
                        long startTime = System.currentTimeMillis();
                        LOGGER.info("Converting transformed XML to JSON....");
                        transformedXMLtoJSON = XML.toJSONObject(new FileReader(transformedXML));
                        value = validateToken();
                        String customerDetails = "";
                        try {
                            customerDetails = serv.searchCustomer(value, customerpartyID);
                        }catch (Exception e) {
                            LOGGER.error("{}: {}" + Helpers.ERROR_MSG,e.getMessage());
                            sendEmail(Helpers.ERROR_MSG, e.getMessage());
                        }
                        if (!customerDetails.equals("")) {
                            JSONObject customerObject = new JSONObject(customerDetails);
                            String businessID_market = "";
                            String businessID_salesDealer = "";
                            String businessID_serviceDealer = "";
                            String businessID_legal = "";
                            JSONArray known_as_object= null;
                            JSONArray segment_object= null;

                            if (customerObject.has("X_UDT_KnownAs")) {
                                known_as_object = customerObject.getJSONArray("X_UDT_KnownAs");
                            }

                            if (customerObject.has("X_UDT_Segment")) {
                                segment_object = customerObject.getJSONArray("X_UDT_Segment");
                            }

                            if (customerObject.has("x_udt_custmarkt_cust_org")) {
                                businessID_market = customerObject.getJSONObject("x_udt_custmarkt_cust_org")
                                        .getString("_businessId");
                            }
                            if (customerObject.has("x_udt_custslsdlr_cust_org")) {
                                businessID_salesDealer = customerObject.getJSONObject("x_udt_custslsdlr_cust_org")
                                        .getString("_businessId");
                            }
                            if (customerObject.has("x_udt_custsvcdlr_cust_org")) {
                                businessID_serviceDealer = customerObject.getJSONObject("x_udt_custsvcdlr_cust_org")
                                        .getString("_businessId");
                            }
                            if (customerObject.has("x_udt_custlgl_cust_cust")) {
                                businessID_legal = customerObject.getJSONObject("x_udt_custlgl_cust_cust")
                                        .getString("_businessId");
                            }

                            JSONObject marketObj = new JSONObject();
                            marketObj.put("businessId", businessID_market);

                            JSONObject sales_Obj = new JSONObject();
                            sales_Obj.put("businessId", businessID_salesDealer);

                            JSONObject service_Obj = new JSONObject();
                            service_Obj.put("businessId", businessID_serviceDealer);

                            JSONObject legal_Obj = new JSONObject();
                            legal_Obj.put("businessId", businessID_legal);

                            JSONObject rootObj = transformedXMLtoJSON.getJSONObject("root");

                            rootObj.put("X_UDT_KnownAs", known_as_object);
                            rootObj.put("X_UDT_Segment", segment_object);
                            rootObj.put("x_udt_custmarkt_cust_org", marketObj);
                            rootObj.put("x_udt_custslsdlr_cust_org", sales_Obj);
                            rootObj.put("x_udt_custsvcdlr_cust_org", service_Obj);
                            rootObj.put("x_udt_custlgl_cust_cust", legal_Obj);
                            transformedXMLtoJSON.put("root", rootObj);
                        }
                        else{
                            LOGGER.error("Customer details not found for Customer PartyID - {}. Proceeding to create a new Customer.",customerpartyID);
                        }
                        LOGGER.info("Conversion of transformed XML to JSON took {}ms.", System.currentTimeMillis() - startTime);
                    } catch (Exception e) {
                        LOGGER.error(LOG_XML_TO_JSON_FAILED + " because ---> {}", e.getMessage());
                        failedMessageMap.put(messageFile, LOG_XML_TO_JSON_FAILED);
                        sendEmail(LOG_XML_TO_JSON_FAILED, "Input payload file: "+messageFile+"\nDetailed error: "+e.getMessage());
                        return;
                    }
                    if (transformedXMLtoJSON.isEmpty()) {
                        failedMessageMap.put(messageFile, LOG_XML_TO_JSON_FAILED);
                        LOGGER.error(LOG_XML_TO_JSON_FAILED);
                        sendEmail(LOG_XML_TO_JSON_FAILED, "Input payload file: "+messageFile+"\nDetailed error: Converted JSON to XML data is empty.");
                        return;
                    }
                }

                JSONObject rootObj = transformedXMLtoJSON.getJSONObject("root");
                rootObj.remove("xmlns:xalan");
                rootObj.remove("xmlns:java");
                rootObj.remove("xmlns:udt");
                rootObj.remove("xmlns:md");
                rootObj.remove("xmlns:volvo");
                rootObj.remove("xmlns:xsi");
                Instant dtToInstant = Instant.now();
                long epochTime = dtToInstant.toEpochMilli();
                rootObj.put("Src_last_update", epochTime);
                transformedXMLtoJSON.put("root", rootObj);

                /// write request JSON into a directory
                Path requestCDIJsonPath = iQueueWorkflowService.writeDataToFile(transformedXMLtoJSON.toString(), CDI_REQUEST_JSON_FILE, SUFFIX_JSON, customerpartyID, filesTimeStamp);
                transformedJSONMessageAppendSendToCDI.append(transformedXMLtoJSON).append(System.getProperty("line.separator"));
                successMessageList.add(messageFile);
            }
        });

        if (!transformedJSONMessageAppendSendToCDI.toString().equals("") && !queueName.equals(RURELATION_QUEUE)) {
            String blobName = BATCH_PROCESS_PREFIX + queueName.replace(".", "-") + "-" + filesTimeStamp + SUFFIX_TXT;
            Path transformedAppendedMessagePath = iQueueWorkflowService.writeAppendedDataToFile(transformedJSONMessageAppendSendToCDI.toString(), CDI_REQUEST_append_JSON_FILE, blobName);
            boolean uploaded = false;
            try{
                /// Upload Message to Blob Storage
                uploaded = iQueueWorkflowService.uploadMessageFileToBlob(transformedAppendedMessagePath.toFile().getPath(), CUSTOMER_INBOUND_BLOB_PATH + File.separatorChar + blobName);
            }catch (Exception e){
                LOGGER.error(Helpers.ERROR_MSG+"->{}",e.getMessage());
                sendEmail(Helpers.ERROR_MSG+"->",e.getMessage());
            }
            if (!uploaded) {
                failedMessageMap.put(transformedAppendedMessagePath.toFile().getPath(), LOG_BLOB_FAILURE);
                LOGGER.error(LOG_BLOB_FAILURE);
            } else {
                try {
                    JSONObject cdiPaylaod = new JSONObject();
                    cdiPaylaod.put("srcFileName", blobName);
                    while (!CDIRunID.equals("")) {
                        value = validateToken();
                        String CDIJobStatus = serv.getCDIJobStatus(value, CDIRunID);
                        if (!CDIJobStatus.equals("RUNNING")) {
                            CDIRunID = Strings.EMPTY;
                        } else {
                            Thread.sleep(30000);
                        }
                    }
                    String cdiResponseMessage = iQueueWorkflowService.invokeCDIRequestForMessage(CDI_URI, HttpMethod.POST.name(), cdiPaylaod.toString());
                    if (!cdiResponseMessage.equals("") && !cdiResponseMessage.trim().isEmpty()) {
                        LOGGER.info("Response form CDI for submitted job: {}", cdiResponseMessage);
                        JSONObject CDIJobResponse = new JSONObject(cdiResponseMessage);
                        CDIRunID = CDIJobResponse.getString("RunId");
                    } else {
                        failedMessageMap.put(transformedAppendedMessagePath.toFile().getPath(), LOG_CDI_FAILURE);
                        LOGGER.error(LOG_CDI_FAILURE + " the CDI invocation response is empty.");
                        sendEmail(LOG_CDI_FAILURE, "CDI request batch file (Payloads appended): "+transformedAppendedMessagePath.toFile().getPath()+"\nDetailed error: Response from CDI invocation call is empty.");
                    }
                } catch (Exception e) {
                    failedMessageMap.put(transformedAppendedMessagePath.toFile().getPath(), LOG_CDI_FAILURE);
                    LOGGER.error(LOG_CDI_FAILURE + " because ---> {}", e.getMessage());
                    sendEmail(LOG_CDI_FAILURE, "CDI request batch file (Payloads appended): "+transformedAppendedMessagePath.toFile().getPath()+"\nDetailed error: "+e.getMessage());
                }
            }
        }


        //wait for the previous CDI job to complete
        //write the waiting loop
        while (!CDIRunID.equals("")) {
            try {
                value = validateToken();
                String CDIJobStatus = "";
                try {
                    CDIJobStatus = serv.getCDIJobStatus(value, CDIRunID);
                }catch (Exception e){
                    LOGGER.error("GET CDI job status call failed due to an exception: {}"+e.getMessage());
                    sendEmail(Helpers.ERROR_MSG,e.getMessage());
                }
                if (!CDIJobStatus.equals("RUNNING")) {
                    CDIRunID = Strings.EMPTY;
                } else {
                    Thread.sleep(30000);
                }
            } catch (Exception e) {
                sendEmail("CDI job status check failed.", "Detailed error: "+e.getMessage());
                LOGGER.error("CDI job status check failed because ---> {}", e.getMessage());
            }


        }

        for (int i = 0; i < RUDocs.size(); i++) {
            Document doc = RUDocs.get(i);
            String messageFile = RUfileList.get(i);
            NodeList nodeList = doc.getElementsByTagName("md:ResponsibleUnitRelationships");
            HashMap<Boolean, String> validMap;
            boolean isValid = false;
            String docType = Strings.EMPTY;
            String messageType = "SyncRURelationship";
            String inputXML = Strings.EMPTY;
            String customerpartyID = iQueueWorkflowService.getCustomerPartyID(doc, filesTimeStamp);

            if (messageFile != null) {
                long startTime = System.currentTimeMillis();
                Reader fileReader = null;
                try {
                    fileReader = new FileReader(messageFile);
                    BufferedReader bufReader = new BufferedReader(fileReader);
                    StringBuilder sb = new StringBuilder();
                    String line = bufReader.readLine();
                    while (line != null) {
                        sb.append(line).append("\n");
                        line = bufReader.readLine();
                    }
                    inputXML = sb.toString();
                    bufReader.close();
                    LOGGER.info("Reading the Input XML message took {}ms.", System.currentTimeMillis() - startTime);
                } catch (IOException e) {
                    LOGGER.error("Exception while reading the input message because of {}-{}\n", e.getClass().toString(), e.getMessage());
                    failedMessageMap.put(messageFile, "UNABLE TO READ THE MESSGAE");
                    sendEmail("Exception while reading the input payload.", "Input payload file: "+messageFile + "\nDetailed error: "+e.getMessage());
                    continue;
                } finally {
                    try {
                        if (fileReader != null) {
                            fileReader.close();
                        }
                    } catch (IOException e) {
                        LOGGER.error("Exception while closing file because of {}-{}\n", e.getClass().toString(), e.getMessage());
                    }
                }
            }

            try {
                LOGGER.info("This Block is executed to process RURelationship.");
                validMap = iQueueWorkflowService.validateAndIdentifyDocType(messageFile);
                for (Map.Entry<Boolean, String> val : validMap.entrySet()) {
                    isValid = val.getKey();
                    docType = val.getValue();
                }
                if (!isValid) {
                    LOGGER.error(LOG_XML_VALIDATION_FAILED);
                    failedMessageMap.put(messageFile, LOG_XML_VALIDATION_FAILED);
                    sendEmail("SyncRURelation failed - "+LOG_XML_VALIDATION_FAILED, "Input payload file: "+messageFile);
                    continue;
                }
            } catch (Exception e) {
                failedMessageMap.put(messageFile, LOG_XML_VALIDATION_FAILED);
                LOGGER.error(LOG_XML_VALIDATION_FAILED + " because: {}", e.getMessage());
                sendEmail("SyncRURelation failed - "+LOG_XML_VALIDATION_FAILED, "Input payload file: "+messageFile+"\nDetailed error: "+e.getMessage());
                continue;
            }
            String customerCountryCode = Strings.EMPTY;
            try {
                if (nodeList.getLength() > 0) {
                    Element ruElement = (Element) nodeList.item(0);
                    /// Update messageType to SyncAccountRURelation
                    LOGGER.info("Processing a valid RuRelationship message...");
                    //customer party ID used to make get call on MDM
//                    String customerGetCallPartyID = iQueueWorkflowService.getCustomerPartyID(doc);
                    if (!customerpartyID.equals("") && !customerpartyID.trim().isEmpty()) {
                        long startTime = System.currentTimeMillis();
                        value = validateToken();
                        String orgCallResponse = serv.searchOrganization(value, CUSTOMER_I_URL_ID_BE, customerpartyID);
                        LOGGER.info("Fetching respective businessID from MDM for the given partyID took {}ms.", System.currentTimeMillis() - startTime);
                        String customerBusinessId = Strings.EMPTY;
                        if (!orgCallResponse.equals("") && !orgCallResponse.trim().isEmpty()) {
                            JSONObject orgResponse = new JSONObject(orgCallResponse);
                            JSONObject resultObj = orgResponse.getJSONObject("searchResult");
                            if (!resultObj.get("hits").equals(0)) {
                                /// Extract BusinessID value from the Search Result Response
                                customerBusinessId = resultObj.getJSONArray("records")
                                        .getJSONObject(0)
                                        .getJSONObject("_meta")
                                        .getString("businessId");
                            }else{
                                LOGGER.error("No Customer details found for customer (Get call) Party ID: {}", customerpartyID);
                                failedMessageMap.put(messageFile, "CUSTOMER DETAILS NOT FOUND");
                                sendEmail("SyncRURelation failed - No Customer found to link RU | Customer Party ID - " +customerpartyID, "");
                                continue;
                            }
                        }
                        else{
                            LOGGER.error("No Customer details found for customer (Get call) Party ID: {}", customerpartyID);
                            failedMessageMap.put(messageFile, "CUSTOMER DETAILS NOT FOUND");
                            sendEmail("SyncRURelation failed - No Customer found to link RU | Customer Party ID - " +customerpartyID, "");
                            continue;
                        }

                        /// Patch MDM customer details with the associated organization businessId
                        if (!customerBusinessId.equals("")) {
                            value = validateToken();
                            String customerCallResponse = "{root:" + serv.getCallMDM(value, CUSTOMER_I_URL_ID_BE, customerBusinessId) + "}";
                            LOGGER.debug("Response from customer GET call --> \n" + customerCallResponse);
                            if (!customerCallResponse.trim().isEmpty()) {
                                JSONObject customerResponse = new JSONObject(customerCallResponse);
                                JSONObject resultObj = customerResponse.getJSONObject("root");

                                /// Extract Country Code value from the Search Result Response
                                customerCountryCode = resultObj.getJSONArray("X_UDT_Address_Custom")
                                        .getJSONObject(0)
                                        .getJSONObject("X_UDT_country")
                                        .getString("Code");
                                String partyIDNode = "<md:PartyID>" + customerpartyID + "</md:PartyID>";
                                String countryCodeXML = "<md:PartyID>" + customerpartyID + "</md:PartyID><CountryCode>" + customerCountryCode + "</CountryCode>";
                                if (!customerCountryCode.equals("") && !customerCountryCode.trim().isEmpty()) {
                                    inputXML = inputXML.replace(partyIDNode, countryCodeXML);
                                    LOGGER.info("Replaced the respective partyID {} with businessID {} from MDM search response.", partyIDNode, countryCodeXML);
                                }
                            }else{
                                LOGGER.error("No customer details found for customer business ID: {}", customerBusinessId);
                                failedMessageMap.put(messageFile, "CUSTOMER DETAILS NOT FOUND");
                                sendEmail("SyncRURelation failed - No Customer found to link RU | Customer Business ID - " +customerBusinessId, "");
//                                continue;
                            }
                        }
                    }

                    // Fetch XSL File path from Message Properties
                    String XSLRURelationshipFilePath;
                    String XSLRURelationshipFilterFilePath;
                    try {
                        XSLRURelationshipFilePath = iQueueWorkflowService.prepareMessageProperties(docType + "." + messageType);
                    } catch (Exception e) {
                        failedMessageMap.put(messageFile, LOG_MSG_NOT_IDENTIFIED);
                        LOGGER.error(LOG_MSG_NOT_IDENTIFIED + "because ---> {}", e.getMessage());
                        sendEmail(LOG_CONFIG_NOT_FOUND +" for message type: "+messageType, "Input payload file: "+messageFile+"\nDetailed error: "+e.getMessage());
                        continue;
                    }

                    if (XSLRURelationshipFilePath == null) {
                        LOGGER.error(LOG_CONFIG_NOT_FOUND);
                        failedMessageMap.put(messageFile, LOG_CONFIG_NOT_FOUND);
                        sendEmail(LOG_CONFIG_NOT_FOUND +" for message type: "+messageType, "Input payload file: "+messageFile);
                        continue;
                    }

                    try {
                        XSLRURelationshipFilterFilePath = iQueueWorkflowService.prepareMessageProperties(docType + "." + "SyncRURelationFilter");
                    } catch (Exception e) {
                        failedMessageMap.put(messageFile, LOG_MSG_NOT_IDENTIFIED);
                        LOGGER.error(LOG_MSG_NOT_IDENTIFIED + "because ---> {}", e.getMessage());
                        sendEmail(LOG_CONFIG_NOT_FOUND +" for message type: "+messageType, "Input payload file: "+messageFile+"\nDetailed error: "+e.getMessage());
                        continue;
                    }

                    if (XSLRURelationshipFilterFilePath == null) {
                        LOGGER.error(LOG_CONFIG_NOT_FOUND);
                        failedMessageMap.put(messageFile, LOG_CONFIG_NOT_FOUND);
                        sendEmail(LOG_CONFIG_NOT_FOUND +" for message type: "+messageType, "Input payload file: "+messageFile);
                        continue;
                    }

                    /// Transform Message using XSL
                    String transformedRURelationshipXML = Strings.EMPTY;
                    try {
                        long startTime = System.currentTimeMillis();
                        if (isValid) {
                            if (!inputXML.isEmpty()) {
                                Path preTransformXMLFilePath = iQueueWorkflowService.writeDataToFile(inputXML, PRE_TRANSFORM_XML_FILE, SUFFIX_XML, customerpartyID, filesTimeStamp);
                                transformedRURelationshipXML = iQueueWorkflowService.transformMessage(TRANSFORM_PATH_XML_FILE, preTransformXMLFilePath, XSLRURelationshipFilePath, customerpartyID, filesTimeStamp, ENV);
                                if (!transformedRURelationshipXML.equals("") && new File(transformedRURelationshipXML).exists()) {
                                    LOGGER.info(LOG_XML_TRANSFORM_COMPLETE);
                                } else {
                                    LOGGER.error("The transformed XML data is empty.");
                                    sendEmail("The transformed XML data is empty.", "Input payload file: "+messageFile);
                                    failedMessageMap.put(messageFile, "TRANSFORMED XML IS EMPTY");
                                }
                                LOGGER.info("Transforming input XML using respective XSL took {}ms.", System.currentTimeMillis() - startTime);
                            }
                        }
                    } catch (Exception e) {
                        failedMessageMap.put(messageFile, LOG_XML_TRANSFORM_FAILED);
                        LOGGER.error(LOG_XML_TRANSFORM_FAILED + " because ---> {}", e.getMessage());
                        sendEmail(LOG_XML_TRANSFORM_FAILED, "Input payload file: "+messageFile+"\nDetailed error: "+e.getMessage());
                        continue;
                    }

                    String transformedXMLStr = FileUtils.readFileToString(new File(transformedRURelationshipXML));

                    /*Search Organization logic and send email if organization does not exist*/

                    Document searchOrgDoc = iQueueWorkflowService.getXMLDocument(transformedXMLStr);

                    List<String> searchOrgPartyIDs = iQueueWorkflowService.searchOrganizationPartyIDExists(searchOrgDoc);
                    LOGGER.info("The RU PartyID is --> {} || Action: {}.", searchOrgPartyIDs, messageType);



                    /// Search MDM to get Organization PartyId to Business ID equivalent
                    if (searchOrgPartyIDs != null && !searchOrgPartyIDs.isEmpty()) {
                        long startTime = System.currentTimeMillis();
                        value = validateToken();
                        List<String> businessIds = new ArrayList<String>();
                        for (String searchOrgPartyID : searchOrgPartyIDs) {
                            try {
                                String orgCallResponse = serv.ruSearchOrganization(value, ORGANIZATION_I_URL_ID_BE, searchOrgPartyID);
                                LOGGER.info("Fetching respective businessID from MDM for the given partyID took {}ms.", System.currentTimeMillis() - startTime);
                                String businessId = Strings.EMPTY;
                                if (!orgCallResponse.equals("") && !orgCallResponse.trim().isEmpty()) {
                                    JSONObject orgResponse = new JSONObject(orgCallResponse);
                                    JSONObject resultObj = orgResponse.getJSONObject("searchResult");
                                    if ((int) resultObj.get("hits") == 1) {
                                        /// Extract BusinessID value from the Search Result Response
                                        businessId = resultObj.getJSONArray("records")
                                                .getJSONObject(0)
                                                .getJSONObject("_meta")
                                                .getString("businessId");
                                        if (!businessId.equals("") && !businessId.trim().isEmpty()) {
                                            businessIds.add(businessId);
                                            LOGGER.info("Replaced the respective partyID {} with businessID {} from MDM search response.", searchOrgPartyID, businessId);
                                        }
                                    } else if ((int) resultObj.get("hits") > 1) {
                                        LOGGER.error("More than one organization record found for the given partyID - {}, Related to Customer partyID - {}.", searchOrgPartyID, customerpartyID);
                                        sendEmail("More than one organization record found for the given partyID - " + searchOrgPartyID + ", Related to Customer partyID - " + customerpartyID, "Input payload file: " + messageFile);
                                        continue;
                                    } else {
                                        //TODO:zero Hits
                                        LOGGER.error("SyncRURelation failed -  Given RU record not found in MDS | Customer PartyID - {} ; RU PartyID - {} ",customerpartyID, searchOrgPartyID);
                                        sendEmail("SyncRURelation failed -  Given RU record not found in MDS | Customer PartyID - " + customerpartyID+" ; RU PartyID - <b>" + searchOrgPartyID + "</b>\n\n<h4 style=\"color:Tomato;\"><b><em>This PartyID Can be Included in the Exclusion List</em></b></h4>", "Input payload file: " + messageFile);
                                    }
                                } else {
                                    LOGGER.error("SyncRURelation failed -  Given RU record not found in MDS | Customer PartyID - {} ; RU PartyID - {} ",customerpartyID, searchOrgPartyID);
                                    sendEmail("SyncRURelation failed -  Given RU record not found in MDS | Customer PartyID - " + customerpartyID+" ; RU PartyID - <b>" + searchOrgPartyID + "</b>\n\n<h4 style=\"color:Tomato;\"><b><em>This PartyID Can be Included in the Exclusion List</em></b></h4>", "Input payload file: " + messageFile);
                                }
                            } catch (Exception e) {
                                sendEmail("Exception Has Occured Because :",e.getMessage());
                                throw new RuntimeException(e);
                                //TODO: send email
                            }
                        }
                        //TODO: logic for making patch call
                        /// Patch MDM customer details with the associated organization businessId
                        if (!businessIds.isEmpty()) {
                            postCDIPatchProcessor(customerpartyID,businessIds.get(0),successMessageList,messageFile,failedMessageMap);
                        }else{
                            //TODO: Apply The Default fo the related Country
                            // Exclusion List Default Country
                            applyDefaultPartyID(customerpartyID,messageType,successMessageList,messageFile,failedMessageMap,customerCountryCode);

                        }
                    }else{
                        //TODO:apply default partyID
                        applyDefaultPartyID(customerpartyID,messageType,successMessageList,messageFile,failedMessageMap,customerCountryCode);
                    }
                    /*=========================================================================*/

                }else{
                    sendEmail("No RUPartyID found.", "Stacktrace: NoRUPartyID found in the input message. \n Input payload file: "+messageFile);
                    LOGGER.error("NoRUPartyID found in the input message for input payload - {}", messageFile);
                    continue;
                }
            } catch (Exception e) {
                LOGGER.error("Exception while processing the RURelationship input message for PartyID - " + customerpartyID + " because ---> \n{}-{}",e.getClass(),e.getMessage());
                failedMessageMap.put(messageFile, "EXCEPTION WHILE PROCESSING RURELATIONSHIP MESSAGE");
                sendEmail("Exception while processing the RURelationship input message for PartyID - " + customerpartyID, "Input payload file: "+messageFile+"\nDetailed error: "+e.getMessage());
            }
        }

        // Copy failed messages to Directory
        if (!failedMessageMap.isEmpty()) {
            File basePath = new File(ERROR_PATH_XML);
            if (!basePath.exists()) {
                basePath.mkdir();
            }
            failedMessageMap.entrySet().forEach(m -> {
                String fileName = m.getKey();
                String error = failedMessageMap.get(fileName);// TODO: Use this to identify or classify the failure reason
                File errorPath = new File(Paths.get(basePath.getPath(), fileName.replace("CDB" + File.separatorChar, "")).toFile().getPath());
                try {
                    FileUtils.copyFile(new File(fileName), errorPath);
                    LOGGER.info("Error file copied from {} to {} and the error is due to {}.", fileName, errorPath.getPath(), error);
                } catch (IOException e) {
                    LOGGER.error("Could not copy error file from {} to {}.", fileName, errorPath.getPath());
                    sendEmail("Could not copy error file.", "Detailed error: Could not copy error file from "+fileName+" to "+errorPath.getPath());
                }
            });
        }

        //purge browsed messages directory respective to the polled queue
        if(queueName.equals(ACCOUNT_QUEUE)){
            purgeFiles(Paths.get(BROWSED_MESSAGE_FILE_PATH + File.separatorChar + ACCOUNT_QUEUE.replace(".", "-")));
            purgeFiles(Paths.get(BROWSED_MESSAGE_FILE_PATH + File.separatorChar + CUSTOMER_QUEUE.replace(".", "-")));
        }else{
            purgeFiles(Paths.get(BROWSED_MESSAGE_FILE_PATH + File.separatorChar + queueName.replace(".", "-")));
        }

        LOGGER.info("x-------------- POLL RESULTS ---------x");
        LOGGER.info("| Total polled messages     :      {} |", fileList.size());
        LOGGER.info("| Total failed messages     :      {} |", failedMessageMap.size());
        LOGGER.info("| Total successful messages :      {} |", successMessageList.size());
        LOGGER.info("x-------------------------------------x");
        return successMessageList;
    }

    private void purgeFiles(Path filesPath){
        try {
            Files.walk(filesPath)
                    .sorted(java.util.Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(java.io.File::delete);
            LOGGER.info("Directory and Files Deleted");
        } catch (NoSuchFileException x) {
            LOGGER.error("{}: no such file or directory \n", filesPath);
        } catch (DirectoryNotEmptyException x) {
            LOGGER.error("{} not empty.", filesPath);
        } catch (IOException x) {
            sendEmail("Unable to purge files in mq-browsed-messages directory for the given file path "+filesPath, "Detailed error: "+x.getMessage());
            LOGGER.error("Exception while purging files from browsed messages directory for respective queue " + filesPath + " because: \n" + x);
        }
    }

    public  void postCDIPatchProcessor(String customerpartyID,String businessId, List successMessageList, String messageFile, Map failedMessageMap) throws Exception {
        while (!CDIRunID.equals("")) {
            value = validateToken();
            String CDIJobStatus = serv.getCDIJobStatus(value, CDIRunID);
            if (!CDIJobStatus.equals("RUNNING")) {
                CDIRunID = Strings.EMPTY;
            } else {
                Thread.sleep(30000);
            }
        }
        value = validateToken();
        if (serv.patchCustomerMDM(value, customerpartyID, businessId)) {
            LOGGER.info("MDM Patch Request successful.");
            successMessageList.add(messageFile);
        } else {
            LOGGER.error("MDM Patch request unsuccessful.");
            failedMessageMap.put(messageFile, "MDM PATCH REQUEST FAILED");
            sendEmail("MDM Patch request unsuccessful for the Customer partyID - "+ customerpartyID, "Input payload file: "+messageFile);
        }
    }

    public void applyDefaultPartyID(String customerpartyID, String messageType, List<String> successMessageList, String messageFile, Map failedMessageMap, String customerCountryCode) throws Exception {
        long startTime = System.currentTimeMillis();
        String exclusionListXMLFilePath = Strings.EMPTY;
        String defaultPartyId = Strings.EMPTY;
        try {
            exclusionListXMLFilePath = iQueueWorkflowService.prepareMessageProperties("common.exclusion.list");
        } catch (Exception e) {
            failedMessageMap.put(messageFile, LOG_CONFIG_NOT_FOUND);
            LOGGER.error(EXCLUSION_LIST_NOT_FOUND+" because ---> {}", e.getMessage());
            sendEmail(EXCLUSION_LIST_NOT_FOUND, "Input payload file: "+messageFile+"\nDetailed error: "+e.getMessage());
        }

        if (exclusionListXMLFilePath == null) {
            LOGGER.error(LOG_CONFIG_NOT_FOUND);
            failedMessageMap.put(messageFile, LOG_CONFIG_NOT_FOUND);
            sendEmail(LOG_CONFIG_NOT_FOUND +" for message type: "+messageType, "Input payload file: "+messageFile);
            return;
        }
        Document exclusionDoc = iQueueWorkflowService.getExclusionXMLDocument(exclusionListXMLFilePath);
        XPath countryXPath =  XPathFactory.newInstance().newXPath();
        String tagXPathExpression = "//Countrys/Country[@code='"+customerCountryCode+"']/DefaultPartyID";
        NodeList countryNodeList = (NodeList) countryXPath.compile(tagXPathExpression).evaluate(exclusionDoc, XPathConstants.NODESET);
        if(countryNodeList.getLength() > 0){
            defaultPartyId= String.valueOf(countryNodeList.item(0).getTextContent());
            try {
                String orgCallResponse = serv.ruSearchOrganization(value, ORGANIZATION_I_URL_ID_BE, defaultPartyId);
                LOGGER.info("Fetching respective businessID from MDM for the given partyID took {}ms.", System.currentTimeMillis() - startTime);
                String businessId = Strings.EMPTY;
                if (!orgCallResponse.equals("") && !orgCallResponse.trim().isEmpty()) {
                    JSONObject orgResponse = new JSONObject(orgCallResponse);
                    JSONObject resultObj = orgResponse.getJSONObject("searchResult");
                    if ((int) resultObj.get("hits") == 1) {
                        /// Extract BusinessID value from the Search Result Response
                        businessId = resultObj.getJSONArray("records")
                                .getJSONObject(0)
                                .getJSONObject("_meta")
                                .getString("businessId");
                        if (!businessId.equals("") && !businessId.trim().isEmpty()) {
                            LOGGER.info("Replaced the respective partyID {} with businessID {} from MDM search response.", defaultPartyId, businessId);
                        }
                    } else if ((int) resultObj.get("hits") > 1) {
                        LOGGER.error("More than one organization record found for the given partyID - {}, Related to Customer partyID - {}.", defaultPartyId, customerpartyID);
                        sendEmail("More than one organization record found for the given partyID - " + defaultPartyId + ", Related to Customer partyID - " + customerpartyID, "Input payload file: " + messageFile);
                        return;
                    } else {
                        //TODO:zero Hits
                        LOGGER.error("SyncRURelation failed -  Given RU record not found in MDS | Customer PartyID - {} ; RU PartyID - {} ",customerpartyID, defaultPartyId);
                        sendEmail("SyncRURelation failed -  Given RU record not found in MDS | Customer PartyID - " + customerpartyID+" ; RU PartyID - <b>" + defaultPartyId + "</b>\n\n<h4 style=\"color:Tomato;\"><b><em>This PartyID Can be Included in the Exclusion List</em></b></h4>", "Input payload file: " + messageFile);
                    }
                    if(!businessId.equals("") && !businessId.trim().isEmpty()) {
                        postCDIPatchProcessor(customerpartyID, businessId, successMessageList, messageFile, failedMessageMap);
                    }

                } else {
                    LOGGER.error("SyncRURelation failed -  Given RU record not found in MDS | Customer PartyID - {} ; RU PartyID - {} ",customerpartyID, defaultPartyId);
                    sendEmail("SyncRURelation failed -  Given RU record not found in MDS | Customer PartyID - " + customerpartyID+" ; RU PartyID - <b>" + defaultPartyId + "\n\n<h4 style=\"color:Tomato;\"><b><em>This PartyID Can be Included in the Exclusion List</em></b></h4>", "Input payload file: " + messageFile);
                }
            } catch (Exception e) {
                sendEmail("Exception Has Occured Because :",e.getMessage());
                throw new RuntimeException(e);
                //TODO: send email
            }
        }
    }
}