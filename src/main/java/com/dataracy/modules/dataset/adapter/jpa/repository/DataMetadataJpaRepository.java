package com.dataracy.modules.dataset.adapter.jpa.repository;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DataMetadataJpaRepository extends JpaRepository<DataMetadataEntity, Long> {
    /**
     * 지정된 dataId에 해당하는 DataMetadataEntity를 Optional로 반환합니다.
     *
     * @param dataId 조회할 데이터의 고유 식별자
     * @return dataId에 해당하는 DataMetadataEntity가 존재하면 Optional로 감싸서 반환하며, 없으면 빈 Optional을 반환합니다.
     */
    Optional<DataMetadataEntity> findByDataId(Long dataId);
}
