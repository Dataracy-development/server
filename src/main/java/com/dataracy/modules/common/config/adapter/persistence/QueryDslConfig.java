/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.common.config.adapter.persistence;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class QueryDslConfig {
  private final EntityManager entityManager;

  /**
   * QueryDSL의 JPAQueryFactory 빈을 생성하여 반환합니다. 이 빈을 통해 애플리케이션 내에서 타입 안전한 JPA 쿼리를 작성할 수 있습니다.
   *
   * @return QueryDSL 쿼리 생성을 위한 JPAQueryFactory 인스턴스
   */
  @Bean
  public JPAQueryFactory jpaQueryFactory() {
    return new JPAQueryFactory(entityManager);
  }
}
