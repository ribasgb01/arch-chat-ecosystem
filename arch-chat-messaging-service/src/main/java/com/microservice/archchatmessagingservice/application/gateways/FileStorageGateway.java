package com.microservice.archchatmessagingservice.application.gateways;

import com.microservice.archchatmessagingservice.domain.Attachment;

import java.io.InputStream;
import java.util.UUID;

public interface FileStorageGateway {

    Attachment uploadFile(InputStream inputStream, long fileSize, String fileName, String contentType, UUID chatId, Double duration);
    void deleteFile(String fileKey);
    String getPresignedUrl(String fileKey);
}
