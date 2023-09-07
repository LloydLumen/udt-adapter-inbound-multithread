package com.udtrucks.adaptor.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static com.udtrucks.adaptor.services.MDMService.loginSaaSMDM;

public class SessionCheck {


    private static final Logger LOGGER = LoggerFactory.getILoggerFactory().getLogger("SessionCheck");
    public static Long milliSec = 1800000L; //parameterize this from properties
    static Map<String, String> sessions;
    static String time;

    public static String check() throws UnsupportedEncodingException, JsonProcessingException {
        String token = loginSaaSMDM();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        time = now.format(formatter);
        sessions = new HashMap<>();
        sessions.put(time, token);
        for (Map.Entry<String, String> val : sessions.entrySet()) {
            return val.getValue();
        }
        return "";
    }

    public static String validateToken() throws UnsupportedEncodingException, JsonProcessingException {

        LOGGER.info("Validating the Token....");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy MM dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String a1 = now.format(dtf);
        LocalDateTime time_present = LocalDateTime.parse(a1, dtf);
        Long difference = null;
        for (Map.Entry<String, String> a : sessions.entrySet()) {
            LocalDateTime time_first = LocalDateTime.parse(a.getKey(), dtf);
            difference = java.time.Duration.between(time_first, time_present).toMillis();
            LOGGER.debug("date 1 first date :- {} ", time_first);
            LOGGER.debug("date 2 current date :- {}", time_present);
            LOGGER.debug("difference of both :- {}", difference);
        }

        if (difference > milliSec) {
            String token = loginSaaSMDM();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd HH:mm:ss");
            LocalDateTime now1 = LocalDateTime.now();
            String time2 = now1.format(formatter);
            sessions.clear();
            sessions.put(time2, token);
            for (Map.Entry<String, String> val : sessions.entrySet()) {
                LOGGER.info("The session token has expired, New session token is generated and being used.");
                return val.getValue();
            }
        } else {
            for (Map.Entry<String, String> val : sessions.entrySet()) {
                LOGGER.info("The session token is valid one.");
                return val.getValue();
            }
        }
        return "";
    }
}