package com.udtrucks.adaptor.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.udtrucks.adaptor.PasswordUtils;
import com.udtrucks.commons.worker.CommWorker;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.util.Strings;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.udtrucks.adaptor.constants.Constants.*;
import static com.udtrucks.adaptor.services.EmailService.sendEmail;

@Service
public class MDMService {
    private static final Logger LOGGER = LoggerFactory.getILoggerFactory().getLogger("MDMService");
    private static String requestType;

    @Retryable(value = Exception.class, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    static public String loginSaaSMDM() throws UnsupportedEncodingException {
        requestType = MDM_LOGIN_TYPE;
        HashMap<String, String> prop = (HashMap<String, String>) parsePropFile(requestType);
        ResponseEntity<String> response = null;
        String sessionId;
        String loginUrl = requestType + URL_NAME;
        String URL = prop.get(loginUrl);
        LOGGER.info("MDM login with URL - {}",URL);
        String payload = parsePayload(prop, requestType, "", "", "");
        String[] headersString = prop.get(requestType + HEADERS_NAME).split(",");
        MultiValueMap<String, String> headers = parseHeaders(headersString, prop, requestType, payload, "");

        try {
            response = CommWorker.CommWorkerBuilder.acommWorker()
                    .withUrl(URL)
                    .withPayload(payload)
                    .withHeaders(headers)
                    .withMethod(HttpMethod.POST.name())
                    .build().request();
            if (response.getStatusCode() == HttpStatus.OK) {
                LOGGER.info("Login Response Received");
                String login = response.getBody();
                JSONObject loginResponseObj = XML.toJSONObject(login);
                sessionId = loginResponseObj.getJSONObject("user").getString(SESSION_ID);
            } else{
                LOGGER.error("MDM login failed with status code - "+response.getStatusCode());
                sendEmail("MDM login failed.","Detailed error: MDM login failed with status code - "+response.getStatusCode());
                throw new RuntimeException("MDM login failed with status code "+ response.getStatusCode());
            }
        } catch (Exception e) {
            LOGGER.error("MDM login failed due to an exception: {}"+e.getMessage());
            sendEmail("MDM login failed.","Detailed error: MDM login failed because - "+e.getMessage());
            throw new RuntimeException(e);
        }
        return sessionId;
    }

    static Object validateSessionId(String sessionValue) throws UnsupportedEncodingException, JsonProcessingException {
        requestType = MDM_VALIDATE_TYPE;

        Map<String, String> prop = parsePropFile(requestType);

        String sessionID = sessionValue;

        String URL = prop.get(requestType + URL_NAME);
        LOGGER.info(URL);

        String payload = parsePayload(prop, requestType, sessionID, "", "");

        String[] headersString = prop.get(requestType + HEADERS_NAME).split(",");

        MultiValueMap<String, String> headers = parseHeaders(headersString, prop, requestType, "", sessionID);

        ResponseEntity<String> obj1 = CommWorker.CommWorkerBuilder.acommWorker().withPayload(payload).withUrl(URL).withHeaders(headers).withMethod(HttpMethod.POST.name()).build().request();
        LOGGER.info("The info received from MDM " + obj1.getBody());

        String recdJsonFile = obj1.getBody();
        HashMap<String, Object> result1 = new ObjectMapper().readValue(recdJsonFile, HashMap.class);
        Object expireTime = result1.get("timeUntilExpire");
        return expireTime;
    }

    public static boolean logoutSaaSMDM(String sessionValue) throws UnsupportedEncodingException {
        requestType = MDM_LOGOUT_TYPE;

        Map<String, String> prop = parsePropFile(requestType);

        String sessionID = sessionValue;

        String URL = prop.get(requestType + URL_NAME);
        LOGGER.info(URL);

        String[] headersString = prop.get(requestType + HEADERS_NAME).split(COMMA);

        MultiValueMap<String, String> headers = parseHeaders(headersString, prop, requestType, "", sessionID);

        ResponseEntity<String> obj1 = CommWorker.CommWorkerBuilder.acommWorker().withUrl(URL).withHeaders(headers).withMethod(HttpMethod.POST.name()).build().request();
        LOGGER.info("Logged Out Successfully");
        return true;
    }

    public static HashMap<String,String>  parsePropObjects(String requestType){
        String docTypes = (String) properties.get(requestType);
        List<String> docTypeList = Arrays.asList(docTypes.split(","));

        HashMap<String, String> docTypeMap = new HashMap<String, String>();
        for (String doc : docTypeList){
            docTypeMap.put(doc.split(":")[0], doc.split(":")[1]);
        }

        return docTypeMap;
    }

    public static HashMap<String, String> parsePropFile(String requestType) {
        HashMap<String, String> requestPropMap = new HashMap<>();
        properties.entrySet().forEach(e -> {
            String headerVal = (String) e.getKey();
            if (headerVal.contains(requestType)) {
                requestPropMap.put(headerVal, properties.getProperty(headerVal));
            }
        });
        return requestPropMap;

    }

    private static String parsePayload(Map<String, String> prop, String requestType, String sessionID, Object idAttrFromValue, String internalId) {
        if (requestType.contains(LOGIN)) {
            String payloadUsername = prop.get(requestType + ".payload" + ".username");
            String payloadPassword = prop.get(requestType + ".payload" + ".password");
            payloadPassword = new String(PasswordUtils.decodePassword(payloadPassword));
            String str1 = "{\n" + "    \"username\": \"" + payloadUsername + "\",\n" + "    \"password\": \"" + payloadPassword + "\"\n" + "}";
            return str1;
        } else if (requestType.contains(VALIDATE)) {
            String payloadtype = prop.get(requestType + ".payload" + ".@type");
            String payloadUsername = prop.get(requestType + ".payload" + ".userName");
            String payloadSessionID = prop.get(requestType + ".payload" + ".icToken");
            if (payloadSessionID.equals("")) {
                payloadSessionID = sessionID;
            }
            String str1 = "{\n" + "   \"@type\": \"" + payloadtype + "\",\n" + "   \"userName\": \"" + payloadUsername + "\",\n" + "   \"icToken\": \"" + payloadSessionID + "\"\n" + "}   ";
            return str1;
        } else if (requestType.contains(FILTER)) {
            String payloadpageOffSet = prop.get(requestType + ".payload" + ".pageoffset");
            String payloadpageSize = prop.get(requestType + ".payload" + ".pageSize");
            String payloadBE = prop.get(requestType + ".payload" + ".businessEntity");
            Object payloadBID = prop.get(requestType + ".payload" + ".businessId");
            if (payloadBID.equals("")) {
                payloadBID = idAttrFromValue;
            }
            if (payloadBE.equals("")) {
                payloadBE = internalId;
            }
            String str1 = "{\n" + "    \"pageoffset\": \"" + payloadpageOffSet + "\",\n" + "    \"pageSize\": \"" + payloadpageSize + "\",\n" + "    \"filter\": {\n" + "        \"_from\": {\n" + "            \"businessEntity\": \"" + payloadBE + "\",\n" + "            \"businessId\": \"" + payloadBID + "\"\n" + "        }\n" + "    }\n" + "}";
            return str1;
        }
        return "";
    }

    private static MultiValueMap<String, String> parseHeaders(String[] headersArray, Map<String, String> prop, String requestType, String payload, String token) throws UnsupportedEncodingException {
        MultiValueMap<String, String> headerMap = new LinkedMultiValueMap<>();

        for (String header : headersArray) {
            String headerValues = prop.get(requestType + HEADERS_NAME_DOT + header);

            if (header.equals(CONTENT_LENGTH)) {
                int contentlength = payload.getBytes(StandardCharsets.UTF_8).length;
                headerMap.put(header, Collections.singletonList(String.valueOf(contentlength)));
            } else if (header.equals(ID_SESSION) || header.equals("INFA-SESSION-ID")) {
                headerMap.put(header, Collections.singletonList(token));
            } else if (headerValues != null && !headerValues.equals("")) {
                headerMap.put(header, Collections.singletonList(headerValues));
            }
        }
        return headerMap;
    }

    public static Header[] convertMultiValueMapToHeaders(MultiValueMap<String, String> multiValueMap) {
        Header[] headers = new Header[multiValueMap.size()];

        int i = 0;
        for (String key : multiValueMap.keySet()) {
            String value = multiValueMap.getFirst(key);
            headers[i++] = new BasicHeader(key, value);
        }

        return headers;
    }

    @Retryable(value = Exception.class, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    public String getCallMDM(String sessionValue, String internalid, Object level) throws UnsupportedEncodingException {
        requestType = MDM_GET_TYPE;
        ResponseEntity<String> response = null;
        Map<String, String> prop = parsePropFile(requestType);
        String sessionID = sessionValue;
        String URL = prop.get(requestType + URL_NAME);
        String strCustMDMUrl = URL + internalid + "/" + level;
        LOGGER.info("Get master Customer record with URL: {}",strCustMDMUrl);
        String[] headersString = prop.get(requestType + HEADERS_NAME).split(COMMA);
        MultiValueMap<String, String> headers = parseHeaders(headersString, prop, requestType, "", sessionID);
        try {
            response = CommWorker.CommWorkerBuilder.acommWorker()
                    .withUrl(strCustMDMUrl)
                    .withHeaders(headers)
                    .withMethod(HttpMethod.GET.name())
                    .build().request();
            if (response.getStatusCode() == HttpStatus.OK) {
                LOGGER.info("MDM GET call Response Received");
            }else{
                LOGGER.error("MDM GET call for master customer record details failed with status code - "+response.getStatusCode());
                sendEmail("MDM GET call failed.","Detailed error: MDM GET call for master customer record details to endpoint "+strCustMDMUrl+ " failed with status code "+response.getStatusCode());
                throw new RuntimeException("MDM GET call for master customer record details failed with status code "+ response.getStatusCode());
            }
        } catch (Exception e) {
            LOGGER.error("MDM GET call for master customer record details failed due to an exception: {}"+e.getMessage());
            sendEmail("MDM GET call failed.","Detailed error: MDM GET call for master customer record details failed because - "+e.getMessage());
            throw new RuntimeException(e);
        }
        return response.getBody();
    }

    @Retryable(value = Exception.class, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    public Object filterCallFleetMDM(String sessionValue, Object idAttrFromValue, String internalURLId, String internalId) throws Exception {
        requestType = MDM_FILTER_TYPE;
        ResponseEntity<String> response = null;
        Map<String, String> prop = parsePropFile(requestType);
        String sessionID = sessionValue;
        String URL = prop.get(requestType + URL_NAME);
        String internalURlID = internalURLId;
        String finalURL = URL + internalURlID + File.separatorChar + "filter";
        LOGGER.info(finalURL);
        String payload = parsePayload(prop, requestType, sessionID, idAttrFromValue, internalId);
        String[] headersString = prop.get(requestType + HEADERS_NAME).split(COMMA);
        MultiValueMap<String, String> headers = parseHeaders(headersString, prop, requestType, "", sessionID);
        try {
            response = CommWorker.CommWorkerBuilder.acommWorker()
                    .withPayload(payload)
                    .withUrl(finalURL)
                    .withHeaders(headers)
                    .withMethod(HttpMethod.POST.name())
                    .build().request();
            if (response.getStatusCode() == HttpStatus.OK) {
                LOGGER.info("MDM Filter call for Fleet details response Received");
            } else{
                LOGGER.error("MDM Filter call for Fleet details failed with status code - "+response.getStatusCode());
                sendEmail("MDM Filter call for Fleet details failed.","Detailed error: MDM Filter call for Fleet details to endpoint "+finalURL+ " failed with status code "+response.getStatusCode());
//                throw new RuntimeException("MDM Filter call for Fleet details failed with status code "+ response.getStatusCode());
            }
        } catch (Exception e) {
            LOGGER.error("MDM Filter call for Fleet details failed due to an exception: {}"+e.getMessage());
            sendEmail("MDM Filter call for Fleet details failed.","Detailed error: MDM Filter call for Fleet details failed because - "+e.getMessage());
//            throw new RuntimeException(e);
        }
        return response.getBody();
    }

    @Retryable(value = Exception.class, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    public String searchOrganization(String sessionValue, String internalId, String partyID) throws Exception {
        requestType = MDM_SEARCH;
        ResponseEntity<String> response = null;
        Map<String, String> prop = parsePropFile(requestType);
        String searchURL = prop.get(requestType + URL_NAME);
        LOGGER.info("Initiating MDM search call with URL --> {}", searchURL);
        String orgSearchPayload = "{\n" + "    \"entityType\": \"" + internalId + "\",\n" + "    \"fields\": {\n" + "            \"" + internalId + ".X_UDT_partyID\": \"" + partyID + "\"\n" + "    }\n" + "}";
        LOGGER.debug("\nPayload for search API call ---> \n{}", orgSearchPayload);
        String[] headersString = prop.get(requestType + HEADERS_NAME).split(COMMA);
        MultiValueMap<String, String> headers = parseHeaders(headersString, prop, requestType, "", sessionValue);
        try {
            response = CommWorker.CommWorkerBuilder.acommWorker()
                    .withPayload(orgSearchPayload)
                    .withUrl(searchURL)
                    .withHeaders(headers)
                    .withMethod(HttpMethod.POST.name())
                    .build().request();
            if (response.getStatusCode() == HttpStatus.OK) {
                LOGGER.info("Search MDM API call response received");
            }
            else{
                LOGGER.error("Search MDM API call failed with status code - "+response.getStatusCode());
                sendEmail("Search MDM API call failed.","Detailed error: Search MDM API call to endpoint "+searchURL+ " failed with status code "+response.getStatusCode());
//                throw new RuntimeException("Search MDM API call failed with status code "+ response.getStatusCode());
            }
        } catch (Exception e) {
            LOGGER.error("Search MDM API call failed due to an exception: {}"+e.getMessage());
            sendEmail("Search MDM API failed.","Detailed error: Search MDM API call failed because - "+e.getMessage());
            throw new RuntimeException(e);
        }
        LOGGER.debug("\nSearch MDM API call response ---> \n{}", response.getBody());
        return response.getBody();
    }

    @Retryable(value = Exception.class, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    public String ruSearchOrganization(String sessionValue, String internalId, String partyID) throws Exception {
        requestType = MDM_SEARCH;
        ResponseEntity<String> response = null;
        Map<String, String> prop = parsePropFile(requestType);
        String searchURL = prop.get(requestType + URL_NAME);
        LOGGER.info("Initiating MDM search call with URL --> {}", searchURL);
        String orgSearchPayload = "{\n" + "    \"entityType\": \"" + internalId + "\",\n" + "    \"fields\": {\n" + "            \"" + internalId + ".X_UDT_partyID\": \"" + partyID + "\"\n" + "    }\n" + "}";
        LOGGER.debug("\nPayload for search API call ---> \n{}", orgSearchPayload);
        String[] headersString = prop.get(requestType + HEADERS_NAME).split(COMMA);
        MultiValueMap<String, String> headers = parseHeaders(headersString, prop, requestType, "", sessionValue);
        try {
            response = CommWorker.CommWorkerBuilder.acommWorker()
                    .withPayload(orgSearchPayload)
                    .withUrl(searchURL)
                    .withHeaders(headers)
                    .withMethod(HttpMethod.POST.name())
                    .build().request();
            if (response.getStatusCode() == HttpStatus.OK) {
                LOGGER.info("Search MDM API call response received");
            }else{
                LOGGER.info("Record Not Found: This PartyID: {} needs to be included in Exclusion List for RURelationships. Sending Email.",partyID);
            }
        }catch (HttpClientErrorException e) {
            if(e.getRawStatusCode() != 404){
                LOGGER.error("Search Organization details MDM API call failed due to an exception: {}",e.getMessage());
                Helpers.ERROR_MSG = "Detailed error: Search Organization details MDM API call failed because - "+e.getMessage();
                throw e;

            }else {
                LOGGER.info("Record Not Found: This PartyID: {} needs to be included in Exclusion List for RURelationships. Sending Email.",partyID);
            }
            return response.getBody();
        }catch (Exception e){
            LOGGER.error("Search customer details MDM API call failed due to an exception: {}",e.getMessage());
            Helpers.ERROR_MSG = "Search customer details MDM API call failed due to an exception -> Detailed error: Search customer details MDM API call failed because - ";
            throw e;
        }
        return response.getBody();
    }

    @Retryable(value = Exception.class, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    public String getCDIJobStatus(String sessionValue, String CDIRunID) throws UnsupportedEncodingException {
        String jobStatus;
        requestType = MDM_GET_JOB_STATUS;
        ResponseEntity<String> response = null;
        Map<String, String> prop = parsePropFile(requestType);
        String searchURL = prop.get(requestType + URL_NAME);
        searchURL = searchURL.replace("runID", CDIRunID);
        LOGGER.info("GET CDI job status with URL --> {}\n", searchURL);
        String[] headersString = prop.get(requestType + HEADERS_NAME).split(COMMA);
        MultiValueMap<String, String> headers = parseHeaders(headersString, prop, requestType, "", sessionValue);
        try {
            response = CommWorker.CommWorkerBuilder.acommWorker()
                    .withUrl(searchURL)
                    .withHeaders(headers)
                    .withMethod(HttpMethod.GET.name())
                    .build().request();
            LOGGER.debug("GET CDI job status response:\n{}",response.getBody());
            if (response.getStatusCode() == HttpStatus.OK) {
                LOGGER.info("GET CDI job status call response received");
                JSONObject jobStatusResponse = new JSONObject(response.getBody());
                LOGGER.debug("GET CDI job status call response ---> \n{}", jobStatusResponse);
                jobStatus = jobStatusResponse.getString("status");
                if(jobStatus.equals("No status available")){
                    LOGGER.error("GET CDI job status call failed with status code - "+jobStatus);
                    Helpers.ERROR_MSG ="GET CDI job status call to endpoint "+searchURL+ " failed with status code "+jobStatus;
                    throw new RuntimeException("GET CDI job status call failed with status code "+ jobStatus);
                }
            }else{
                LOGGER.error("GET CDI job status call failed with status code - "+response.getStatusCode());
                Helpers.ERROR_MSG = "GET CDI job status call to endpoint "+searchURL+ " failed with status code "+response.getStatusCode();
                throw new RuntimeException(Helpers.ERROR_MSG);
            }
        } catch (Exception e) {
            LOGGER.error("GET CDI job status call failed due to an exception: {}"+e.getMessage());
            Helpers.ERROR_MSG = "GET CDI job status call failed. \nDetailed error: GET CDI job status call failed because -";
            throw new RuntimeException(e);
        }
        LOGGER.info("The earlier CDI job with RunID - {} is in {} state.", CDIRunID, jobStatus);
        return jobStatus;
    }

    @Retryable(value = Exception.class, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    public String patchCustomer(String sessionValue, String partyID, String businessID) throws Exception {
        String customerPatchResponse = Strings.EMPTY;
        requestType = MDM_PATCH;
        String orgPatchPayload = "[\n{\n" +
                " \"op\": \"add\",\n" +
                " \"path\": \"x_udt_custmarkt_cust_org\",\n" +
                " \"value\": {\n" +
                " \"_businessId\": \"" + businessID + "\"\n" +
                " }\n" +
                "}\n]";

        Map<String, String> prop = parsePropFile(requestType);
        String searchURL = prop.get(requestType + URL_NAME);
        String[] headersString = prop.get(requestType + HEADERS_NAME).split(COMMA);
        MultiValueMap<String, String> headers = parseHeaders(headersString, prop, requestType, "", sessionValue);
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            searchURL = searchURL.replace("partyID", partyID);
            LOGGER.info("Initiating MDM customer details patch call with URL --> {}", searchURL);
            HttpPatch httpPatch = new HttpPatch(searchURL);
            StringEntity requestEntity = new StringEntity(orgPatchPayload);
            requestEntity.setContentType("application/json-patch+json");
            httpPatch.setEntity(requestEntity);
            // Creating BasicHeaders object to hold headers
            MultiValueMap<String, String> requestHeaders = new LinkedMultiValueMap<>();
            requestHeaders.add("IDS-SESSION-ID", sessionValue);
            requestHeaders.add("Content-Type", "application/json");
            httpPatch.setHeaders(convertMultiValueMapToHeaders(requestHeaders));

            CloseableHttpResponse patchResponse = httpClient.execute(httpPatch);
            customerPatchResponse = EntityUtils.toString(patchResponse.getEntity());

            if (patchResponse.getStatusLine().getStatusCode() == 200) {
                LOGGER.info("Patch MDM API call is successful.");
                LOGGER.debug("\nPatch MDM API call response ---> \n{}", customerPatchResponse);
            } else {
                LOGGER.error("Failure to invoke Patch API call Failure");
            }
        } catch (Exception e) {
            LOGGER.info("Re - trying the Patch API call due to an exception --> {}", e.getMessage());
        }
        return customerPatchResponse;
    }

    public boolean patchCustomerMDM(String sessionID, String partyID, String businessID) throws Exception {
        String MDMRequestProcessorJarPath  = (String) properties.get("project.mdm.utility");
        String MDMRequestProcessoLoggerPath  = (String) properties.get("project.mdm.utility.log");
        LOGGER.info("Request to patch relationship info to MDM for {} is submitted....",partyID);
        long startTime = System.currentTimeMillis();
        List<String> MDMPatchService = new ArrayList<>();
        MDMPatchService.add("java");
        MDMPatchService.add("-jar");
//        MDMPatchService.add(MDMRequestProcessorJarPath);
//        MDMPatchService.add("E:\\udt-adapter-inbound\\src\\main\\resources\\MDMRequestProcessor.jar");
        MDMPatchService.add("C:\\Users\\LloydKoshy\\OneDrive - LumenData, Inc\\Documents\\UD trucks\\AdapterApplication\\udt-adapter-inbound-multithread\\src\\main\\resources\\MDMRequestProcessor.jar");
//        MDMPatchService.add("E:\\adaptor-inbound\\resources\\MDMRequestProcessor.jar");
        MDMPatchService.add(sessionID);
        MDMPatchService.add(partyID);
        MDMPatchService.add(businessID);
        ProcessBuilder pb = new ProcessBuilder(MDMPatchService);

        // Redirect the output to a log file
        File logFile = new File("C:\\Users\\LloydKoshy\\OneDrive - LumenData, Inc\\Documents\\UD trucks\\AdapterApplication\\udt-adapter-inbound-multithread\\logs\\MDMPatchService.log");
//        File logFile = new File("E:\\adaptor-inbound\\logs\\MDMPatchService.log");
//        File logFile = new File(MDMRequestProcessoLoggerPath);
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));

        // Start the process
        Process process = pb.start();

        // Wait for the process to complete
        int exitCode = process.waitFor();
        LOGGER.info("Patch request to MDM took {}ms.", System.currentTimeMillis() - startTime);
        return exitCode == 0;
    }

    @Retryable(value = Exception.class, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    public String searchCustomer1(String sessionValue, String partyID) throws Exception {
        requestType = MDM_GET_CUSTOMER;
        ResponseEntity<String> response = null;
        Map<String, String> prop = parsePropFile(requestType);
        String searchURL = prop.get(requestType + URL_NAME);
        searchURL = searchURL.replace("partyID", partyID);
        LOGGER.info("Initiating MDM customer details GET call with URL --> {}\n", searchURL);
        String[] headersString = prop.get(requestType + HEADERS_NAME).split(COMMA);
        MultiValueMap<String, String> headers = parseHeaders(headersString, prop, requestType, "", sessionValue);
        try {
            response = CommWorker.CommWorkerBuilder.acommWorker()
                    .withUrl(searchURL)
                    .withHeaders(headers)
                    .withMethod(HttpMethod.GET.name())
                    .build().request();
            LOGGER.debug("Search customer response:\n{}",response.getBody());
            if (response.getStatusCode() == HttpStatus.OK) {
                LOGGER.info("Search Customer details MDM API call response received");
            }else{
                LOGGER.error("Search customer details MDM API call failed with status code - "+response.getStatusCode());
                sendEmail("Search customer details MDM API call failed.","Detailed error: Search customer details MDM API call to endpoint "+searchURL+ " failed with status code "+response.getStatusCode());
//                throw new RuntimeException("Search customer details MDM API call failed with status code "+ response.getStatusCode());
            }
        } catch (Exception e) {
            LOGGER.error("Search customer details MDM API call failed due to an exception: {}"+e.getMessage());
            sendEmail("Search customer details MDM API failed.","Detailed error: Search customer details MDM API call failed because - "+e.getMessage());
//            throw new RuntimeException(e);
//            return response.getBody();
        }
        LOGGER.debug("\nGET Customer details MDM API call response ---> \n{}", response.getBody());
        return response.getBody();
    }

    @Retryable(value = Exception.class, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    public String searchCustomer(String sessionValue, String partyID) throws Exception {
        String searchOrgResponse = Strings.EMPTY;
        requestType = MDM_GET_CUSTOMER;

        ResponseEntity<String> response = null;

        Map<String, String> prop = parsePropFile(requestType);

        String searchURL = prop.get(requestType + URL_NAME);

        LOGGER.info("Initiating MDM customer details GET call with URL --> {}\n", searchURL.replace("partyID", partyID));

        String[] headersString = prop.get(requestType + HEADERS_NAME).split(COMMA);

        MultiValueMap<String, String> headers = parseHeaders(headersString, prop, requestType, "", sessionValue);

        try {
            response = CommWorker.CommWorkerBuilder.acommWorker()
                    .withUrl(searchURL.replace("partyID", partyID))
                    .withHeaders(headers).withMethod(HttpMethod.GET.name())
                    .build().request();
            LOGGER.debug("Search customer response:\n{}",response.getBody());
            if (response.getStatusCode() == HttpStatus.OK) {
                LOGGER.info("GET Customer details MDM API call response received");
                searchOrgResponse = response.getBody();
                LOGGER.debug("\nGET Customer details MDM API call response ---> \n{}", searchOrgResponse);
            }else{
                LOGGER.info("Record Not Found: {} -> Creating a new record." +partyID);
            }
        }catch (HttpClientErrorException e) {
            if(e.getRawStatusCode() != 404){
                LOGGER.error("Search customer details MDM API call failed due to an exception: {}"+e.getMessage());
                Helpers.ERROR_MSG = "Detailed error: Search customer details MDM API call failed because - ";
                throw e;

            }else {
                LOGGER.info("Record Not Found: {} -> Creating a new record." +partyID);
            }
            return searchOrgResponse;
        }catch (Exception e){
            LOGGER.error("Search customer details MDM API call failed due to an exception: {}"+e.getMessage());
            Helpers.ERROR_MSG = "Search customer details MDM API call failed due to an exception -> Detailed error: Search customer details MDM API call failed because - ";
            throw e;
        }
        return searchOrgResponse;
    }
}