package com.udtrucks.adaptor.services.queue;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import com.udtrucks.adaptor.PasswordUtils;
import com.udtrucks.adaptor.config.MessageTypeConfiguration;
import com.udtrucks.adaptor.services.MDMService;
import com.udtrucks.adaptor.services.Helpers;
import com.udtrucks.commons.worker.CommWorker;
import com.udtrucks.commons.worker.XMLWorker;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.jms.Queue;
import javax.jms.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.udtrucks.adaptor.constants.Constants.*;
import static com.udtrucks.adaptor.services.EmailService.sendEmail;

@Service
public class QueueWorkflowService {
    private static final Logger LOGGER = LoggerFactory.getILoggerFactory().getLogger("QueueWorkflowService");
    @Autowired
    private com.udtrucks.adaptor.services.azure.blob.IAzureBlobService iAzureBlobService;
    private XMLWorker xmlWorker;

    String MALFORMED_XML_FILE = (String) properties.get("xml.malformed.file.path");

    public ArrayList<Message> getMessageListFromMQ(String queueName) throws JMSException {
        String mq_host = (String) properties.get("project.mq.host");
        int mq_port = Integer.valueOf((String) properties.get("project.mq.port"));
        String mq_queue_manager = (String) properties.get("project.mq.queue-manager");
        String mq_channel = (String) properties.get("project.mq.channel");
        String mq_username = (String) properties.get("project.mq.username");
        String mq_password = (String) properties.get("project.mq.password");
        String mq_ssl_keystore = (String) properties.get("project.mq.ssl.keystore");
        String mq_ssl_truststore = (String) properties.get("project.mq.ssl.truststore");
        String mq_ssl_keystorepassword = (String) properties.get("project.mq.ssl.keystorepassword");
        String mq_ssl_truststorepassword = (String) properties.get("project.mq.ssl.truststorepassword");
        String mq_ssl_ciphermappings = (String) properties.get("project.mq.ssl.ciphermappings");
        String mq_ssl_ciphersuite = (String) properties.get("project.mq.ssl.ciphersuite");
        long startTime = System.currentTimeMillis();
        Connection conn = null;

        Session sess;
        ArrayList<Message> msgs = new ArrayList<>();

        System.setProperty("javax.net.ssl.keyStore", mq_ssl_keystore);
        System.setProperty("javax.net.ssl.trustStore", mq_ssl_truststore);
        System.setProperty("javax.net.ssl.trustStorePassword", new String(PasswordUtils.decodePassword(mq_ssl_truststorepassword)));
        System.setProperty("javax.net.ssl.keyStorePassword", new String(PasswordUtils.decodePassword(mq_ssl_keystorepassword)));
        System.setProperty("com.ibm.mq.cfg.useIBMCipherMappings", mq_ssl_ciphermappings);

        try {
            MQConnectionFactory cf = new MQConnectionFactory();
            cf.setQueueManager(mq_queue_manager);
            cf.setHostName(mq_host);
            cf.setPort(mq_port);
            cf.setChannel(mq_channel);
            cf.setTransportType(WMQConstants.WMQ_CM_CLIENT);
            cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
//            cf.setSSLCipherSuite(mq_ssl_ciphersuite);

            conn = cf.createConnection(mq_username, new String(PasswordUtils.decodePassword(mq_password)));
            LOGGER.info("created connection to MQ " + conn);

            conn.start();
            sess = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

            LOGGER.info("Polling queue --> {}", queueName);
            Queue queue = sess.createQueue(queueName);

            //Execute browse queue operation
            browseQueueAndWriteToFile(sess, queue, queueName);

            //Consume messages from queue in receive mode
            MessageConsumer consumer = sess.createConsumer(queue);
            Message message;
            while ((message = consumer.receiveNoWait()) != null) {
                msgs.add(message);
            }
            return msgs;
        } catch (Exception e) {
            LOGGER.error("Exception while polling the messages from MQ queue because ---> {}", e.getMessage());
        } finally {
            assert conn != null;
            conn.close();
        }
        LOGGER.info("Polling messages took {}ms.", System.currentTimeMillis() - startTime);
        return msgs;
    }

    public List<String> writeMessageToFile(List<Message> messageList, String fileBaseDir, long filesTimeStamp) {
        long startTime = System.currentTimeMillis();
        List<String> fileList = new ArrayList<>();
        HashMap<String, String> checkpointMap = new HashMap<String, String>();
        HashMap<String, String> fileListMap = new HashMap<String, String>();
        AtomicBoolean addToFileList = new AtomicBoolean(false);
        File baseDir = new File(fileBaseDir);
        if (!baseDir.exists()) {
            baseDir.mkdir();
        }
        messageList.forEach(message -> {
            addToFileList.set(false);
            // Write XML message to file
            if (message != null) {
                String mess = Strings.EMPTY;
                String partyID = Strings.EMPTY;
                try {
                    mess = message.getBody(String.class);
                } catch (JMSException e) {
                    LOGGER.error("Unable to process the polled message because: {}",e.getMessage());
                    LOGGER.error("Invalid input message: \n{}"+mess);
                    sendEmail("Invalid input polled message.", "Stacktrace: Unable to process the polled message because:\n" +e.getMessage());
                    return;
                }
                try {
                    Document inputDoc = getXMLDocument(mess);
                    partyID = getCustomerPartyID(inputDoc, filesTimeStamp);
                    LOGGER.info("The customer PartyID is --> " + partyID);
                    String msgTS = getMessageTimestamp(inputDoc);
                    if (msgTS.equals("")){
                        LOGGER.error("No CreationDateTime found in the input message. Malformed XML payload --> \n {}",mess);
                        sendEmail("Malformed XML input payload.", "Stacktrace: No CreationDateTime found in the input message. Please check the error log for more details.");
                        Path malformedXMLFilePath = writeDataToFile(mess, MALFORMED_XML_FILE, SUFFIX_XML, "", filesTimeStamp);
                        LOGGER.info("Malformed input payload written into {} directory and the file path is - {}", MALFORMED_XML_FILE, malformedXMLFilePath);
                        return;
                    }
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
                    Instant currentTS = Instant.from(formatter.parse(msgTS));
                    Instant prevTS = null; // Initialize prevTS to null

                    if (checkpointMap.containsKey(partyID)) {
                        String prevTimestamp = checkpointMap.get(partyID);
                        if (!prevTimestamp.isEmpty()) {
                            prevTS = Instant.from(formatter.parse(prevTimestamp));
                        }
                    }

                    if (prevTS == null || currentTS.compareTo(prevTS) > 0) {
                        checkpointMap.put(partyID, msgTS);
                        addToFileList.set(true);
                    }
                } catch (ParserConfigurationException | SAXException | IOException e) {
                    LOGGER.error("Invalid or malformed XML message: {}", mess);
                    sendEmail("Malformed XML input payload.", "Stacktrace: "+e.getMessage());
                    Path malformedXMLFilePath = writeDataToFile(mess, MALFORMED_XML_FILE, SUFFIX_XML, "", filesTimeStamp);
                    LOGGER.info("Malformed input payload written into {} directory and the file path is - {}", MALFORMED_XML_FILE, malformedXMLFilePath);
                    return;
                }
                String fileName = partyID + "-" + filesTimeStamp + "-" + new Date().getTime() + SUFFIX_XML;
                Path filePath = Paths.get(baseDir.getPath(), fileName);
                LOGGER.info("Writing to path:" + filePath);
                File xmlFile = new File(String.valueOf(filePath));
                try {
                    FileUtils.writeStringToFile(xmlFile, mess);
                    if(addToFileList.get()){
                        fileListMap.put(partyID, xmlFile.getPath());
                    }
                } catch (IOException e) {
                    LOGGER.error("Unable to write the message to file because: {}", e.getMessage());
                }
            }
        });
        fileList.addAll(fileListMap.values());
        LOGGER.info("Writing message to the files took {}ms.", System.currentTimeMillis() - startTime);
        return fileList;
    }

    public List<String> writeBrowsedMessageToFile(List<String> messageList, String fileBaseDir, long filesTimeStamp) {
        long startTime = System.currentTimeMillis();
        List<String> fileList = new ArrayList<>();
        HashMap<String, String> checkpointMap = new HashMap<String, String>();
        HashMap<String, String> fileListMap = new HashMap<String, String>();
        AtomicBoolean addToFileList = new AtomicBoolean(false);
        File baseDir = new File(fileBaseDir);
        if (!baseDir.exists()) {
            baseDir.mkdir();
        }
        messageList.forEach(messagePath -> {
            addToFileList.set(false);
            // Write XML message to file
            if (messagePath != null) {
                String message = Strings.EMPTY;
                String partyID;
                try {
                    message = new String(Files.readAllBytes(Paths.get(messagePath)), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    LOGGER.error("Unable to process the polled message because: {}",e.getMessage());
                    LOGGER.error("Polled invalid input message: \n{}"+message);
                    return;
                }
                try {
                    Document inputDoc = getXMLDocument(message);
                    partyID = getCustomerPartyID(inputDoc, filesTimeStamp);
                    LOGGER.info("The customer PartyID is --> " + partyID);
                    String msgTS = getMessageTimestamp(inputDoc);
                    if (msgTS.equals("")){
                        LOGGER.error("No CreationDateTime found in the input message. Malformed XML payload --> \n {}",message);
                        sendEmail("Malformed XML input payload.", "Stacktrace: No CreationDateTime found in the input message. Please check the error log for more details.");
                        Path malformedXMLFilePath = writeDataToFile(message, MALFORMED_XML_FILE, SUFFIX_XML, "", filesTimeStamp);
                        LOGGER.info("Malformed input payload written into {} directory and the file path is - {}", MALFORMED_XML_FILE, malformedXMLFilePath);
                        return;
                    }
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
                    Instant currentTS = Instant.from(formatter.parse(msgTS));
                    Instant prevTS = null; // Initialize prevTS to null

                    if (checkpointMap.containsKey(partyID)) {
                        String prevTimestamp = checkpointMap.get(partyID);
                        if (!prevTimestamp.isEmpty()) {
                            prevTS = Instant.from(formatter.parse(prevTimestamp));
                        }
                    }

                    if (prevTS == null || currentTS.compareTo(prevTS) > 0) {
                        checkpointMap.put(partyID, msgTS);
                        addToFileList.set(true);
                    }
                } catch (ParserConfigurationException | SAXException | IOException e) {
                    LOGGER.error("Invalid or malformed XML message: {}", message);
                    sendEmail("Malformed XML input payload.", "Stacktrace: "+e.getMessage());
                    Path malformedXMLFilePath = writeDataToFile(message, MALFORMED_XML_FILE, SUFFIX_XML, "", filesTimeStamp);
                    LOGGER.info("Malformed input payload written into {} directory and the file path is - {}", MALFORMED_XML_FILE, malformedXMLFilePath);
                    return;
                }
                String fileName = partyID + "-" + filesTimeStamp + "-" + new Date().getTime() + SUFFIX_XML;
                Path filePath = Paths.get(baseDir.getPath(), fileName);
                LOGGER.info("Writing to path:" + filePath);
                File xmlFile = new File(String.valueOf(filePath));
                try {
                    FileUtils.writeStringToFile(xmlFile, message);
                    if(addToFileList.get()){
                        fileListMap.put(partyID, xmlFile.getPath());
                    }
                } catch (IOException e) {
                    LOGGER.error("Unable to write the message to file because: {}", e.getMessage());
                }
            }
        });
        fileList.addAll(fileListMap.values());
        LOGGER.info("Writing message to the files took {}ms.", System.currentTimeMillis() - startTime);
        return fileList;
    }

    public HashMap<String,ArrayList<String>> getBrowsedMessages(){
        String BROWSED_MESSAGE_FILE_PATH = (String) properties.get("xml.browsed.message.file.path");
        HashMap<String, ArrayList<String>> messagesMap = new HashMap<>();
        File dir = new File(BROWSED_MESSAGE_FILE_PATH);
        if (dir.exists() && dir.isDirectory()){
            File[] subDirs = dir.listFiles(File::isDirectory);

            for (File subDir : subDirs){
                File[] files = subDir.listFiles();
                ArrayList<String> filesList = new ArrayList<>();
                for (File file : files){
                    filesList.add(file.getAbsolutePath());
                }
                messagesMap.put(subDir.getName().replace("-","."), filesList);
            }
        }
        return messagesMap;
    }

    public Document getXMLDocument(String inputXML) throws ParserConfigurationException, IOException, SAXException {
        // create a DocumentBuilder instance to parse the XML input string
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        builder = factory.newDocumentBuilder();
        // parse the XML input string into a Document object
        Document doc = builder.parse(new InputSource(new StringReader(inputXML)));
        return doc;
    }

    public Document getExclusionXMLDocument(String inputXML) throws ParserConfigurationException, IOException, SAXException {
        // create a DocumentBuilder instance to parse the XML input string
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        builder = factory.newDocumentBuilder();
        Document doc = null;
        try {
             doc = builder.parse(Files.newInputStream(Paths.get(new File(inputXML).getAbsolutePath())));
        }catch (Exception e){
            throw e;
        }
        return doc;
    }

    private String getMessageTimestamp(Document doc) {
        long startTime = System.currentTimeMillis();
        String msgTS = Strings.EMPTY;
        // Find the <udt:CreationDateTime> or <volvo:CreationDateTime> element
        NodeList msgTSList;
        msgTSList = doc.getElementsByTagName("volvo:CreationDateTime");
        if (msgTSList.getLength() < 1){
            msgTSList = doc.getElementsByTagName("udt:CreationDateTime");
        }
        else if (msgTSList.getLength() < 1) {
            LOGGER.error("No CreationDateTime found in the input message payload.");
        }else{
            Element partyIDElement;
            partyIDElement = (Element) msgTSList.item(0);
            if (partyIDElement != null) {
                // Get the text content of <udt:CreationDateTime> or <volvo:CreationDateTime> element
                msgTS = partyIDElement.getTextContent();
            }
        }
        LOGGER.info("Identifying CreationDateTime from input payload took {}ms.", System.currentTimeMillis() - startTime);
        return msgTS;
    }

    public String getCustomerPartyID(Document doc, long pollTS) {
        long startTime = System.currentTimeMillis();
        String partyID = Strings.EMPTY;
        // Find the md:PartyID element
        NodeList partyIDList = doc.getElementsByTagName("md:PartyID");
        if (partyIDList.getLength() > 0) {
            Element partyIDElement = null;
            partyIDElement = (Element) partyIDList.item(0);
            if (partyIDElement != null) {
                // Get the text content of md:PartyID
                partyID = partyIDElement.getTextContent();
            }
        } else {
            LOGGER.error("No PartyID found in the input message payload.");
            sendEmail("Malformed XML input payload.", "Stacktrace: No PartyID found in the input message. Please check the error log for more details.");
            Path malformedXMLFilePath = writeDataToFile(convertXmlToString(doc), MALFORMED_XML_FILE, SUFFIX_XML, "", pollTS);
            LOGGER.info("Malformed input payload written into {} directory and the file path is - {}", MALFORMED_XML_FILE, malformedXMLFilePath);
        }
        LOGGER.info("Identifying partyID from input payload took {}ms.", System.currentTimeMillis() - startTime);
        return partyID;
    }

    public String getCustomerPartyID(Document doc, String messageType, long pollTS) {
        if (null == doc){
            LOGGER.error("No doc instance found for the XML message.");
            return "";
        }
        long startTime = System.currentTimeMillis();
        String partyID = Strings.EMPTY;
        // Find the md:PartyID element
        NodeList partyIDList = doc.getElementsByTagName("md:PartyID");
        if (partyIDList.getLength() > 0) {
            Element partyIDElement = null;
            if (messageType.equals("SyncRURelationship")) {
                partyIDElement = (Element) partyIDList.item(1);
            } else {
                partyIDElement = (Element) partyIDList.item(0);
            }
            if (partyIDElement != null) {
                // Get the text content of md:PartyID
                partyID = partyIDElement.getTextContent();
            }
            LOGGER.info("The customer PartyID is --> {} || Action: {}.",partyID,messageType);
        } else {
            LOGGER.error("No PartyID found in the input message payload.");
            sendEmail("Malformed XML input payload.", "Stacktrace: No PartyID found in the input message. Please check the error log for more details.");
            Path malformedXMLFilePath = writeDataToFile(convertXmlToString(doc), MALFORMED_XML_FILE, SUFFIX_XML, "", pollTS);
            LOGGER.info("Malformed input payload written into {} directory and the file path is - {}", MALFORMED_XML_FILE, malformedXMLFilePath);
        }
        LOGGER.info("Identifying partyID from input payload took {}ms.", System.currentTimeMillis() - startTime);
        return partyID;
    }

    public String getCustomerFilterPartyID(Document doc) {
        if (null == doc){
            LOGGER.error("No doc instance found for the XML message.");
            return "";
        }
        long startTime = System.currentTimeMillis();
        String partyID = Strings.EMPTY;
        // Find the md:PartyID element
        NodeList partyIDList = doc.getElementsByTagName("businessId");
        if (partyIDList.getLength() > 0) {
            Element partyIDElement = null;
            partyIDElement = (Element) partyIDList.item(0);
            if (partyIDElement != null) {
                // Get the text content of md:PartyID
                partyID = partyIDElement.getTextContent();
            }
            LOGGER.info("The RU PartyID is --> " + partyID);
        } else {
            LOGGER.error("SyncRURelation - Volvo Payload received/ Only excluded RU IDs are present |No processing done.");
        }
        LOGGER.info("Identifying RU partyID from input payload took {}ms.", System.currentTimeMillis() - startTime);
        return partyID;
    }

    public List<String> searchOrganizationPartyIDExists(Document doc) {
        if (null == doc){
            LOGGER.error("No doc instance found for the XML message.");
            return null;
        }
        long startTime = System.currentTimeMillis();
        List<String> partyIDs = new ArrayList<String>();
        // Find the md:PartyID element
        NodeList partyIDList = doc.getElementsByTagName("businessId");
        System.out.println(partyIDList.getLength());
        if (partyIDList.getLength() > 0) {
        for (int i=0; i<partyIDList.getLength() ;i++){
            String partyIDElement = null;
            partyIDElement = partyIDList.item(i).getTextContent();
            if (!partyIDElement.equals("")) {
                // Get the text content of md:PartyID
                partyIDs.add(partyIDElement);
            }
            LOGGER.info("The RU PartyID is --> " + partyIDs);
        }
        } else {
            LOGGER.error("SyncRURelation - Volvo Payload received/ Only excluded RU IDs are present |No processing done.");
        }
        LOGGER.info("Identifying RU partyID from input payload took {}ms.", System.currentTimeMillis() - startTime);

        return partyIDs;
    }

    public Path writeDataToFile(String data, String fileBaseDir, String fileExtension, String partyID, long filesTimeStamp) {
        File baseDir = new File(fileBaseDir);
        if (!baseDir.exists()) {
            baseDir.mkdir();
        }
        long startTime = System.currentTimeMillis();
        String fileName = partyID + "-" + filesTimeStamp + "-" + new Date().getTime() + fileExtension;
        Path filePath = Paths.get(fileBaseDir + File.separator + fileName);
        LOGGER.info("Writing to path:" + filePath);
        File messageFile = new File(String.valueOf(filePath));
        try {
            FileUtils.writeStringToFile(messageFile, data, "UTF-8");
        } catch (IOException e) {
            LOGGER.error("Unable to write the message to the filepath " + filePath + " because:\n" + e.getMessage());
        }
        LOGGER.info("Writing the data to respective file took {}ms.", System.currentTimeMillis() - startTime);
        return filePath;
    }

    public Path writeAppendedDataToFile(String data, String fileBaseDir, String fileName) {
        File baseDir = new File(fileBaseDir);
        if (!baseDir.exists()) {
            baseDir.mkdir();
        }
        long startTime = System.currentTimeMillis();
        Path filePath = Paths.get(fileBaseDir + File.separator + fileName);
        LOGGER.info("Writing to path:" + filePath);
        File messageFile = new File(String.valueOf(filePath));
        try {
            FileUtils.writeStringToFile(messageFile, data, "UTF-8");
        } catch (IOException e) {
            LOGGER.error("Unable to write the message to the filepath " + filePath + " because:\n" + e.getMessage());
        }
        LOGGER.info("Writing the data to respective file took {}ms.", System.currentTimeMillis() - startTime);
        return filePath;
    }

    public Path writeDataToFile(String data, String fileBaseDir, String fileExtension) {
        File baseDir = new File(fileBaseDir);
        if (!baseDir.exists()) {
            baseDir.mkdir();
        }
        long startTime = System.currentTimeMillis();
        String fileName = PREFIX_XML + new Date().getTime() + fileExtension;
        Path filePath = Paths.get(fileBaseDir + File.separator + fileName);
        LOGGER.info("Writing to path:" + filePath);
        File messageFile = new File(String.valueOf(filePath));
        try {
            FileUtils.writeStringToFile(messageFile, data, "UTF-8");
        } catch (IOException e) {
            LOGGER.error("Unable to write the message to the filepath " + filePath + " because:\n" + e.getMessage());
        }
        LOGGER.info("Writing the data to respective file took {}ms.", System.currentTimeMillis() - startTime);
        return filePath;
    }

    public boolean validateMessage(String messageFile, File XSDFile) {
        xmlWorker = XMLWorker.XMLWorkerBuilder.aXMLWorker()
                .withSourceFilePath(messageFile)
                .withXSDPath(XSDFile.getAbsolutePath())
                .build();
        return xmlWorker.validate();
    }

    public String identifyMessage(String file) {
        xmlWorker = XMLWorker.XMLWorkerBuilder.aXMLWorker()
                .withSourceFilePath(file)
                .build();
        // TODO: Manage exception
        return xmlWorker.identifyMsgType();
    }

    public HashMap<Boolean, String> validateAndIdentifyDocType(String messageFile) {
        HashMap<String, String> XSDDocTypeMap = MDMService.parsePropObjects("xml.xsd.doctype");
        String XSDBasePath = (String) properties.get("xml.validation.base.path");
        long startTime = System.currentTimeMillis();
        final File f = new File(XSDBasePath); // need to get path from props
        boolean isMsgValid = false;
        String docType = Strings.EMPTY;
        HashMap<Boolean, String> messageMap = new HashMap<>();
        File[] filf = f.listFiles();
//        messageMap.put(isMsgValid, docType);
        for (final File file : Objects.requireNonNull(f.listFiles())) {
            if (!file.isDirectory()) {
                isMsgValid = validateMessage(messageFile, file);
                if (isMsgValid) {
                    LOGGER.info("XML Validation Successful.");
                    docType = XSDDocTypeMap.get(file.getName());
                    LOGGER.info("Respective docType of the input message is {}.", docType);
                    messageMap.put(isMsgValid, docType);
                    LOGGER.info("Validating message took {}ms.", System.currentTimeMillis() - startTime);
                    return messageMap;
                }
            }
        }
        LOGGER.info("Validating message took {}ms.", System.currentTimeMillis() - startTime);
        return messageMap;
    }

    public String prepareMessageProperties(String messageType) {
        MessageTypeConfiguration typeConfiguration = new MessageTypeConfiguration();
        return (String) typeConfiguration.getMessageConfig(messageType);
    }


    public String transformMessage(String fileBaseDir, Path XMLFilePath, String XSLPath, String customerpartyID, long filesTimeStamp, String env_name) {
        File baseDir = new File(fileBaseDir);
        if (!baseDir.exists()) {
            baseDir.mkdir();
        }
        String targetXMLFilePath = baseDir + File.separator + customerpartyID + "-" + filesTimeStamp + "-" + new Date().getTime() + SUFFIX_XML;
        xmlWorker = XMLWorker.XMLWorkerBuilder.aXMLWorker()
                .withSourceFilePath(String.valueOf(XMLFilePath))
                .withTargetXSLFilePath(XSLPath)
                .withTargetFilePath(targetXMLFilePath)
                .withEnviName(env_name)
                .build();
        return xmlWorker.transform();
    }

    @Retryable(value = Exception.class, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    public boolean uploadMessageFileToBlob(String jsonObject, String blobName) {
        LOGGER.info(LOG_START_BLOB_UPLOAD);
        long startTime = System.currentTimeMillis();
        boolean uploaded = false;
        try {
            uploaded = iAzureBlobService.uploadBlob(jsonObject, blobName);
            if (uploaded) {
                LOGGER.info(LOG_BLOB_SUCCESS);
            } else {
                LOGGER.error(LOG_BLOB_FAILURE);
                Helpers.ERROR_MSG = LOG_BLOB_FAILURE+"\n Stacktrace: Failure to upload file to blob. Check AzureBlobUploader log file for details from logs directory.";
                throw new RuntimeException(Helpers.ERROR_MSG);
            }
        } catch (Exception e) {
            LOGGER.error("Failure to upload file to blob because -->" + e.getMessage());
            Helpers.ERROR_MSG = LOG_BLOB_FAILURE+ "\nStacktrace: Failure to upload file to blob. Check AzureBlobUploader or IB Adapter inbound log file for details from logs directory.";
            throw new RuntimeException(Helpers.ERROR_MSG);
        }
        LOGGER.info("File upload to Azure blob storage took {}ms.", System.currentTimeMillis() - startTime);
        return uploaded;
    }

    @Retryable(value = Exception.class, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    public String invokeCDIRequestForMessage(String cdiURL, String requestType, String payload) throws Exception {
        String CDIResponse = Strings.EMPTY;
        String CDI_U = (String) properties.get("mdm.cdi.auth.username");
        String CDI_P = (String) properties.get("mdm.cdi.auth.pass");
        long startTime = System.currentTimeMillis();
        ResponseEntity<String> response = null;
        if (cdiURL.equals("") || requestType.equals("") || payload.equals("")) {
            LOGGER.info("The request URL or Type or Payload is empty. Unable to invoke the CDI endpoint.");
            return CDIResponse;
        }
        LOGGER.info(LOG_START_CDI_INVOKE);
        try {
            // Add headers
            MultiValueMap<String, String> headers = prepareHeader();
            // Add authHeaders
            MultiValueMap<String, String> authHeaders = new LinkedMultiValueMap<>();
            authHeaders.add(CDI_U, CDI_P);
            response = CommWorker.CommWorkerBuilder.acommWorker()
                    .withUrl(cdiURL)
                    .withMethod(requestType)
                    .withHeaders(headers)
                    .withAuthHeaders(authHeaders)
                    .withPayload(payload)
                    .build()
                    .request();

            if (response.getStatusCode() == HttpStatus.OK) {
                LOGGER.info(LOG_CDI_SUCCESS);
                CDIResponse = response.getBody();
            } else {
                LOGGER.error(LOG_CDI_FAILURE);
            }
        } catch (Exception e) {
            LOGGER.error(LOG_REQ_FAILED);
            LOGGER.info("Request to CDI failed with payload --> \n {} \n failed because --> {}", payload, e.getMessage());
            throw new Exception("");
        }
        LOGGER.info("Submission of a job to the CDI endpoint took {}ms.", System.currentTimeMillis() - startTime);
        return CDIResponse;
    }

    private MultiValueMap<String, String> prepareHeader() {
        String CDI_CONTENT_HEADER = (String) properties.get("mdm.cdi.headers.content");
        String CDI_CONTENT_ACCEPT = (String) properties.get("mdm.cdi.headers.accept");
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        String[] content = CDI_CONTENT_HEADER.split(COMMA);
        String[] accept = CDI_CONTENT_ACCEPT.split(COMMA);
        headers.add(content[0], content[1]);
        headers.add(accept[0], accept[1]);
        return headers;
    }

    private String prepareFilePath(String messageType) {
        String BASE_PATH_XML = (String) properties.get("xml.transform.base.path");
        String messageFilePath = BASE_PATH_XML + "/" + messageType + "_" + new Date().getTime() + SUFFIX_XML;
        LOGGER.info(LOG_GEN_XML_PATH + messageFilePath);
        return messageFilePath;
    }

    private void browseQueueAndWriteToFile(Session sess, Queue queue, String queueName) throws JMSException {
        String BROWSED_MESSAGE_FILE_PATH = (String) properties.get("xml.browsed.message.file.path");
        long startTime = System.currentTimeMillis();
        QueueBrowser browser = sess.createBrowser(queue);
        Enumeration<?> enumeration = browser.getEnumeration();
        while (enumeration.hasMoreElements()) {
            writeXMLMessageToFile(BROWSED_MESSAGE_FILE_PATH+File.separatorChar+queueName.replace(".", "-"), (Message) enumeration.nextElement());
        }
        LOGGER.debug("Messages in the queue {} browsed successfully and written to path CDB/mq-browsed-messages in {}ms.", queue.getQueueName(), System.currentTimeMillis() - startTime);
    }

    private String writeXMLMessageToFile(String fileBaseDir, Message message) {
        File baseDir = new File(fileBaseDir);
        if (!baseDir.exists()) {
            baseDir.mkdir();
        }
        // Write XML message to file
        String fileName = PREFIX_XML + new Date().getTime() + SUFFIX_XML; // TODO: Add message type in the Filename
        Path filePath = Paths.get(baseDir.getPath(), fileName);
        LOGGER.info("Writing to path:" + filePath);
        File xmlFile = new File(String.valueOf(filePath));
        try {
            String mess = message.getBody(String.class);
            FileUtils.writeStringToFile(xmlFile, mess);
            return xmlFile.getPath();
        } catch (IOException | JMSException e) {
            LOGGER.error("Exception in inbound adapter application because: {}", e.getMessage());
        }
        return fileName;
    }

    public static String convertXmlToString(Document xmlDocument) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            StringWriter writer = new StringWriter();
            //transform document to string
            transformer.transform(new DOMSource(xmlDocument), new StreamResult(writer));
            return writer.getBuffer().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
