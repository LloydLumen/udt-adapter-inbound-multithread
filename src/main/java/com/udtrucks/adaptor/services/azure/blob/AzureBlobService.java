package com.udtrucks.adaptor.services.azure.blob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.udtrucks.adaptor.constants.Constants.properties;

@Service
public class AzureBlobService implements IAzureBlobService{
    private static final Logger LOGGER  = LoggerFactory.getILoggerFactory().getLogger("QueueController");

    @Override
    public boolean uploadBlob(String message, String blobName) throws IOException, InterruptedException {

        String azureBlobUploaderJarPath  = (String) properties.get("project.blob.uploader.utility");
        String azureBlobUploaderLoggerPath  = (String) properties.get("project.blob.uploader.log");
        LOGGER.info("Executing upload job using utility {} and logging the execution logs into {}.",
                azureBlobUploaderJarPath, azureBlobUploaderLoggerPath);
        LOGGER.info("Received message to upload to Azure Blob Storage with path --> "+blobName);
        List<String> callUploadService = new ArrayList<>();
        callUploadService.add("java");
        callUploadService.add("-jar");
//        callUploadService.add("E:\\adaptor-inbound\\resources\\AzureBlobUploader.jar");
        callUploadService.add(azureBlobUploaderJarPath);
        callUploadService.add(message);
        callUploadService.add(blobName);
        LOGGER.info("Input JSON Payload for upload is --> \n"+message);
        ProcessBuilder pb = new ProcessBuilder(callUploadService);

        // Redirect the output to a log file
//        File logFile = new File(azureBlobUploaderLoggerPath);
        File logFile = new File("C:\\Users\\LloydKoshy\\OneDrive - LumenData, Inc\\Documents\\UD trucks\\AdapterApplication\\udt-adapter-inbound-multithread\\logs\\AzureUploadService.log");
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));

        // Start the process
        Process process = pb.start();

        // Wait for the process to complete
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            return true;
        } else {
            return false;
        }
    }
}