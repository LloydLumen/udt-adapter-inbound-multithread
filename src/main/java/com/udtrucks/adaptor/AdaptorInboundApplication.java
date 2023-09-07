package com.udtrucks.adaptor;

import com.udtrucks.adaptor.services.QueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.retry.annotation.EnableRetry;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.udtrucks.adaptor.constants.Constants.properties;
import static com.udtrucks.adaptor.services.EmailService.sendEmail;

@SpringBootApplication
@EnableJms
@EnableAutoConfiguration
@EnableRetry
public class AdaptorInboundApplication {
	private static final Logger LOGGER = LoggerFactory.getILoggerFactory().getLogger("AdaptorInboundApplication");
    public static String ERROR_MSG = "";

    static{
        try{
            String propFileName = System.getenv("UDT_INBOUND_ADAPTER_PROPERTIES");
//            String propFileName = "E:\\udt-adaptor-inbound-properties\\application.properties";
            LOGGER.info("Property file is in path --> {}",propFileName);
            InputStream inputStream;
            inputStream = new FileInputStream(propFileName);
            // read the file
            if (inputStream != null) {
                properties.load(inputStream);
                LOGGER.info("Application properties are loaded.");
            } else {
                sendEmail("IB Adapter property file is missing.", "Property file is required to start the Inbound adapter application.");
                LOGGER.error("application.properties (property file) file not found in the classpath.");
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
        }catch(Exception e){
            LOGGER.error("Unable to load the property file because: {}",e.getMessage());
            sendEmail("Unable to load the property file", "Detailed error: "+e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Autowired
    QueueService queueService;
    private static long pollInterval;
    public static void main(String[] args) throws IOException, JMSException, ExecutionException, InterruptedException {
        pollInterval = Long.parseLong((String) properties.get("project.mq.poll.interval"));
		try{
            LOGGER.info("The encoding of the inbound application is:{}", Charset.defaultCharset());
            SpringApplication.run(AdaptorInboundApplication.class, args);
		} catch (Exception e) {
            LOGGER.error("Unable to start IB adapter application because --> {}",e.getMessage());
            sendEmail("Unable to start IB adapter application.", "Detailed error: "+e.getMessage());
			throw new RuntimeException(e);
		}
    }

    @PostConstruct
    public void startProcess() {
        try {
            List<String> browsedMessageList = queueService.pollAllBrowsedMessages();
            while(true){
                List<String> completableFuture = queueService.pollFromQueues();
                LOGGER.info("Poll messages job is complete, application is into sleep mode.");
                Thread.sleep(pollInterval);
                LOGGER.info("Starting to poll the queue after sleep interval....");
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("IB adapter application stopped ---> {}",e.getMessage());
            sendEmail("IB adapter application stopped.", "Detailed error: "+e.getMessage());
            throw new RuntimeException(e);
        }
	}
}