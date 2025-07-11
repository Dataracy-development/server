package com.dataracy.modules.reference.application.port.out;

import com.dataracy.modules.reference.domain.model.AnalysisPurpose;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AnalysisPurpose db에 접근하는 포트
 */
@Repository
public interface AnalysisPurposeRepositoryPort {
    /**
 * 데이터베이스에 저장된 모든 분석 목적(AnalysisPurpose) 목록을 반환합니다.
 *
 * @return 전체 분석 목적 엔티티의 리스트
 */
List<AnalysisPurpose> allAnalysisPurposes();
    /**
 * 주어진 ID에 해당하는 AnalysisPurpose 엔티티를 조회합니다.
 *
 * @param analysisPurposeId 조회할 AnalysisPurpose의 고유 식별자
 * @return 해당 ID의 AnalysisPurpose 객체
 */
AnalysisPurpose findAnalysisPurposeById(Long analysisPurposeId);
}
