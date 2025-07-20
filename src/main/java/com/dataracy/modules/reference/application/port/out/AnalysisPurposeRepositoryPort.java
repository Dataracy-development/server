package com.dataracy.modules.reference.application.port.out;

import com.dataracy.modules.reference.domain.model.AnalysisPurpose;

import java.util.List;
import java.util.Map;
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
 * 주어진 ID로 AnalysisPurpose 엔티티를 조회합니다.
 *
 * @param analysisPurposeId 조회할 AnalysisPurpose의 고유 ID
 * @return 해당 ID의 AnalysisPurpose가 존재하면 Optional로 반환하며, 존재하지 않으면 빈 Optional을 반환합니다.
 */
    Optional<AnalysisPurpose> findAnalysisPurposeById(Long analysisPurposeId);

    /**
 * 지정된 ID의 AnalysisPurpose 엔티티가 데이터베이스에 존재하는지 확인합니다.
 *
 * @param analysisPurposeId 존재 여부를 확인할 AnalysisPurpose의 ID
 * @return 해당 ID의 엔티티가 존재하면 true, 그렇지 않으면 false
 */
boolean existsAnalysisPurposeById(Long analysisPurposeId);

    /**
 * 지정된 분석 목적 ID에 해당하는 라벨을 반환합니다.
 *
 * @param analysisPurposeId 라벨을 조회할 분석 목적의 고유 ID
 * @return 해당 ID의 라벨이 존재하면 Optional로 반환하며, 없으면 빈 Optional을 반환합니다.
 */
Optional<String> getLabelById(Long analysisPurposeId);

    /**
 * 주어진 분석 목적 ID 목록에 대해 각 ID에 해당하는 라벨을 반환합니다.
 *
 * @param analysisPurposeIds 라벨을 조회할 분석 목적 ID 목록
 * @return 각 ID와 해당 라벨이 매핑된 Map 객체. 라벨이 없는 ID는 결과에 포함되지 않을 수 있습니다.
 */
Map<Long, String> getLabelsByIds(List<Long> analysisPurposeIds);
}
