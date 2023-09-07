package com.udtrucks.adaptor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomerMessageTest {

    @Autowired QueueController queueController;

    @Autowired
    JmsTemplate jmsTemplate;

    @Test
    public void testWorkflow() throws Exception {
        putMessages();
        String[] qList = {"queue.customer"};
        List<String> messageList = queueController.startCustomer(Arrays.toString(qList));
        assertEquals(2,messageList.size());
    }


    public void putMessages(){
        jmsTemplate.convertAndSend("queue.customer","");

    }


}
