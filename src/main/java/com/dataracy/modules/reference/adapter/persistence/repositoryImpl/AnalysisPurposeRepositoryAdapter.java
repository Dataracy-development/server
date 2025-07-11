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
     * analysisPurpose 엔티티의 모든 데이터셋을 조회한다.
     * @return analysisPurpose 데이터셋
     */
    @Override
    public List<AnalysisPurpose> allAnalysisPurposes() {
        List<AnalysisPurposeEntity> analysisPurposeEntities = analysisPurposeJpaRepository.findAll();
        return analysisPurposeEntities.stream()
                .map(AnalysisPurposeEntityMapper::toDomain)
                .toList();
    }

    /**
     * 분석 목적 id에 해당하는 분석 목적이 존재하면 조회한다.
     * @param analysisPurposeId 분석 목적 아이디
     * @return 분석 목적
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
