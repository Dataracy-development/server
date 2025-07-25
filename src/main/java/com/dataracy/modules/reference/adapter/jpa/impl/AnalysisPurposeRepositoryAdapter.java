package com.dataracy.modules.reference.adapter.jpa.impl;

import com.dataracy.modules.reference.adapter.jpa.entity.AnalysisPurposeEntity;
import com.dataracy.modules.reference.adapter.jpa.mapper.AnalysisPurposeEntityMapper;
import com.dataracy.modules.reference.adapter.jpa.repository.AnalysisPurposeJpaRepository;
import com.dataracy.modules.reference.application.port.out.AnalysisPurposeRepositoryPort;
import com.dataracy.modules.reference.domain.model.AnalysisPurpose;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AnalysisPurposeRepositoryAdapter implements AnalysisPurposeRepositoryPort {
    private final AnalysisPurposeJpaRepository analysisPurposeJpaRepository;

    /**
     * 모든 분석 목적(AnalysisPurpose) 도메인 객체 목록을 조회한다.
     *
     * @return 데이터베이스에 저장된 모든 분석 목적의 도메인 객체 리스트
     */
    @Override
    public List<AnalysisPurpose> findAllAnalysisPurposes() {
        List<AnalysisPurposeEntity> analysisPurposeEntities = analysisPurposeJpaRepository.findAll();
        return analysisPurposeEntities.stream()
                .map(AnalysisPurposeEntityMapper::toDomain)
                .toList();
    }

    /**
     * 주어진 ID에 해당하는 분석 목적 도메인 객체를 Optional로 반환한다.
     *
     * @param analysisPurposeId 조회할 분석 목적의 ID. null인 경우 Optional.empty()를 반환한다.
     * @return 해당 ID의 분석 목적이 존재하면 Optional로 감싸서 반환하며, 존재하지 않으면 Optional.empty()를 반환한다.
     */
    @Override
    public Optional<AnalysisPurpose> findAnalysisPurposeById(Long analysisPurposeId) {
        if (analysisPurposeId == null) {
            return Optional.empty();
        }
        return analysisPurposeJpaRepository.findById(analysisPurposeId)
                .map(AnalysisPurposeEntityMapper::toDomain);
    }

    /**
     * 주어진 ID의 분석 목적이 데이터베이스에 존재하는지 확인합니다.
     *
     * @param analysisPurposeId 존재 여부를 확인할 분석 목적의 ID
     * @return 해당 ID가 null이 아니고, 데이터베이스에 존재하면 true, 그렇지 않으면 false
     */
    @Override
    public boolean existsAnalysisPurposeById(Long analysisPurposeId) {
        if (analysisPurposeId == null) {
            return false;
        }
        return analysisPurposeJpaRepository.existsById(analysisPurposeId);
    }

    /**
     * 주어진 ID에 해당하는 분석 목적의 라벨을 Optional로 반환합니다.
     *
     * @param analysisPurposeId 라벨을 조회할 분석 목적의 ID
     * @return 존재할 경우 라벨을 포함한 Optional, 존재하지 않거나 ID가 null인 경우 빈 Optional
     */
    @Override
    public Optional<String> getLabelById(Long analysisPurposeId) {
        if (analysisPurposeId == null) {
            return Optional.empty();
        }
        return analysisPurposeJpaRepository.findLabelById(analysisPurposeId);
    }

    /**
     * 주어진 분석 목적 ID 목록에 해당하는 엔티티들의 ID와 라벨을 매핑한 Map을 반환합니다.
     *
     * @param analysisPurposeIds 조회할 분석 목적 ID 목록
     * @return 각 ID에 해당하는 라벨 문자열의 Map
     */
    @Override
    public Map<Long, String> getLabelsByIds(List<Long> analysisPurposeIds) {
        return analysisPurposeJpaRepository.findAllById(analysisPurposeIds)
                .stream()
                .collect(Collectors.toMap(AnalysisPurposeEntity::getId, AnalysisPurposeEntity::getLabel));
    }
}
