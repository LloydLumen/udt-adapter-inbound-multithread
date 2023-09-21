package com.udtrucks.adaptor.services;

import com.udtrucks.adaptor.QueueController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.udtrucks.adaptor.constants.Constants.properties;

@Service
public class QueueService {
    private static final Logger LOGGER = LoggerFactory.getILoggerFactory().getLogger("QueueService");
    @Autowired
    QueueController queueController;

    public List<String> pollFromQueues() throws InterruptedException, ExecutionException {
        String customerQueues = (String) properties.get("project.mq.poll.queues.customer");
        String organizationQueues = (String) properties.get("project.mq.poll.queues.organization");
        String vehicleQueues = (String) properties.get("project.mq.poll.queues.vehicle");

        List<String> allMessages = new ArrayList<>();

        CompletableFuture<List<String>> customerMessagesF = CompletableFuture.supplyAsync(() -> {
            try {
                return queueController.startCustomer(customerQueues);
            } catch (Exception e) {
                LOGGER.error("Exception while initiating the job to poll the customer queue because: " + e.getMessage());
                return new ArrayList<String>();
            }
        });

        CompletableFuture<List<String>> organizationMessagesF = CompletableFuture.supplyAsync(() -> {
            try {
                return queueController.startOrganization(organizationQueues);
            } catch (Exception e) {
                LOGGER.error("Exception while initiating the job to poll the organization queue because: " + e.getMessage());
                return new ArrayList<String>();
            }
        });

        CompletableFuture<List<String>> vehicleMessagesF = CompletableFuture.supplyAsync(() -> {
            try {
                return queueController.startVehicle(vehicleQueues);
            } catch (Exception e) {
                LOGGER.error("Exception while initiating the job to poll the vehicle queue because: " + e.getMessage());
                return new ArrayList<String>();
            }
        });


        try {
            List<String> resultCustomerList = customerMessagesF.get();
            List<String> resultOrganizationList = organizationMessagesF.get();
            List<String> resultVehicleList = vehicleMessagesF.get();
            allMessages.addAll(resultCustomerList);
//            allMessages.addAll(resultOrganizationList);
//            allMessages.addAll(resultVehicleList);
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Exception in inbound adapter application because: {}", e.getMessage());
        }
        return allMessages;
    }

    public List<String> pollAllBrowsedMessages(){
        return queueController.processBrowsedMessages();
    }
}