package com.dataracy.modules.common.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AwsS3Config {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public AmazonS3 amazonS3() {
        try {
            BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
            return AmazonS3ClientBuilder.standard()
                    .withRegion(region)
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .build();
            } catch (Exception e) {
                log.error("AWS S3 클라이언트 생성 실패", e);
                throw new IllegalStateException("AWS S3 클라이언트를 생성할 수 없습니다.", e);
            }
    }
}
