package com.dataracy.modules.common.logging.support;

import com.dataracy.modules.common.logging.*;

public class LoggerFactory {

    private static final ApiLogger API_LOGGER = new ApiLogger();
    private static final ServiceLogger SERVICE_LOGGER = new ServiceLogger();
    private static final DomainLogger DOMAIN_LOGGER = new DomainLogger();
    private static final PersistenceLogger PERSISTENCE_LOGGER = new PersistenceLogger();
    private static final QueryDslLogger QUERY_DSL_LOGGER = new QueryDslLogger();
    private static final KafkaLogger KAFKA_LOGGER = new KafkaLogger();
    private static final ElasticLogger ELASTIC_LOGGER = new ElasticLogger();
    private static final RedisLogger REDIS_LOGGER = new RedisLogger();
    private static final SchedulerLogger SCHEDULER_LOGGER = new SchedulerLogger();
    private static final DistributedLockLogger DISTRIBUTED_LOCK_LOGGER = new DistributedLockLogger();
    private static final CommonLogger COMMON_LOGGER = new CommonLogger();

    /**
     * API 로깅을 위한 싱글톤 ApiLogger 인스턴스를 반환합니다.
     *
     * @return API 로그 기록에 사용되는 ApiLogger 인스턴스
     */
    public static ApiLogger api() {
        return API_LOGGER;
    }

    /**
     * ServiceLogger의 싱글톤 인스턴스를 반환합니다.
     *
     * @return ServiceLogger의 전역 인스턴스
     */
    public static ServiceLogger service() {
        return SERVICE_LOGGER;
    }

    /**
     * DomainLogger의 싱글톤 인스턴스를 반환합니다.
     *
     * 이 메서드는 도메인 관련 로깅을 위한 DomainLogger의 전역 인스턴스를 제공합니다.
     *
     * @return DomainLogger의 싱글톤 인스턴스
     */
    public static DomainLogger domain() {
        return DOMAIN_LOGGER;
    }

    /**
     * PersistenceLogger의 싱글톤 인스턴스를 반환합니다.
     *
     * @return PersistenceLogger 인스턴스
     */
    public static PersistenceLogger db() {
        return PERSISTENCE_LOGGER;
    }

    /**
     * QueryDSL 관련 로깅을 위한 싱글톤 QueryDslLogger 인스턴스를 반환합니다.
     *
     * @return QueryDSL 로깅에 사용되는 QueryDslLogger 인스턴스
     */
    public static QueryDslLogger query() {
        return QUERY_DSL_LOGGER;
    }

    /**
     * Kafka 관련 로그를 기록하는 싱글톤 KafkaLogger 인스턴스를 반환합니다.
     *
     * @return KafkaLogger 싱글톤 인스턴스
     */
    public static KafkaLogger kafka() {
        return KAFKA_LOGGER;
    }

    /**
     * ElasticLogger의 싱글톤 인스턴스를 반환합니다.
     *
     * @return ElasticLogger 인스턴스
     */
    public static ElasticLogger elastic() {
        return ELASTIC_LOGGER;
    }

    /**
     * 스케줄러 로거의 싱글톤 인스턴스를 반환합니다.
     *
     * @return SchedulerLogger의 싱글톤 인스턴스
     */
    public static SchedulerLogger scheduler() {
        return SCHEDULER_LOGGER;
    }

    /**
     * Redis 관련 로깅을 위한 싱글톤 RedisLogger 인스턴스를 반환합니다.
     *
     * @return RedisLogger 싱글톤 인스턴스
     */
    public static RedisLogger redis() {
        return REDIS_LOGGER;
    }

    public static DistributedLockLogger lock() {
        return DISTRIBUTED_LOCK_LOGGER;
    }

    /**
     * 애플리케이션 전체에서 공통적으로 사용할 수 있는 CommonLogger의 싱글톤 인스턴스를 반환합니다.
     *
     * @return CommonLogger의 싱글톤 인스턴스
     */
    public static CommonLogger common() {
        return COMMON_LOGGER;
    }
}
