package com.udtrucks.adaptor;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import javax.jms.*;
import java.io.File;
import java.util.ArrayList;

public class MQReaderSSL {

    public static void main(String[] args) throws JMSException {
        Connection conn=null;

        Session sess;
        ArrayList<String> msgs = new ArrayList<>();

        System.setProperty("javax.net.ssl.keyStore", "C:\\Users\\a460440\\Documents\\ibmwebspheremqcs-ws-s-udtmds-qa\\git key.jks");
        System.setProperty("javax.net.ssl.trustStore", "C:\\Users\\a460440\\Documents\\ibmwebspheremqcs-ws-s-udtmds-qa\\key.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "udtmds");
        System.setProperty("javax.net.ssl.keyStorePassword", "udtmds");
        System.setProperty("com.ibm.mq.cfg.useIBMCipherMappings", "false");

        try {

            MQConnectionFactory cf = new MQConnectionFactory();
            cf.setQueueManager("UDAZWEET1");
            cf.setHostName("udazweet1.udcn.udtrucks.net");
            cf.setPort(1414);
            cf.setChannel("UDTMDS.SRV01");
            cf.setTransportType(WMQConstants.WMQ_CM_CLIENT);
            cf.setSSLCipherSuite("TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384");

            conn = cf.createConnection("CS-WS-S-UDTMDS-QA", "1(FzGZKHgQIt");

            System.out.println("created connn " + conn);
            conn.start();
            sess = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = sess.createQueue("UDTMDS.APP.ACCOUNT.IN");

            MessageConsumer consumer = sess.createConsumer(queue);

            while(true) {
                Message message = consumer.receive();
                if (message == null) {
                    System.out.println("No messages received in the last 5 seconds");
                } else {
                    String msgText = message instanceof TextMessage ? ((TextMessage) message).getText() : "Non-text message";
                    System.out.println("Received message: " + msgText);
                    msgs.add(msgText);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            conn.close();
        }
    }

}