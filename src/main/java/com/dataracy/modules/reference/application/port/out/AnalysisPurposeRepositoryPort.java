package com.dataracy.modules.reference.application.port.out;

import com.dataracy.modules.reference.domain.model.AnalysisPurpose;

import java.util.List;
import java.util.Optional;

/**
 * AnalysisPurpose db에 접근하는 포트
 */
public interface AnalysisPurposeRepositoryPort {
    /**
 * 데이터베이스에 저장된 모든 분석 목적(AnalysisPurpose) 엔티티의 목록을 반환합니다.
 *
 * @return 전체 분석 목적 엔티티의 리스트
 */
    List<AnalysisPurpose> findAllAnalysisPurposes();

    /**
 * 주어진 ID에 해당하는 AnalysisPurpose 엔티티를 조회합니다.
 *
 * @param analysisPurposeId 조회할 AnalysisPurpose의 고유 식별자
 * @return 해당 ID의 AnalysisPurpose가 존재하면 Optional에 담아 반환하며, 없으면 빈 Optional을 반환합니다.
 */
    Optional<AnalysisPurpose> findAnalysisPurposeById(Long analysisPurposeId);

    Boolean existsAnalysisPurposeById(Long analysisPurposeId);
}
