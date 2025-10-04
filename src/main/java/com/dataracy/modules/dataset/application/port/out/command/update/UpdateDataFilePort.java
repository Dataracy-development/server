package com.dataracy.modules.dataset.application.port.out.command.update;

public interface UpdateDataFilePort {
  /**
   * 지정된 데이터 엔티티의 데이터 파일 참조(URL 및 파일 크기)를 갱신합니다.
   *
   * @param dataId 데이터 엔티티의 고유 식별자
   * @param dataFileUrl 새로 설정할 데이터 파일의 URL
   * @param dataFileSize 새로 설정할 데이터 파일의 크기(바이트). 크기를 모르는 경우 null 허용
   */
  void updateDataFile(Long dataId, String dataFileUrl, Long dataFileSize);
}
