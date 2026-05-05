package com.restaurador.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3StorageService {

    private final S3Client s3Client;

    @Value("${s3.bucket-name}")
    private String bucketName;

    public S3StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file, UUID userId) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") ? 
                           originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        
        String key = "uploads/user-" + userId + "/" + UUID.randomUUID() + extension;

        s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return key;
    }
}
