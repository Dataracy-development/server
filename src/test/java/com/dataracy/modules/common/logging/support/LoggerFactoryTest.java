package com.dataracy.modules.common.logging.support;

import com.dataracy.modules.common.logging.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoggerFactoryTest {

    @Test
    @DisplayName("api() - ApiLogger 싱글톤 인스턴스 반환")
    void api_ReturnsSingletonApiLogger() {
        // when
        ApiLogger logger1 = LoggerFactory.api();
        ApiLogger logger2 = LoggerFactory.api();

        // then
        assertThat(logger1).isNotNull();
        assertThat(logger2).isNotNull();
        assertThat(logger1).isSameAs(logger2);
        assertThat(logger1).isInstanceOf(ApiLogger.class);
    }

    @Test
    @DisplayName("service() - ServiceLogger 싱글톤 인스턴스 반환")
    void service_ReturnsSingletonServiceLogger() {
        // when
        ServiceLogger logger1 = LoggerFactory.service();
        ServiceLogger logger2 = LoggerFactory.service();

        // then
        assertThat(logger1).isNotNull();
        assertThat(logger2).isNotNull();
        assertThat(logger1).isSameAs(logger2);
        assertThat(logger1).isInstanceOf(ServiceLogger.class);
    }

    @Test
    @DisplayName("domain() - DomainLogger 싱글톤 인스턴스 반환")
    void domain_ReturnsSingletonDomainLogger() {
        // when
        DomainLogger logger1 = LoggerFactory.domain();
        DomainLogger logger2 = LoggerFactory.domain();

        // then
        assertThat(logger1).isNotNull();
        assertThat(logger2).isNotNull();
        assertThat(logger1).isSameAs(logger2);
        assertThat(logger1).isInstanceOf(DomainLogger.class);
    }

    @Test
    @DisplayName("persistence() - PersistenceLogger 싱글톤 인스턴스 반환")
    void persistence_ReturnsSingletonPersistenceLogger() {
        // when
        PersistenceLogger logger1 = LoggerFactory.db();
        PersistenceLogger logger2 = LoggerFactory.db();

        // then
        assertThat(logger1).isNotNull();
        assertThat(logger2).isNotNull();
        assertThat(logger1).isSameAs(logger2);
        assertThat(logger1).isInstanceOf(PersistenceLogger.class);
    }

    @Test
    @DisplayName("query() - QueryDslLogger 싱글톤 인스턴스 반환")
    void query_ReturnsSingletonQueryDslLogger() {
        // when
        QueryDslLogger logger1 = LoggerFactory.query();
        QueryDslLogger logger2 = LoggerFactory.query();

        // then
        assertThat(logger1).isNotNull();
        assertThat(logger2).isNotNull();
        assertThat(logger1).isSameAs(logger2);
        assertThat(logger1).isInstanceOf(QueryDslLogger.class);
    }

    @Test
    @DisplayName("kafka() - KafkaLogger 싱글톤 인스턴스 반환")
    void kafka_ReturnsSingletonKafkaLogger() {
        // when
        KafkaLogger logger1 = LoggerFactory.kafka();
        KafkaLogger logger2 = LoggerFactory.kafka();

        // then
        assertThat(logger1).isNotNull();
        assertThat(logger2).isNotNull();
        assertThat(logger1).isSameAs(logger2);
        assertThat(logger1).isInstanceOf(KafkaLogger.class);
    }

    @Test
    @DisplayName("elastic() - ElasticLogger 싱글톤 인스턴스 반환")
    void elastic_ReturnsSingletonElasticLogger() {
        // when
        ElasticLogger logger1 = LoggerFactory.elastic();
        ElasticLogger logger2 = LoggerFactory.elastic();

        // then
        assertThat(logger1).isNotNull();
        assertThat(logger2).isNotNull();
        assertThat(logger1).isSameAs(logger2);
        assertThat(logger1).isInstanceOf(ElasticLogger.class);
    }

    @Test
    @DisplayName("redis() - RedisLogger 싱글톤 인스턴스 반환")
    void redis_ReturnsSingletonRedisLogger() {
        // when
        RedisLogger logger1 = LoggerFactory.redis();
        RedisLogger logger2 = LoggerFactory.redis();

        // then
        assertThat(logger1).isNotNull();
        assertThat(logger2).isNotNull();
        assertThat(logger1).isSameAs(logger2);
        assertThat(logger1).isInstanceOf(RedisLogger.class);
    }

    @Test
    @DisplayName("scheduler() - SchedulerLogger 싱글톤 인스턴스 반환")
    void scheduler_ReturnsSingletonSchedulerLogger() {
        // when
        SchedulerLogger logger1 = LoggerFactory.scheduler();
        SchedulerLogger logger2 = LoggerFactory.scheduler();

        // then
        assertThat(logger1).isNotNull();
        assertThat(logger2).isNotNull();
        assertThat(logger1).isSameAs(logger2);
        assertThat(logger1).isInstanceOf(SchedulerLogger.class);
    }

    @Test
    @DisplayName("distributedLock() - DistributedLockLogger 싱글톤 인스턴스 반환")
    void distributedLock_ReturnsSingletonDistributedLockLogger() {
        // when
        DistributedLockLogger logger1 = LoggerFactory.lock();
        DistributedLockLogger logger2 = LoggerFactory.lock();

        // then
        assertThat(logger1).isNotNull();
        assertThat(logger2).isNotNull();
        assertThat(logger1).isSameAs(logger2);
        assertThat(logger1).isInstanceOf(DistributedLockLogger.class);
    }

    @Test
    @DisplayName("common() - CommonLogger 싱글톤 인스턴스 반환")
    void common_ReturnsSingletonCommonLogger() {
        // when
        CommonLogger logger1 = LoggerFactory.common();
        CommonLogger logger2 = LoggerFactory.common();

        // then
        assertThat(logger1).isNotNull();
        assertThat(logger2).isNotNull();
        assertThat(logger1).isSameAs(logger2);
        assertThat(logger1).isInstanceOf(CommonLogger.class);
    }

    @Test
    @DisplayName("모든 로거가 서로 다른 인스턴스인지 확인")
    void allLoggers_AreDifferentInstances() {
        // when
        ApiLogger apiLogger = LoggerFactory.api();
        ServiceLogger serviceLogger = LoggerFactory.service();
        DomainLogger domainLogger = LoggerFactory.domain();
        PersistenceLogger persistenceLogger = LoggerFactory.db();
        QueryDslLogger queryLogger = LoggerFactory.query();
        KafkaLogger kafkaLogger = LoggerFactory.kafka();
        ElasticLogger elasticLogger = LoggerFactory.elastic();
        RedisLogger redisLogger = LoggerFactory.redis();
        SchedulerLogger schedulerLogger = LoggerFactory.scheduler();
        DistributedLockLogger distributedLockLogger = LoggerFactory.lock();
        CommonLogger commonLogger = LoggerFactory.common();

        // then
        assertThat(apiLogger).isNotSameAs(serviceLogger);
        assertThat(apiLogger).isNotSameAs(domainLogger);
        assertThat(apiLogger).isNotSameAs(persistenceLogger);
        assertThat(apiLogger).isNotSameAs(queryLogger);
        assertThat(apiLogger).isNotSameAs(kafkaLogger);
        assertThat(apiLogger).isNotSameAs(elasticLogger);
        assertThat(apiLogger).isNotSameAs(redisLogger);
        assertThat(apiLogger).isNotSameAs(schedulerLogger);
        assertThat(apiLogger).isNotSameAs(distributedLockLogger);
        assertThat(apiLogger).isNotSameAs(commonLogger);
    }
}
