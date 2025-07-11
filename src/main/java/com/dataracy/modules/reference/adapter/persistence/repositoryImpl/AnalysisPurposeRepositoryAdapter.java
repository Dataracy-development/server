package com.dataracy.modules.reference.adapter.persistence.repositoryImpl;

import com.dataracy.modules.reference.adapter.persistence.entity.AnalysisPurposeEntity;
import com.dataracy.modules.reference.adapter.persistence.mapper.AnalysisPurposeEntityMapper;
import com.dataracy.modules.reference.adapter.persistence.repository.AnalysisPurposeJpaRepository;
import com.dataracy.modules.reference.application.port.out.AnalysisPurposeRepositoryPort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.AnalysisPurpose;
import com.dataracy.modules.reference.domain.status.ReferenceErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    public List<AnalysisPurpose> allAnalysisPurposes() {
        List<AnalysisPurposeEntity> analysisPurposeEntities = analysisPurposeJpaRepository.findAll();
        return analysisPurposeEntities.stream()
                .map(AnalysisPurposeEntityMapper::toDomain)
                .toList();
    }

    /**
     * 주어진 ID에 해당하는 분석 목적을 조회한다.
     *
     * @param analysisPurposeId 조회할 분석 목적의 ID
     * @return 해당 ID의 분석 목적 도메인 객체. ID가 null이면 null을 반환한다.
     * @throws ReferenceException 해당 ID의 분석 목적이 존재하지 않을 경우 발생한다.
     */
    @Override
    public AnalysisPurpose findAnalysisPurposeById(Long analysisPurposeId) {
        if (analysisPurposeId == null) {
            return null;
        }

        AnalysisPurposeEntity analysisPurposeEntity = analysisPurposeJpaRepository.findById(analysisPurposeId)
                .orElseThrow(() -> new ReferenceException(ReferenceErrorStatus.NOT_FOUND_ANALYSIS_PURPOSE));
        return AnalysisPurposeEntityMapper.toDomain(analysisPurposeEntity);
    }
}
