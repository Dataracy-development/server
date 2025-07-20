package com.dataracy.modules.reference.adapter.jpa.impl;

import com.dataracy.modules.reference.adapter.jpa.entity.VisitSourceEntity;
import com.dataracy.modules.reference.adapter.jpa.mapper.VisitSourceEntityMapper;
import com.dataracy.modules.reference.adapter.jpa.repository.VisitSourceJpaRepository;
import com.dataracy.modules.reference.application.port.out.VisitSourceRepositoryPort;
import com.dataracy.modules.reference.domain.model.VisitSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class VisitSourceRepositoryAdapter implements VisitSourceRepositoryPort {
    private final VisitSourceJpaRepository visitSourceJpaRepository;

    /**
     * visitSource 엔티티의 모든 데이터셋을 조회한다.
     * @return visitSource 데이터셋
     */
    @Override
    public List<VisitSource> findAllVisitSources() {
        List<VisitSourceEntity> visitSourceEntities = visitSourceJpaRepository.findAll();
        return visitSourceEntities.stream()
                .map(VisitSourceEntityMapper::toDomain)
                .toList();
    }

    /****
     * 주어진 ID에 해당하는 방문 경로 도메인 객체를 Optional로 반환한다.
     *
     * @param visitSourceId 조회할 방문 경로의 ID. null인 경우 빈 Optional을 반환한다.
     * @return 방문 경로가 존재하면 해당 도메인 객체를 포함한 Optional, 존재하지 않거나 ID가 null이면 빈 Optional
     */
    @Override
    public Optional<VisitSource> findVisitSourceById(Long visitSourceId) {
        if (visitSourceId == null) {
            return Optional.empty();
        }
        return visitSourceJpaRepository.findById(visitSourceId)
                .map(VisitSourceEntityMapper::toDomain);
    }

    /**
     * 주어진 ID에 해당하는 방문 소스가 데이터베이스에 존재하는지 반환합니다.
     *
     * @param visitSourceId 존재 여부를 확인할 방문 소스의 ID
     * @return 방문 소스가 존재하면 true, ID가 null이거나 존재하지 않으면 false
     */
    @Override
    public boolean existsVisitSourceById(Long visitSourceId) {
        if (visitSourceId == null) {
            return false;
        }
        return visitSourceJpaRepository.existsById(visitSourceId);
    }

    /**
     * 주어진 ID에 해당하는 방문 소스의 라벨을 Optional로 반환합니다.
     *
     * @param visitSourceId 조회할 방문 소스의 ID
     * @return 방문 소스가 존재하면 라벨을, 없으면 빈 Optional을 반환합니다.
     */
    @Override
    public Optional<String> getLabelById(Long visitSourceId) {
        if (visitSourceId == null) {
            return Optional.empty();
        }
        return visitSourceJpaRepository.findLabelById(visitSourceId);
    }

    /**
     * 주어진 방문 소스 ID 목록에 해당하는 ID와 라벨의 매핑을 반환합니다.
     *
     * @param visitSourceIds 조회할 방문 소스 ID 목록
     * @return 각 ID에 해당하는 라벨의 Map
     */
    @Override
    public Map<Long, String> getLabelsByIds(List<Long> visitSourceIds) {
        return visitSourceJpaRepository.findAllById(visitSourceIds)
                .stream()
                .collect(Collectors.toMap(VisitSourceEntity::getId, VisitSourceEntity::getLabel));
    }
}
