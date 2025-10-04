package com.dataracy.modules.email.adapter.ses.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dataracy.modules.common.logging.support.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/** AWS Ses Configuration 설정 */
@Configuration
@RequiredArgsConstructor
public class SesConfig {
  @Value("${aws.ses.access-key:}")
  private String accessKey;

  @Value("${aws.ses.secret-key:}")
  private String secretKey;

  @Value("${aws.ses.region:}")
  private String region;

  /**
   * AWS SES 클라이언트(AmazonSimpleEmailService) 빈을 생성하여 반환합니다.
   *
   * <p>AWS 자격 증명은 다음 순서로 시도됩니다: 1. 설정된 access-key, secret-key가 있으면 AWSStaticCredentialsProvider 사용
   * 2. 그렇지 않으면 DefaultAWSCredentialsProviderChain 사용 (환경변수, IAM 역할 등)
   *
   * <p>보안상 권장사항: - 프로덕션 환경에서는 IAM 역할이나 환경변수 사용 권장 - 하드코딩된 자격 증명은 보안 위험을 초래할 수 있음
   *
   * @return 구성된 AmazonSimpleEmailService 클라이언트 인스턴스
   */
  @Bean
  public AmazonSimpleEmailService amazonSimpleEmailService() {
    try {
      AmazonSimpleEmailServiceClientBuilder builder =
          AmazonSimpleEmailServiceClientBuilder.standard().withRegion(region);

      // 설정된 자격 증명이 있으면 사용, 없으면 기본 자격 증명 체인 사용
      if (!accessKey.isBlank() && !secretKey.isBlank()) {
        LoggerFactory.common()
            .logWarning("AWS SES 설정", "하드코딩된 AWS 자격 증명 사용 중 - 프로덕션 환경에서는 IAM 역할 사용 권장");
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        builder.withCredentials(new AWSStaticCredentialsProvider(credentials));
      } else {
        LoggerFactory.common().logInfo("AWS SES 설정", "기본 AWS 자격 증명 체인 사용 (IAM 역할 또는 환경변수)");
        builder.withCredentials(DefaultAWSCredentialsProviderChain.getInstance());
      }

      return builder.build();
    } catch (Exception e) {
      LoggerFactory.common().logError("AWS SES 클라이언트 생성", "AWS SES 클라이언트를 생성할 수 없습니다.", e);
      throw new IllegalStateException("AWS SES 클라이언트를 생성할 수 없습니다.", e);
    }
  }

  @PostConstruct
  public void validateProperties() {
    if (region.isBlank()) {
      throw new IllegalStateException("AWS SES 클라이언트 초기화에 필요한 region 설정이 누락되었습니다.");
    }

    // 자격 증명이 설정되지 않은 경우 정보 로그 출력 (기본 자격 증명 체인 사용 예정)
    if (accessKey.isBlank() || secretKey.isBlank()) {
      LoggerFactory.common().logInfo("AWS SES 설정", "하드코딩된 자격 증명이 설정되지 않음 - 기본 AWS 자격 증명 체인 사용 예정");
    }
  }
}
