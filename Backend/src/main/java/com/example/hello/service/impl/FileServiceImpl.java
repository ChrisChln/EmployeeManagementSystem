package com.example.hello.service.impl;

import com.example.hello.service.FileService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private MinioClient minioClient;

    private static final String BUCKET_NAME = "22y35qwz-zxyf";
    private static final String EXTERNAL_ENDPOINT = "http://objectstorageapi.usw.sealos.io";

    @Override
    public String upload(MultipartFile file) {
        // Generate unique file name
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectName = UUID.randomUUID().toString().replaceAll("-", "") + extension;
        
        try {
            // Upload file to MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(objectName)
                            .contentType(file.getContentType())
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .build()
            );
            
            // Return file access URL
            return EXTERNAL_ENDPOINT + "/" + BUCKET_NAME + "/" + objectName;
        } catch (Exception e) {
            log.error("File upload failed", e);
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }
} 