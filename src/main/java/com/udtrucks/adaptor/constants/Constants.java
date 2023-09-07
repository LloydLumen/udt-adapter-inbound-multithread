package com.udtrucks.adaptor.constants;

import java.util.Properties;
public interface Constants {

    Properties properties = new Properties();
    public String APPLICATION_PROPERTIES_NAME="D:\\udt-adaptor-inbound-v2\\src\\main\\resources\\application.properties";
    public String LOG_CDI_FAILURE = "Invoke CDI failure";
    public String LOG_CDI_SUCCESS = "Invoke CDI success";
    public String LOG_BLOB_FAILURE = "Blob upload failure";

    public String LOG_BLOB_SUCCESS = "Blob upload success";
    public String LOG_START_POLLING = "Starting polling from ";
    public String LOG_START_BLOB_UPLOAD = "Starting Blob upload";
    public String LOG_START_CDI_INVOKE = "Starting CDI Invoke ";
    public String LOG_POLLED_MESSAGES = "Polled %s messages.";
    public String LOG_FINISHED_POLL = "Finished Polling";
    public String PREFIX_XML = "XML_";
    public String LOG_CONFIG_NOT_FOUND = "Message Configuration not found";

    public String EXCLUSION_LIST_NOT_FOUND = "Exlusion List File not found";

    public String LOG_Q_CONFIG_NOT_FOUND = "Queue Configuration not found";
    public String LOG_XML_TRANSFORM_COMPLETE = "XSL transform Complete";

    public String LOG_XML_TRANSFORM_FAILED = "XSL transform Failed";

    public String LOG_XML_VALIDATION_FAILED = "XSL validation Failed";
    public String LOG_XML_TO_JSON_FAILED = "Failed to parse Json from XML message";



    public String LOG_REQ_COMPLETE = "COMPLETED";

    public String LOG_GEN_XML_PATH = "Generate XML file path: ";

    public String LOG_MSG_NOT_IDENTIFIED = "Message identification failed";
    public String RESP_NO_NEW_MSG = "No new messages";
    public String SUFFIX_XML = ".xml";
    public String SUFFIX_TXT = ".txt";
    public String BATCH_PROCESS_PREFIX = "BatchProcess-";
    public String SUFFIX_JSON = ".json";
    public String LOG_RCVD_MESSAGES = "Received Message \n %s ";
    public String LOG_REQ_FAILED = "REQUEST FAILED";
    public String COMMA =",";

    public String PATH_FAILED_MSG ="FAILED_MESSAGES";
    public String PATH_MSG_BASE_DIR ="MQ_MSG";
    public String MDM_VALIDATE_TYPE ="MDM.request.validate";
    public String MDM_LOGIN_TYPE ="MDM.request.login";
    public String URL_NAME =".URL";
    public String HEADERS_NAME=".headers";
    public String SESSION_ID="icSessionId";
    public String MDM_LOGOUT_TYPE="MDM.request.logout";
    public String MDM_GET_TYPE="MDM.request.Get";
    public String MDM_SEARCH="MDM.request.search";
    public String MDM_PATCH="MDM.request.patch";
    public String MDM_GET_JOB_STATUS="MDM.request.status";
    public String MDM_GET_CUSTOMER="MDM.request.customer.get";
    public String MDM_FILTER_TYPE="MDM.request.filter";
    public String FILTER="filter";
    public String LOGIN="login";
    public String VALIDATE="validate";
    public String HEADERS_NAME_DOT=".headers.";
    public String CONTENT_LENGTH="content-length";
    public String ID_SESSION="IDS-SESSION-ID";
    public String ORGANIZATION_I_URL_ID_BE ="udt_organization";
    public String CUSTOMER_I_URL_ID_BE ="udt_customer";
    public String PARTY_ID="PartyID";
}
