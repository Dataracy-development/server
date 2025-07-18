package com.dataracy.modules.reference.adapter.persistence.impl;

import com.dataracy.modules.reference.adapter.persistence.entity.AnalysisPurposeEntity;
import com.dataracy.modules.reference.adapter.persistence.mapper.AnalysisPurposeEntityMapper;
import com.dataracy.modules.reference.adapter.persistence.repository.AnalysisPurposeJpaRepository;
import com.dataracy.modules.reference.application.port.out.AnalysisPurposeRepositoryPort;
import com.dataracy.modules.reference.domain.model.AnalysisPurpose;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
     * @param analysisPurposeId 조회할 분석 목적의 ID
     * @return 해당 ID의 분석 목적이 존재하면 Optional로 감싸서 반환하며, 존재하지 않으면 Optional.empty()를 반환한다.
     */
    @Override
    public Optional<AnalysisPurpose> findAnalysisPurposeById(Long analysisPurposeId) {
        return analysisPurposeJpaRepository.findById(analysisPurposeId)
                .map(AnalysisPurposeEntityMapper::toDomain);
    }

    /**
     * 주어진 ID를 가진 분석 목적(AnalysisPurpose)이 데이터베이스에 존재하는지 확인합니다.
     *
     * @param analysisPurposeId 존재 여부를 확인할 분석 목적의 ID
     * @return 해당 ID의 분석 목적이 존재하면 true, 그렇지 않으면 false
     */
    @Override
    public boolean existsAnalysisPurposeById(Long analysisPurposeId) {
        return analysisPurposeJpaRepository.existsById(analysisPurposeId);
    }

    @Override
    public Optional<String> getLabelById(Long analysisPurposeId) {
        return analysisPurposeJpaRepository.findById(analysisPurposeId)
                .map(AnalysisPurposeEntity::getLabel);
    }
}
