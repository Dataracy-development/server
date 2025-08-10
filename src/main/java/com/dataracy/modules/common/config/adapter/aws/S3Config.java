package com.dataracy.modules.common.config.adapter.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class S3Config {
    @Value("${cloud.aws.credentials.access-key:}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key:}")
    private String secretKey;

    @Value("${cloud.aws.region.static:}")
    private String region;

    /**
     * AWS S3 클라이언트(AmazonS3) 빈을 생성하여 반환합니다.
     *
     * 주어진 접근 키, 시크릿 키, 리전을 사용하여 AmazonS3 클라이언트를 생성합니다.
     * 클라이언트 생성에 실패할 경우 IllegalStateException이 발생합니다.
     *
     * @return 구성된 AmazonS3 클라이언트 인스턴스
     */
    @Bean
    public AmazonS3 amazonS3() {
        try {
            BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
            return AmazonS3ClientBuilder.standard()
                    .withRegion(region)
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .build();
            } catch (Exception e) {
                LoggerFactory.common().logError("AWS S3 클라이언트 생성", "AWS S3 클라이언트를 생성할 수 없습니다.");
                throw new IllegalStateException("AWS S3 클라이언트를 생성할 수 없습니다.", e);
            }
    }

    @PostConstruct
    public void validateProperties() {
        if (accessKey.isBlank() || secretKey.isBlank() || region.isBlank()) {
            throw new IllegalStateException("AWS S3 클라이언트 초기화에 필요한 설정이 누락되었습니다.");
        }
    }
}
