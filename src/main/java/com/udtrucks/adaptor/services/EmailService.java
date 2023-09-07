package com.udtrucks.adaptor.services;

import com.udtrucks.adaptor.utils.EmailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.mail.Session;

import java.util.Properties;

import static com.udtrucks.adaptor.constants.Constants.properties;

public class EmailService {

    private final static Logger LOGGER = LoggerFactory.getILoggerFactory().getLogger("EmailService");
    public static void sendEmail(String reason, String summary) {
        String mail = properties.getProperty("mail.list");
        String APPLICATION_ENV = properties.getProperty("envi.name");
        String[] emailList = mail.split(",");
        String messageBody = getMsgBody(reason, summary, "");
        String smtpHostServer = "mailjpn.it.udtrucks.net";
        Properties props = System.getProperties();

        props.put("mail.smtp.host", smtpHostServer);
        props.put("mail.smtp.port", 25);

        Session session = Session.getInstance(props, null);

        EmailUtil.send(session, emailList, "Notification from IB Adapter application [ MDS - "+ APPLICATION_ENV +"].", messageBody);
    }

    /*
    Error:
    Exception while pre-processing the < Message Onetime Setup > input message - PartyID <PartyID>

    Error Summary:
    ADP Server          - VM - UDTYON50xx
    Error log path      - E:\\adaptor-inbound\\logs
    XML Payload path    - E:\\udt-adaptor-inbound\\cdb\\<mq-failed-messages>
    XML Payload file    - <XML file name processed>

    Stacktrace:
    <Complete error log>
     */

    private static String getMsgBody(String reason, String summary, String stackTrace){
        StringBuilder emailBody = new StringBuilder();
        emailBody.append("</br><b>Error</b></br>");
        emailBody.append(reason+"</br></br>");
        emailBody.append("</br><b>Error summary</b></br>");
        emailBody.append("ADP Server                    :</b>"+properties.getProperty("server.name")+"</br>");
        emailBody.append("Error log path                :</b>"+properties.getProperty("logging.file.name")+"</br>");
        emailBody.append("(Please look into the log file of the respective date.)</br>");
        emailBody.append(summary+"</br>");
        if (!stackTrace.equals("")){
            emailBody.append("</br><b>Stacktrace:</b></br>");
            emailBody.append(stackTrace);
        }
        return emailBody.toString();
    }
}
