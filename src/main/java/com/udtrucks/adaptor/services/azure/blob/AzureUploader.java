package com.udtrucks.adaptor.services.azure.blob;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.omg.SendingContext.RunTime;

import javax.net.ssl.SSLContext;

public class AzureUploader {
    public static void main(String args[]) throws IOException, NoSuchAlgorithmException {

        System.out.println("Env --> "+System.getenv("JAVA_HOME"));
        String path = "src/main/resources";

        File file = new File(path);
        String absolutePath = file.getAbsolutePath();
        String relativePath = file.getPath();

        System.out.println(absolutePath);

        System.out.println("Env --> "+System.getenv("JAVA_HOME"));

        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .endpoint("https://jpestudtmds01.blob.core.windows.net?sp=racwdlmeop&st=2023-02-03T06:07:47Z&se=2023-12-30T14:07:47Z&sv=2021-06-08&sr=c&sig=Q0P3d76KZ0lBo1N9pKkLRAeRbmFdGlzr1D1nf5%2F7S90%3D")
                .buildClient();

        // Upload the file to Blob Storage
        file = new File("C:\\Users\\A440163X\\Documents\\udt-adaptor-inbound\\mq-transformed-messages\\XML_UPload_Test.xml");
        FileInputStream stream = new FileInputStream(file);
        String blobName = file.getName();
        System.out.println("Blob to be uploaded is --> "+blobName);
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("data");
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        blobClient.upload(stream);
        stream.close();
        System.out.println("File upload status -- "+String.valueOf(blobClient.exists()));
    }
}
