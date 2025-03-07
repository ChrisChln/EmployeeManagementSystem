package com.example.hello.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint("http://objectstorageapi.usw.sealos.io")
                .credentials("22y35qwz", "xf27dr7ts4fnrg8c")
                .build();
    }
} 