package com.dataracy.modules.common.logging.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.common.logging.*;

@DisplayName("LoggerFactory 테스트")
class LoggerFactoryTest {

  @Nested
  @DisplayName("api 메서드 테스트")
  class ApiTest {

    @Test
    @DisplayName("ApiLogger 싱글톤 인스턴스 반환")
    void apiApiLoggerReturnsSingletonInstance() {
      // when
      ApiLogger logger1 = LoggerFactory.api();
      ApiLogger logger2 = LoggerFactory.api();

      // then
      assertAll(
          () -> assertThat(logger1).isNotNull(),
          () -> assertThat(logger2).isNotNull(),
          () -> assertThat(logger1).isSameAs(logger2));
    }
  }

  @Nested
  @DisplayName("service 메서드 테스트")
  class ServiceTest {

    @Test
    @DisplayName("ServiceLogger 싱글톤 인스턴스 반환")
    void serviceServiceLoggerReturnsSingletonInstance() {
      // when
      ServiceLogger logger1 = LoggerFactory.service();
      ServiceLogger logger2 = LoggerFactory.service();

      // then
      assertAll(
          () -> assertThat(logger1).isNotNull(),
          () -> assertThat(logger2).isNotNull(),
          () -> assertThat(logger1).isSameAs(logger2));
    }
  }

  @Nested
  @DisplayName("domain 메서드 테스트")
  class DomainTest {

    @Test
    @DisplayName("DomainLogger 싱글톤 인스턴스 반환")
    void domainDomainLoggerReturnsSingletonInstance() {
      // when
      DomainLogger logger1 = LoggerFactory.domain();
      DomainLogger logger2 = LoggerFactory.domain();

      // then
      assertAll(
          () -> assertThat(logger1).isNotNull(),
          () -> assertThat(logger2).isNotNull(),
          () -> assertThat(logger1).isSameAs(logger2));
    }
  }

  @Nested
  @DisplayName("persistence 메서드 테스트")
  class PersistenceTest {

    @Test
    @DisplayName("PersistenceLogger 싱글톤 인스턴스 반환")
    void persistencePersistenceLoggerReturnsSingletonInstance() {
      // when
      PersistenceLogger logger1 = LoggerFactory.db();
      PersistenceLogger logger2 = LoggerFactory.db();

      // then
      assertAll(
          () -> assertThat(logger1).isNotNull(),
          () -> assertThat(logger2).isNotNull(),
          () -> assertThat(logger1).isSameAs(logger2));
    }
  }

  @Nested
  @DisplayName("queryDsl 메서드 테스트")
  class QueryDslTest {

    @Test
    @DisplayName("QueryDslLogger 싱글톤 인스턴스 반환")
    void queryDslQueryDslLoggerReturnsSingletonInstance() {
      // when
      QueryDslLogger logger1 = LoggerFactory.query();
      QueryDslLogger logger2 = LoggerFactory.query();

      // then
      assertAll(
          () -> assertThat(logger1).isNotNull(),
          () -> assertThat(logger2).isNotNull(),
          () -> assertThat(logger1).isSameAs(logger2));
    }
  }

  @Nested
  @DisplayName("kafka 메서드 테스트")
  class KafkaTest {

    @Test
    @DisplayName("KafkaLogger 싱글톤 인스턴스 반환")
    void kafkaKafkaLoggerReturnsSingletonInstance() {
      // when
      KafkaLogger logger1 = LoggerFactory.kafka();
      KafkaLogger logger2 = LoggerFactory.kafka();

      // then
      assertAll(
          () -> assertThat(logger1).isNotNull(),
          () -> assertThat(logger2).isNotNull(),
          () -> assertThat(logger1).isSameAs(logger2));
    }
  }

  @Nested
  @DisplayName("elastic 메서드 테스트")
  class ElasticTest {

    @Test
    @DisplayName("ElasticLogger 싱글톤 인스턴스 반환")
    void elasticElasticLoggerReturnsSingletonInstance() {
      // when
      ElasticLogger logger1 = LoggerFactory.elastic();
      ElasticLogger logger2 = LoggerFactory.elastic();

      // then
      assertAll(
          () -> assertThat(logger1).isNotNull(),
          () -> assertThat(logger2).isNotNull(),
          () -> assertThat(logger1).isSameAs(logger2));
    }
  }

  @Nested
  @DisplayName("redis 메서드 테스트")
  class RedisTest {

    @Test
    @DisplayName("RedisLogger 싱글톤 인스턴스 반환")
    void redisRedisLoggerReturnsSingletonInstance() {
      // when
      RedisLogger logger1 = LoggerFactory.redis();
      RedisLogger logger2 = LoggerFactory.redis();

      // then
      assertAll(
          () -> assertThat(logger1).isNotNull(),
          () -> assertThat(logger2).isNotNull(),
          () -> assertThat(logger1).isSameAs(logger2));
    }
  }

  @Nested
  @DisplayName("scheduler 메서드 테스트")
  class SchedulerTest {

    @Test
    @DisplayName("SchedulerLogger 싱글톤 인스턴스 반환")
    void schedulerSchedulerLoggerReturnsSingletonInstance() {
      // when
      SchedulerLogger logger1 = LoggerFactory.scheduler();
      SchedulerLogger logger2 = LoggerFactory.scheduler();

      // then
      assertAll(
          () -> assertThat(logger1).isNotNull(),
          () -> assertThat(logger2).isNotNull(),
          () -> assertThat(logger1).isSameAs(logger2));
    }
  }

  @Nested
  @DisplayName("distributedLock 메서드 테스트")
  class DistributedLockTest {

    @Test
    @DisplayName("DistributedLockLogger 싱글톤 인스턴스 반환")
    void distributedLockDistributedLockLoggerReturnsSingletonInstance() {
      // when
      DistributedLockLogger logger1 = LoggerFactory.lock();
      DistributedLockLogger logger2 = LoggerFactory.lock();

      // then
      assertAll(
          () -> assertThat(logger1).isNotNull(),
          () -> assertThat(logger2).isNotNull(),
          () -> assertThat(logger1).isSameAs(logger2));
    }
  }

  @Nested
  @DisplayName("common 메서드 테스트")
  class CommonTest {

    @Test
    @DisplayName("CommonLogger 싱글톤 인스턴스 반환")
    void commonCommonLoggerReturnsSingletonInstance() {
      // when
      CommonLogger logger1 = LoggerFactory.common();
      CommonLogger logger2 = LoggerFactory.common();

      // then
      assertAll(
          () -> assertThat(logger1).isNotNull(),
          () -> assertThat(logger2).isNotNull(),
          () -> assertThat(logger1).isSameAs(logger2));
    }
  }

  @Nested
  @DisplayName("모든 로거 타입 테스트")
  class AllLoggersTest {

    @Test
    @DisplayName("모든 로거가 정상적으로 생성되는지 확인")
    void allLoggersAreCreatedSuccessfully() {
      // when
      ApiLogger apiLogger = LoggerFactory.api();
      ServiceLogger serviceLogger = LoggerFactory.service();
      DomainLogger domainLogger = LoggerFactory.domain();
      PersistenceLogger persistenceLogger = LoggerFactory.db();
      QueryDslLogger queryDslLogger = LoggerFactory.query();
      KafkaLogger kafkaLogger = LoggerFactory.kafka();
      ElasticLogger elasticLogger = LoggerFactory.elastic();
      RedisLogger redisLogger = LoggerFactory.redis();
      SchedulerLogger schedulerLogger = LoggerFactory.scheduler();
      DistributedLockLogger distributedLockLogger = LoggerFactory.lock();
      CommonLogger commonLogger = LoggerFactory.common();

      // then - 모든 로거가 null이 아니고 정상적으로 생성되었는지 확인
      assertAll(
          () -> assertThat(apiLogger).isNotNull(),
          () -> assertThat(serviceLogger).isNotNull(),
          () -> assertThat(domainLogger).isNotNull(),
          () -> assertThat(persistenceLogger).isNotNull(),
          () -> assertThat(queryDslLogger).isNotNull(),
          () -> assertThat(kafkaLogger).isNotNull(),
          () -> assertThat(elasticLogger).isNotNull(),
          () -> assertThat(redisLogger).isNotNull(),
          () -> assertThat(schedulerLogger).isNotNull(),
          () -> assertThat(distributedLockLogger).isNotNull(),
          () -> assertThat(commonLogger).isNotNull());
    }
  }
}
