package com.dataracy.modules.dataset.application.port.out.command.update;

public interface UpdateDataFilePort {
    /**
     * 데이터 엔티티의 데이터 파일 URL을 새 값으로 변경합니다.
     *
     * @param dataId 데이터 엔티티의 고유 식별자
     * @param dataFileUrl 새로 지정할 데이터 파일의 URL
     */
    void updateDataFile(Long dataId, String dataFileUrl);
}
