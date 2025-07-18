package com.dataracy.modules.data.application.port.out;

import com.dataracy.modules.data.domain.model.Data;

import java.util.Optional;

/**
 * 데이터셋 db 포트
 */
public interface DataRepositoryPort {

    /**
 * 데이터 객체를 데이터베이스에 저장하고 저장된 인스턴스를 반환합니다.
 *
 * @param data 저장할 데이터 객체
 * @return 저장된 데이터 객체
 */
    Data saveData(Data data);

    /**
 * 주어진 식별자에 해당하는 Data 엔티티를 조회합니다.
 *
 * @param dataId 조회할 Data 엔티티의 고유 식별자
 * @return Data 엔티티가 존재하면 해당 객체를, 없으면 빈 Optional을 반환합니다.
 */
    Optional<Data> findDataById(Long dataId);

    /**
 * 지정된 데이터 ID에 해당하는 데이터 엔티티의 데이터 파일 URL을 업데이트합니다.
 *
 * @param dataId       데이터 엔티티의 고유 식별자
 * @param dataFileUrl  새로 설정할 데이터 파일의 URL
 */
    void updateDataFile(Long dataId, String dataFileUrl);

    /**
 * 지정된 데이터 엔티티의 썸네일 파일 URL을 업데이트합니다.
 *
 * @param dataId        썸네일 파일 URL을 변경할 데이터의 식별자
 * @param thumbFileUrl  새로 설정할 썸네일 파일의 URL
 */
    void updateThumbnailFile(Long dataId, String thumbFileUrl);

    boolean existsDataById(Long dataId);
}
