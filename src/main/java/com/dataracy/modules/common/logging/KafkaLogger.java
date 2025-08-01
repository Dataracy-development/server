package com.dataracy.modules.common.logging;

import com.dataracy.modules.common.logging.support.BaseLogger;

public class KafkaLogger extends BaseLogger {

    /**
     * 지정된 Kafka 토픽에 메시지가 전송되었음을 정보 로그로 기록합니다.
     *
     * @param topic   메시지가 전송된 Kafka 토픽 이름
     * @param message 전송된 메시지 내용
     */
    public void logProduce(String topic, String message) {
        debug("[Kafka 전송] topic={} message={}", topic, message);
    }

    /**
     * 지정된 Kafka 토픽에서 수신된 메시지를 정보 로그로 기록합니다.
     *
     * @param topic   메시지를 수신한 Kafka 토픽 이름
     * @param message 수신된 메시지 내용
     */
    public void logConsume(String topic, String message) {
        debug("[Kafka 수신] topic={} message={}", topic, message);
    }

    /**
     * Kafka 작업 중 발생한 예외를 토픽과 메시지 정보와 함께 에러 로그로 기록합니다.
     *
     * @param topic   예외가 발생한 Kafka 토픽 이름
     * @param message 관련 Kafka 메시지 내용
     * @param e       발생한 예외 객체
     */
    public void logError(String topic, String message, Throwable e) {
        error(e, "[Kafka 예외] topic={} message={}", topic, message);
    }
}
