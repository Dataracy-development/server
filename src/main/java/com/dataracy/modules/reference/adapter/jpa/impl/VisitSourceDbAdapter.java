package com.dataracy.modules.reference.adapter.jpa.impl;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.reference.adapter.jpa.entity.VisitSourceEntity;
import com.dataracy.modules.reference.adapter.jpa.mapper.VisitSourceEntityMapper;
import com.dataracy.modules.reference.adapter.jpa.repository.VisitSourceJpaRepository;
import com.dataracy.modules.reference.application.port.out.VisitSourcePort;
import com.dataracy.modules.reference.domain.model.VisitSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class VisitSourceDbAdapter implements VisitSourcePort {
    private final VisitSourceJpaRepository visitSourceJpaRepository;

    /**
     * visitSource 엔티티의 모든 데이터셋을 조회한다.
     * @return visitSource 데이터셋
     */
    @Override
    public List<VisitSource> findAllVisitSources() {
        Instant startTime = LoggerFactory.db().logQueryStart("VisitSourceEntity", "[findAll] 방문 경로 목록 조회 시작");
        List<VisitSourceEntity> visitSourceEntities = visitSourceJpaRepository.findAll();
        List<VisitSource> visitSources = visitSourceEntities.stream()
                .map(VisitSourceEntityMapper::toDomain)
                .toList();
        LoggerFactory.db().logQueryEnd("VisitSourceEntity", "[findAll] 방문 경로 목록 조회 종료", startTime);
        return visitSources;
    }

    /****
     * 주어진 ID에 해당하는 방문 경로 도메인 객체를 Optional로 반환한다.
     *
     * @param visitSourceId 조회할 방문 경로의 ID. null인 경우 빈 Optional을 반환한다.
     * @return 방문 경로가 존재하면 해당 도메인 객체를 포함한 Optional, 존재하지 않거나 ID가 null이면 빈 Optional
     */
    @Override
    public Optional<VisitSource> findVisitSourceById(Long visitSourceId) {
        Instant startTime = LoggerFactory.db().logQueryStart("VisitSourceEntity", "[findById] 방문 경로 목록 조회 시작 visitSourceId=" + visitSourceId);
        if (visitSourceId == null) {
            return Optional.empty();
        }
        Optional<VisitSource> visitSource = visitSourceJpaRepository.findById(visitSourceId)
                .map(VisitSourceEntityMapper::toDomain);
        LoggerFactory.db().logQueryEnd("VisitSourceEntity", "[findById] 방문 경로 목록 조회 종료 visitSourceId=" + visitSourceId, startTime);
        return visitSource;
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
        boolean isExists = visitSourceJpaRepository.existsById(visitSourceId);
        LoggerFactory.db().logExist("VisitSourceEntity", "[existsById] 방문 경로 존재 유무 확인 visitSourceId=" + visitSourceId + ", isExists=" + isExists);
        return isExists;
    }

    /**
     * 주어진 ID에 해당하는 방문 소스의 라벨을 Optional로 반환합니다.
     *
     * @param visitSourceId 조회할 방문 소스의 ID
     * @return 방문 소스가 존재하면 라벨을, 없으면 빈 Optional을 반환합니다.
     */
    @Override
    public Optional<String> getLabelById(Long visitSourceId) {
        Instant startTime = LoggerFactory.db().logQueryStart("VisitSourceEntity", "[findLabelById] 방문 경로 라벨 조회 시작 visitSourceId=" + visitSourceId);
        if (visitSourceId == null) {
            return Optional.empty();
        }
        Optional<String> label = visitSourceJpaRepository.findLabelById(visitSourceId);
        LoggerFactory.db().logQueryEnd("VisitSourceEntity", "[findLabelById] 방문 경로 라벨 조회 종료 visitSourceId=" + visitSourceId + ", label=" + label, startTime);
        return label;
    }

    /**
     * 주어진 방문 소스 ID 목록에 해당하는 ID와 라벨의 매핑을 반환합니다.
     *
     * @param visitSourceIds 조회할 방문 소스 ID 목록
     * @return 각 ID에 해당하는 라벨의 Map
     */
    @Override
    public Map<Long, String> getLabelsByIds(List<Long> visitSourceIds) {
        Instant startTime = LoggerFactory.db().logQueryStart("VisitSourceEntity", "[findAllById] 방문 경로 라벨 목록 조회 시작");
        Map<Long, String> labels = visitSourceJpaRepository.findAllById(visitSourceIds)
                .stream()
                .collect(Collectors.toMap(VisitSourceEntity::getId, VisitSourceEntity::getLabel));
        LoggerFactory.db().logQueryEnd("VisitSourceEntity", "[findAllById] 방문 경로 라벨 목록 조회 종료", startTime);
        return labels;
    }
}
