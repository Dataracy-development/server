package com.dataracy.modules.dataset.adapter.jpa.repository;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DataMetadataJpaRepository extends JpaRepository<DataMetadataEntity, Long> {
    /**
 * 주어진 dataId에 해당하는 DataMetadataEntity를 조회합니다.
 *
 * @param dataId 조회할 데이터의 식별자
 * @return 해당 dataId에 매핑된 DataMetadataEntity가 존재하면 Optional로 반환하며, 없으면 빈 Optional을 반환합니다.
 */
Optional<DataMetadataEntity> findByDataId(Long dataId);
}
