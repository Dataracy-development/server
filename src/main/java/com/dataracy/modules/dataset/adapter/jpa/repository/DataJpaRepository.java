package com.dataracy.modules.dataset.adapter.jpa.repository;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DataJpaRepository extends JpaRepository<DataEntity, Long> {
    /**
     * 삭제된 데이터를 포함하여 지정된 dataId에 해당하는 DataEntity를 조회합니다.
     *
     * 이 메서드는 네이티브 쿼리를 사용하여 soft delete 등으로 필터링된 데이터까지 모두 반환합니다.
     *
     * @param dataId 조회할 데이터의 ID
     * @return 해당 dataId의 DataEntity가 존재하면 Optional로 반환하며, 없으면 Optional.empty()를 반환합니다.
     */
    @Query(value = "SELECT * FROM data WHERE data_id = :dataId", nativeQuery = true) // @Where 무시됨
    Optional<DataEntity> findIncludingDeletedData(@Param("dataId") Long dataId);
}
