package com.udtrucks.adaptor.services.queue;

import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.List;

@Component
public interface IQueueWorkflowService {
    public List<Message> pollMessage(String queueName) throws JMSException;
    public List<Message> pollMessage(String... queueNames) throws JMSException;
    List<String> writeMessageToFile(List<Message> messageList,String fileBaseDir) throws Exception;
    String identifyMessage(String file, String messageType);
    String prepareMessageProperties(String messageType);
    public String prepareQueueProperties(String queueType);
    boolean validateMessage(String messageFile,String messageType);
    boolean transformMessage();
    String parseXMLStringtoObject(String jsonMessage);
    boolean uploadMessageFileToBlob(String jsonObject,String messageFilePath);
    Object invokeCDIRequestForMessage(String message) throws Exception;
    Object test(String message) throws Exception;
}