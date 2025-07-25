package com.dataracy.modules.dataset.application.port.out;

/**
 * 데이터셋 카프카 포트
 */
public interface DataKafkaProducerPort {
    /****
 * 데이터 업로드 이벤트를 전송합니다.
 *
 * @param dataId 업로드된 데이터의 고유 식별자
 * @param fileUrl 업로드된 파일의 URL
 * @param originalFilename 업로드된 파일의 원본 파일명
 */
void sendUploadEvent(Long dataId, String fileUrl, String originalFilename);
}
