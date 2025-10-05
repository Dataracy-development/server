package com.dataracy.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/** 테스트용 메타 어노테이션 공통 테스트 설정을 적용합니다. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ActiveProfiles("test")
@Import({TestConfig.class, IntegrationTestConfig.class})
public @interface TestSliceConfig {}
