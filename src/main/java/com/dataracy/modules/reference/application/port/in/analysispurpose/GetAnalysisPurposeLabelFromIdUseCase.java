package com.dataracy.modules.reference.application.port.in.analysispurpose;

import java.util.List;
import java.util.Map;

public interface GetAnalysisPurposeLabelFromIdUseCase {
    /**
 * 주어진 분석 목적 ID에 해당하는 라벨을 반환합니다.
 *
 * @param analysisPurposeId 분석 목적의 고유 식별자
 * @return 해당 ID에 매핑된 라벨 문자열
 */
String getLabelById(Long analysisPurposeId);

    /**
 * 주어진 분석 목적 ID 목록에 대해 각 ID에 해당하는 라벨 문자열을 반환합니다.
 *
 * @param analysisPurposeIds 라벨을 조회할 분석 목적 ID 목록
 * @return 각 분석 목적 ID와 해당 라벨 문자열의 매핑을 담은 Map 객체
 */
Map<Long, String> getLabelsByIds(List<Long> analysisPurposeIds);
}
