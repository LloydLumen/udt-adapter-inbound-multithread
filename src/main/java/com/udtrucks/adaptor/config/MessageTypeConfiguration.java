package com.udtrucks.adaptor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.udtrucks.adaptor.services.EmailService.sendEmail;

@Configuration
    public class MessageTypeConfiguration {
    private static final Logger LOGGER = LoggerFactory.getILoggerFactory().getLogger("MessageTypeConfiguration");
    private  Properties properties;
    public MessageTypeConfiguration(){
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        properties = new Properties();
        try (InputStream resourceStream = loader.getResourceAsStream("message.properties")) {
            properties.load(resourceStream);
        } catch (IOException e) {
            LOGGER.error("Unable to load Message type configuration from message.properties file because: {}", e.getMessage());
            sendEmail("Unable to load configurationf or respective message type from message.properties.", "Detailed error: "+e.getMessage());
        }
    }

    public Object getMessageConfig(String messageType){
        return properties.get(messageType);
    }

}
