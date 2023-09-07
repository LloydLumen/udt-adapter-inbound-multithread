package com.udtrucks.adaptor.services;

import com.udtrucks.commons.worker.CommWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.Instant;
import java.util.List;

import static com.udtrucks.adaptor.constants.Constants.*;

@Service
public class CDIService {
    private static final Logger LOGGER = LoggerFactory.getILoggerFactory().getLogger("CDIService");

    private MultiValueMap<String, String> prepareHeader() {
        String CDI_CONTENT_HEADER = (String) properties.get("mdm.cdi.headers.content");
        String CDI_CONTENT_ACCEPT = (String) properties.get("mdm.cdi.headers.accept");
        String CDI_CONTENT_MODEL_VERSION = (String) properties.get("mdm.cdi.headers.model.version");
        Instant dtToInstant = Instant.now();
        long epochTime = dtToInstant.toEpochMilli();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        String[] content = CDI_CONTENT_HEADER.split(COMMA);
        String[] accept = CDI_CONTENT_ACCEPT.split(COMMA);
        headers.add(content[0], content[1]);
        headers.add(accept[0], accept[1]);
        headers.add(CDI_CONTENT_MODEL_VERSION, String.valueOf(epochTime));
        return headers;
    }

    @Retryable(value = Exception.class, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    public Object invokeCDIRequestForMessage(String xmlMessage) throws Exception {
        String CDI_URI = (String) properties.get("mdm.cdi.customer.url");
        String CDI_U = (String) properties.get("mdm.cdi.auth.username");
        String CDI_P = (String) properties.get("mdm.cdi.auth.pass");
        LOGGER.info(LOG_START_CDI_INVOKE);
        ResponseEntity response = null;
        try {

            // Add headers
            MultiValueMap<String, String> headers = prepareHeader();
            // Add authHeaders
            MultiValueMap<String, String> authHeaders = new LinkedMultiValueMap<>();
            authHeaders.add(CDI_U, CDI_P);

            CommWorker commWorker = CommWorker.CommWorkerBuilder.acommWorker().withUrl(CDI_URI).withMethod(HttpMethod.POST.name()).withHeaders(headers)
                    .withAuthHeaders(authHeaders).withPayload(xmlMessage).build();
            ResponseEntity<String> responseEntity = commWorker.request();
            response = new ResponseEntity<String>(responseEntity.getBody(), HttpStatus.OK);
            if (response.getStatusCode() == HttpStatus.OK) {
                LOGGER.info(LOG_CDI_SUCCESS);
            } else {
                LOGGER.error(LOG_CDI_FAILURE);
            }
        } catch (Exception e) {
            LOGGER.error(LOG_REQ_FAILED+" because: {}",e.getMessage());
        }

        return response;
    }

    @Retryable(value = Exception.class, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    public Object invokeCDIRequestForMessage(List<String> xmlMessage) throws Exception {
        String CDI_URI = (String) properties.get("mdm.cdi.customer.url");
        String CDI_U = (String) properties.get("mdm.cdi.auth.username");
        String CDI_P = (String) properties.get("mdm.cdi.auth.pass");
        LOGGER.info(LOG_START_CDI_INVOKE);
        ResponseEntity response = null;
        try {

            // Add headers
            MultiValueMap<String, String> headers = prepareHeader();
            // Add authHeaders
            MultiValueMap<String, String> authHeaders = new LinkedMultiValueMap<>();
            authHeaders.add(CDI_U, CDI_P);

            CommWorker commWorker = CommWorker.CommWorkerBuilder.acommWorker().withUrl(CDI_URI).withMethod(HttpMethod.POST.name()).withHeaders(headers)
                    .withAuthHeaders(authHeaders).withPayload(xmlMessage.get(0)).build(); // TODO: Update payload object
            ResponseEntity<String> responseEntity = commWorker.request();
            response = new ResponseEntity<String>(responseEntity.getBody(), HttpStatus.OK);
            if (response.getStatusCode() == HttpStatus.OK) {
                LOGGER.info(LOG_CDI_SUCCESS);
            } else {
                LOGGER.error(LOG_CDI_FAILURE);
            }
        } catch (Exception e) {
            LOGGER.error(LOG_REQ_FAILED+" because: {}",e.getMessage());
        }

        return response;
    }
}