package com.dataracy.modules.reference.adapter.persistence.impl;

import com.dataracy.modules.reference.adapter.persistence.entity.OccupationEntity;
import com.dataracy.modules.reference.adapter.persistence.mapper.OccupationEntityMapper;
import com.dataracy.modules.reference.adapter.persistence.repository.jpa.OccupationJpaRepository;
import com.dataracy.modules.reference.application.port.out.OccupationRepositoryPort;
import com.dataracy.modules.reference.domain.model.Occupation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OccupationRepositoryAdapter implements OccupationRepositoryPort {
    private final OccupationJpaRepository occupationJpaRepository;

    /**
     * occupation 엔티티의 모든 데이터셋을 조회한다.
     * @return occupation 데이터셋
     */
    @Override
    public List<Occupation> findAllOccupations() {
        List<OccupationEntity> occupationEntities = occupationJpaRepository.findAll();
        return occupationEntities.stream()
                .map(OccupationEntityMapper::toDomain)
                .toList();
    }

    /**
     * 주어진 ID에 해당하는 직업 정보를 조회하여 Optional로 반환한다.
     *
     * @param occupationId 조회할 직업의 ID
     * @return 직업이 존재하면 해당 직업의 도메인 객체를 담은 Optional, 존재하지 않으면 Optional.empty()
     */
    @Override
    public Optional<Occupation> findOccupationById(Long occupationId) {
        return occupationJpaRepository.findById(occupationId)
                .map(OccupationEntityMapper::toDomain);
    }

    /**
     * 주어진 직업 ID에 해당하는 직업의 존재 여부를 확인합니다.
     *
     * @param occupationId 존재 여부를 확인할 직업의 ID
     * @return 직업이 존재하면 true, 존재하지 않으면 false
     */
    @Override
    public boolean existsOccupationById(Long occupationId) {
        return occupationJpaRepository.existsById(occupationId);
    }

    /**
     * 주어진 직업 ID에 해당하는 직업의 라벨을 조회합니다.
     *
     * @param occupationId 조회할 직업의 ID
     * @return 직업 라벨이 존재하면 해당 값을 포함한 Optional, 없으면 Optional.empty()
     */
    @Override
    public Optional<String> getLabelById(Long occupationId) {
        return occupationJpaRepository.findById(occupationId)
                .map(OccupationEntity::getLabel);
    }
}
