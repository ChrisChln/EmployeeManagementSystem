package com.example.hello.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    /**
     * Upload file to object storage
     * @param file file to upload
     * @return file access URL
     */
    String upload(MultipartFile file);
} 