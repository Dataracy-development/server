package com.dataracy.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

/** 통합 테스트용 베이스 클래스 공통 설정을 제공합니다. */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@Transactional
public abstract class IntegrationTestBase {
  // 공통 설정만 제공, 구현은 하위 클래스에서
}
