package com.dataracy.modules.common.logging.support;

import com.dataracy.modules.common.logging.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("LoggerFactory 테스트")
class LoggerFactoryTest {

    @Nested
    @DisplayName("api 메서드 테스트")
    class ApiTest {

        @Test
        @DisplayName("ApiLogger 싱글톤 인스턴스 반환")
        void api_ApiLogger싱글톤인스턴스반환() {
            // when
            ApiLogger logger1 = LoggerFactory.api();
            ApiLogger logger2 = LoggerFactory.api();

            // then
            assertAll(
                    () -> assertThat(logger1).isNotNull(),
                    () -> assertThat(logger2).isNotNull(),
                    () -> assertThat(logger1).isSameAs(logger2)
            );
        }
    }

    @Nested
    @DisplayName("service 메서드 테스트")
    class ServiceTest {

        @Test
        @DisplayName("ServiceLogger 싱글톤 인스턴스 반환")
        void service_ServiceLogger싱글톤인스턴스반환() {
            // when
            ServiceLogger logger1 = LoggerFactory.service();
            ServiceLogger logger2 = LoggerFactory.service();

            // then
            assertAll(
                    () -> assertThat(logger1).isNotNull(),
                    () -> assertThat(logger2).isNotNull(),
                    () -> assertThat(logger1).isSameAs(logger2)
            );
        }
    }

    @Nested
    @DisplayName("domain 메서드 테스트")
    class DomainTest {

        @Test
        @DisplayName("DomainLogger 싱글톤 인스턴스 반환")
        void domain_DomainLogger싱글톤인스턴스반환() {
            // when
            DomainLogger logger1 = LoggerFactory.domain();
            DomainLogger logger2 = LoggerFactory.domain();

            // then
            assertAll(
                    () -> assertThat(logger1).isNotNull(),
                    () -> assertThat(logger2).isNotNull(),
                    () -> assertThat(logger1).isSameAs(logger2)
            );
        }
    }

    @Nested
    @DisplayName("persistence 메서드 테스트")
    class PersistenceTest {

        @Test
        @DisplayName("PersistenceLogger 싱글톤 인스턴스 반환")
        void persistence_PersistenceLogger싱글톤인스턴스반환() {
            // when
            PersistenceLogger logger1 = LoggerFactory.db();
            PersistenceLogger logger2 = LoggerFactory.db();

            // then
            assertAll(
                    () -> assertThat(logger1).isNotNull(),
                    () -> assertThat(logger2).isNotNull(),
                    () -> assertThat(logger1).isSameAs(logger2)
            );
        }
    }

    @Nested
    @DisplayName("queryDsl 메서드 테스트")
    class QueryDslTest {

        @Test
        @DisplayName("QueryDslLogger 싱글톤 인스턴스 반환")
        void queryDsl_QueryDslLogger싱글톤인스턴스반환() {
            // when
            QueryDslLogger logger1 = LoggerFactory.query();
            QueryDslLogger logger2 = LoggerFactory.query();

            // then
            assertAll(
                    () -> assertThat(logger1).isNotNull(),
                    () -> assertThat(logger2).isNotNull(),
                    () -> assertThat(logger1).isSameAs(logger2)
            );
        }
    }

    @Nested
    @DisplayName("kafka 메서드 테스트")
    class KafkaTest {

        @Test
        @DisplayName("KafkaLogger 싱글톤 인스턴스 반환")
        void kafka_KafkaLogger싱글톤인스턴스반환() {
            // when
            KafkaLogger logger1 = LoggerFactory.kafka();
            KafkaLogger logger2 = LoggerFactory.kafka();

            // then
            assertAll(
                    () -> assertThat(logger1).isNotNull(),
                    () -> assertThat(logger2).isNotNull(),
                    () -> assertThat(logger1).isSameAs(logger2)
            );
        }
    }

    @Nested
    @DisplayName("elastic 메서드 테스트")
    class ElasticTest {

        @Test
        @DisplayName("ElasticLogger 싱글톤 인스턴스 반환")
        void elastic_ElasticLogger싱글톤인스턴스반환() {
            // when
            ElasticLogger logger1 = LoggerFactory.elastic();
            ElasticLogger logger2 = LoggerFactory.elastic();

            // then
            assertAll(
                    () -> assertThat(logger1).isNotNull(),
                    () -> assertThat(logger2).isNotNull(),
                    () -> assertThat(logger1).isSameAs(logger2)
            );
        }
    }

    @Nested
    @DisplayName("redis 메서드 테스트")
    class RedisTest {

        @Test
        @DisplayName("RedisLogger 싱글톤 인스턴스 반환")
        void redis_RedisLogger싱글톤인스턴스반환() {
            // when
            RedisLogger logger1 = LoggerFactory.redis();
            RedisLogger logger2 = LoggerFactory.redis();

            // then
            assertAll(
                    () -> assertThat(logger1).isNotNull(),
                    () -> assertThat(logger2).isNotNull(),
                    () -> assertThat(logger1).isSameAs(logger2)
            );
        }
    }

    @Nested
    @DisplayName("scheduler 메서드 테스트")
    class SchedulerTest {

        @Test
        @DisplayName("SchedulerLogger 싱글톤 인스턴스 반환")
        void scheduler_SchedulerLogger싱글톤인스턴스반환() {
            // when
            SchedulerLogger logger1 = LoggerFactory.scheduler();
            SchedulerLogger logger2 = LoggerFactory.scheduler();

            // then
            assertAll(
                    () -> assertThat(logger1).isNotNull(),
                    () -> assertThat(logger2).isNotNull(),
                    () -> assertThat(logger1).isSameAs(logger2)
            );
        }
    }

    @Nested
    @DisplayName("distributedLock 메서드 테스트")
    class DistributedLockTest {

        @Test
        @DisplayName("DistributedLockLogger 싱글톤 인스턴스 반환")
        void distributedLock_DistributedLockLogger싱글톤인스턴스반환() {
            // when
            DistributedLockLogger logger1 = LoggerFactory.lock();
            DistributedLockLogger logger2 = LoggerFactory.lock();

            // then
            assertAll(
                    () -> assertThat(logger1).isNotNull(),
                    () -> assertThat(logger2).isNotNull(),
                    () -> assertThat(logger1).isSameAs(logger2)
            );
        }
    }

    @Nested
    @DisplayName("common 메서드 테스트")
    class CommonTest {

        @Test
        @DisplayName("CommonLogger 싱글톤 인스턴스 반환")
        void common_CommonLogger싱글톤인스턴스반환() {
            // when
            CommonLogger logger1 = LoggerFactory.common();
            CommonLogger logger2 = LoggerFactory.common();

            // then
            assertAll(
                    () -> assertThat(logger1).isNotNull(),
                    () -> assertThat(logger2).isNotNull(),
                    () -> assertThat(logger1).isSameAs(logger2)
            );
        }
    }

    @Nested
    @DisplayName("모든 로거 타입 테스트")
    class AllLoggersTest {

        @Test
        @DisplayName("모든 로거가 정상적으로 생성되는지 확인")
        void 모든로거_정상생성확인() {
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
                    () -> assertThat(commonLogger).isNotNull()
            );
        }
    }
}
