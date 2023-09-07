package com.udtrucks.adaptor.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;

public class EmailUtil {

    private static final Logger LOGGER = LoggerFactory.getILoggerFactory().getLogger("EmailUtil");

    /**
     * Utility method to send simple HTML email
     * @param session
     * @param toEmail
     * @param subject
     * @param body
     */
    public static void send(Session session, String[] toEmail, String subject, String body){
        try
        {
            LOGGER.info("Sending email alert....");
            MimeMessage msg = new MimeMessage(session);
            //set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");
            msg.setFrom(new InternetAddress("no_reply@udtrucks.net", "NoReply-JD"));
            msg.setReplyTo(InternetAddress.parse("no_reply@udtrucks.net", false));
            msg.setSubject(subject, "UTF-8");
//            msg.setText(body, "UTF-8");
            msg.setContent(body, "text/html; charset=utf-8");
            msg.setSentDate(new Date());
            Address[] recipients = getRecipients(toEmail);
            msg.setRecipients(Message.RecipientType.TO, recipients);
            LOGGER.info("Message is ready");
            Transport.send(msg);
            LOGGER.info("Email(s) sent successfully");
        }
        catch (Exception e) {
            LOGGER.error("Error in sending email because ---> "+e.getMessage());
        }
    }

    private static Address[] getRecipients(String[] toEmail){
        Address[] recipients = new InternetAddress[toEmail.length];
        try{
            for (int i=0; i < toEmail.length; i++){
                recipients[i] = new InternetAddress(toEmail[i]);
            }
        }catch (AddressException ae){
            LOGGER.error("Error in parsing the email address for sending alerts because: {}", ae.getMessage());
        }
        return recipients;
    }
}