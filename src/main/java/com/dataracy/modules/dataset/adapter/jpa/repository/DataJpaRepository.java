package com.dataracy.modules.dataset.adapter.jpa.repository;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DataJpaRepository extends JpaRepository<DataEntity, Long> {
    /**
     * 삭제된 데이터를 포함하여 지정된 dataId에 해당하는 DataEntity를 반환합니다.
     * 네이티브 쿼리를 사용하여 soft delete 등 엔티티 레벨의 필터링을 무시하고 데이터를 조회합니다.
     *
     * @param dataId 조회할 데이터의 ID
     * @return 해당 dataId의 DataEntity가 존재하면 Optional로 반환하며, 없으면 Optional.empty()를 반환합니다.
     */
    @Query(value = "SELECT * FROM data WHERE data_id = :dataId", nativeQuery = true) // @Where 무시됨
    Optional<DataEntity> findIncludingDeletedData(@Param("dataId") Long dataId);

    /**
     * 지정된 데이터 ID에 해당하는 데이터 파일 URL을 반환합니다.
     * 데이터의 삭제 상태와 무관하게 `data` 테이블에서 직접 조회하며, 해당 ID의 URL이 없으면 빈 Optional을 반환합니다.
     *
     * @param dataId 데이터 파일 URL을 조회할 데이터의 ID
     * @return 데이터 파일 URL이 존재하면 해당 값을, 없으면 빈 Optional
     */
    @Query("SELECT d.dataFileUrl FROM DataEntity d WHERE d.id = :dataId") // @Where 무시됨
    Optional<String> findDataFileUrlById(@Param("dataId") Long dataId);
}
