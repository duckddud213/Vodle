package com.tes.server.global.S3.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    // IAM 사용자 액세스 키
    @Value("${spring.cloud.aws.s3.credentials.access-key}")
    private String accessKey;

    // IAM 사용자 비밀 액세스 키
    @Value("${spring.cloud.aws.s3.credentials.secret-key}")
    private String secretKey;

    // 버킷 리전
    @Value("${spring.cloud.aws.region.static}")
    private String region;

    // AmazonS3Client에 사용자 인증 정보, 버켓 리전 설정
    @Bean
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey,secretKey);

        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
    }
}
