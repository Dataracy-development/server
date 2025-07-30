package com.dataracy.modules.dataset.application.port.out;

import com.dataracy.modules.dataset.application.dto.request.DataModifyRequest;
import com.dataracy.modules.dataset.domain.model.Data;

import java.util.Optional;

/**
 * 데이터셋 db 포트
 */
public interface DataRepositoryPort {

    /**
 * 데이터 객체를 데이터베이스에 저장한 후, 저장된 인스턴스를 반환합니다.
 *
 * @param data 저장할 데이터 엔티티
 * @return 데이터베이스에 저장된 데이터 엔티티
 */
    Data saveData(Data data);

    /**
 * 주어진 식별자에 해당하는 Data 엔티티를 조회합니다.
 *
 * @param dataId 조회할 Data 엔티티의 고유 식별자
 * @return Data 엔티티가 존재하면 해당 객체를 포함한 Optional, 존재하지 않으면 빈 Optional
 */
    Optional<Data> findDataById(Long dataId);

    /**
 * 데이터 엔티티의 데이터 파일 URL을 새 값으로 변경합니다.
 *
 * @param dataId 데이터 엔티티의 고유 식별자
 * @param dataFileUrl 새로 지정할 데이터 파일의 URL
 */
    void updateDataFile(Long dataId, String dataFileUrl);

    /**
 * 지정된 데이터 엔티티의 썸네일 파일 URL을 새 값으로 변경합니다.
 *
 * @param dataId 썸네일 파일 URL을 업데이트할 데이터의 고유 식별자
 * @param thumbFileUrl 새로 저장할 썸네일 파일의 URL
 */
    void updateThumbnailFile(Long dataId, String thumbFileUrl);

    /**
 * 주어진 ID의 데이터 엔티티가 저장소에 존재하는지 확인합니다.
 *
 * @param dataId 존재 여부를 확인할 데이터의 고유 식별자
 * @return 데이터가 존재하면 true, 존재하지 않으면 false
 */
boolean existsDataById(Long dataId);

    /**
 * 지정된 데이터 ID에 연결된 사용자 ID를 반환합니다.
 *
 * @param dataId 사용자 ID를 조회할 데이터의 고유 식별자
 * @return 해당 데이터에 연결된 사용자 ID
 */
Long findUserIdByDataId(Long dataId);
    /**
 * 삭제된 데이터를 포함하여 지정된 데이터 ID에 연결된 사용자 ID를 반환합니다.
 *
 * @param dataId 사용자 ID를 조회할 데이터의 고유 식별자
 * @return 해당 데이터에 연결된 사용자 ID
 */
Long findUserIdIncludingDeleted(Long dataId);

    /**
 * 지정된 데이터 ID에 해당하는 데이터 엔터티를 주어진 수정 요청 정보로 변경합니다.
 *
 * @param dataId 수정할 데이터의 고유 식별자
 * @param requestDto 데이터 수정 요청 정보를 담은 객체
 */
void modify(Long dataId, DataModifyRequest requestDto);
    /**
 * 지정된 데이터 ID에 해당하는 데이터를 삭제합니다.
 *
 * @param dataId 삭제할 데이터의 고유 식별자
 */
void delete(Long dataId);
    /**
 * 삭제된 데이터 엔티티를 복구합니다.
 *
 * @param dataId 복구할 데이터의 고유 식별자
 */
void restore(Long dataId);

    Optional<String> downloadDatasetFile(Long dataId);
}
