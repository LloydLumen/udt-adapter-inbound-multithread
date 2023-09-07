package com.udtrucks.adaptor.services.azure.blob;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.io.IOException;


public interface IAzureBlobService {
      public boolean uploadBlob(String message, String blobName) throws IOException, InterruptedException;

}
