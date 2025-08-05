package com.dataracy.modules.dataset.application.port.out.command.update;

public interface UpdateDataFilePort {
    /**
 * 지정된 데이터 엔티티의 데이터 파일 URL을 새로운 값으로 업데이트합니다.
 *
 * @param dataId 데이터 엔티티의 고유 식별자
 * @param dataFileUrl 새로 설정할 데이터 파일의 URL
 */
    void updateDataFile(Long dataId, String dataFileUrl);
}
