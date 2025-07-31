package com.dataracy.modules.common.logging.support;

import com.dataracy.modules.common.logging.*;

public class LoggerFactory {

    public static ApiLogger api() {
        return new ApiLogger();
    }

    public static ServiceLogger service() {
        return new ServiceLogger();
    }

    public static KafkaLogger kafka() {
        return new KafkaLogger();
    }

    public static ElasticLogger elastic() {
        return new ElasticLogger();
    }

    public static PersistenceLogger db() {
        return new PersistenceLogger();
    }

    public static SchedulerLogger scheduler() {
        return new SchedulerLogger();
    }

    public static FilterLogger filter() {
        return new FilterLogger();
    }

    public static DomainLogger domain() {
        return new DomainLogger();
    }
}

