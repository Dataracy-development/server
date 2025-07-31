package com.dataracy.modules.common.logging;

public class KafkaLogger extends BaseLogger {

    public void logProduce(String topic, String message) {
        info("[Kafka 전송] topic={} message={}", topic, message);
    }

    public void logConsume(String topic, String message) {
        info("[Kafka 수신] topic={} message={}", topic, message);
    }

    public void logError(String topic, String message, Throwable e) {
        error(e, "[Kafka 예외] topic={} message={}", topic, message);
    }
}
