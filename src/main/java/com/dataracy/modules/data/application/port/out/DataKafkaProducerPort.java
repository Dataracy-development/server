package com.dataracy.modules.data.application.port.out;

/**
 * 데이터셋 카프카 포트
 */
public interface DataKafkaProducerPort {
    void sendUploadEvent(Long dataId, String fileUrl, String originalFilename);
}
