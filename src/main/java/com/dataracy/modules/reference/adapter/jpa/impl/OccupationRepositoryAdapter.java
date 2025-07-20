package com.dataracy.modules.reference.adapter.jpa.impl;

import com.dataracy.modules.reference.adapter.jpa.entity.OccupationEntity;
import com.dataracy.modules.reference.adapter.jpa.mapper.OccupationEntityMapper;
import com.dataracy.modules.reference.adapter.jpa.repository.OccupationJpaRepository;
import com.dataracy.modules.reference.application.port.out.OccupationRepositoryPort;
import com.dataracy.modules.reference.domain.model.Occupation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * 주어진 ID로 직업 정보를 조회하여 도메인 객체로 반환한다.
     *
     * @param occupationId 조회할 직업의 ID. null인 경우 Optional.empty()를 반환한다.
     * @return 직업이 존재하면 도메인 객체를 담은 Optional, 존재하지 않거나 ID가 null이면 Optional.empty()
     */
    @Override
    public Optional<Occupation> findOccupationById(Long occupationId) {
        if (occupationId == null) {
            return Optional.empty();
        }
        return occupationJpaRepository.findById(occupationId)
                .map(OccupationEntityMapper::toDomain);
    }

    /**
     * 주어진 직업 ID에 해당하는 직업이 데이터베이스에 존재하는지 여부를 반환합니다.
     *
     * @param occupationId 확인할 직업의 ID
     * @return 직업이 존재하면 true, null이거나 존재하지 않으면 false
     */
    @Override
    public boolean existsOccupationById(Long occupationId) {
        if (occupationId == null) {
            return false;
        }
        return occupationJpaRepository.existsById(occupationId);
    }

    /**
     * 주어진 ID에 해당하는 직업의 라벨을 Optional로 반환합니다.
     *
     * @param occupationId 조회할 직업의 ID
     * @return 직업 라벨이 존재하면 Optional에 담아 반환하며, ID가 null이거나 라벨이 없으면 Optional.empty()를 반환합니다.
     */
    @Override
    public Optional<String> getLabelById(Long occupationId) {
        if (occupationId == null) {
            return Optional.empty();
        }
        return occupationJpaRepository.findLabelById(occupationId);
    }

    /**
     * 주어진 직업 ID 목록에 해당하는 직업의 ID와 라벨을 매핑한 Map을 반환합니다.
     *
     * @param occupationIds 조회할 직업 ID 목록
     * @return 각 직업 ID와 해당 라벨이 매핑된 Map
     */
    @Override
    public Map<Long, String> getLabelsByIds(List<Long> occupationIds) {
        return occupationJpaRepository.findAllById(occupationIds)
                .stream()
                .collect(Collectors.toMap(OccupationEntity::getId, OccupationEntity::getLabel));
    }
}
