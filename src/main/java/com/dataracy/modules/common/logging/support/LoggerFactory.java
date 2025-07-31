package com.dataracy.modules.common.logging.support;

import com.dataracy.modules.common.logging.*;

public class LoggerFactory {

    private static final ApiLogger API_LOGGER = new ApiLogger();
    private static final ServiceLogger SERVICE_LOGGER = new ServiceLogger();
    private static final KafkaLogger KAFKA_LOGGER = new KafkaLogger();
    private static final ElasticLogger ELASTIC_LOGGER = new ElasticLogger();
    private static final PersistenceLogger PERSISTENCE_LOGGER = new PersistenceLogger();
    private static final SchedulerLogger SCHEDULER_LOGGER = new SchedulerLogger();
    private static final FilterLogger FILTER_LOGGER = new FilterLogger();
    private static final DomainLogger DOMAIN_LOGGER = new DomainLogger();

    public static ApiLogger api() {
        return API_LOGGER;
    }

    public static ServiceLogger service() {
        return SERVICE_LOGGER;
    }

    public static KafkaLogger kafka() {
        return KAFKA_LOGGER;
    }

    public static ElasticLogger elastic() {
        return ELASTIC_LOGGER;
    }

    public static PersistenceLogger db() {
        return PERSISTENCE_LOGGER;
    }

    public static SchedulerLogger scheduler() {
        return SCHEDULER_LOGGER;
    }

    public static FilterLogger filter() {
        return FILTER_LOGGER;
    }

    public static DomainLogger domain() {
        return DOMAIN_LOGGER;
    }
}
