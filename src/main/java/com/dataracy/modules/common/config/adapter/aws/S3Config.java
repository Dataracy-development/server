package com.dataracy.modules.common.config.adapter.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dataracy.modules.common.logging.support.LoggerFactory;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

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
   * <p>AWS 자격 증명은 다음 순서로 시도됩니다: 1. 설정된 access-key, secret-key가 있으면 AWSStaticCredentialsProvider 사용
   * 2. 그렇지 않으면 DefaultAWSCredentialsProviderChain 사용 (환경변수, IAM 역할 등)
   *
   * <p>보안상 권장사항: - 프로덕션 환경에서는 IAM 역할이나 환경변수 사용 권장 - 하드코딩된 자격 증명은 보안 위험을 초래할 수 있음
   *
   * @return 구성된 AmazonS3 클라이언트 인스턴스
   */
  @Bean
  public AmazonS3 amazonS3() {
    try {
      AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard().withRegion(region);

      // 설정된 자격 증명이 있으면 사용, 없으면 기본 자격 증명 체인 사용
      if (!accessKey.isBlank() && !secretKey.isBlank()) {
        LoggerFactory.common()
            .logWarning("AWS S3 설정", "하드코딩된 AWS 자격 증명 사용 중 - 프로덕션 환경에서는 IAM 역할 사용 권장");
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        builder.withCredentials(new AWSStaticCredentialsProvider(credentials));
      } else {
        LoggerFactory.common().logInfo("AWS S3 설정", "기본 AWS 자격 증명 체인 사용 (IAM 역할 또는 환경변수)");
        builder.withCredentials(DefaultAWSCredentialsProviderChain.getInstance());
      }

      return builder.build();
    } catch (Exception e) {
      LoggerFactory.common().logError("AWS S3 클라이언트 생성", "AWS S3 클라이언트를 생성할 수 없습니다.", e);
      throw new IllegalStateException("AWS S3 클라이언트를 생성할 수 없습니다.", e);
    }
  }

  @PostConstruct
  public void validateProperties() {
    if (region.isBlank()) {
      throw new IllegalStateException("AWS S3 클라이언트 초기화에 필요한 region 설정이 누락되었습니다.");
    }

    // 자격 증명이 설정되지 않은 경우 경고 로그 출력 (기본 자격 증명 체인 사용 예정)
    if (accessKey.isBlank() || secretKey.isBlank()) {
      LoggerFactory.common().logInfo("AWS S3 설정", "하드코딩된 자격 증명이 설정되지 않음 - 기본 AWS 자격 증명 체인 사용 예정");
    }
  }
}
