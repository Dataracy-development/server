package com.dataracy.modules.reference.application.port.in.occupation;

import java.util.List;
import java.util.Map;

public interface GetOccupationLabelFromIdUseCase {
    /**
 * 주어진 직업 ID에 해당하는 직업 라벨을 반환합니다.
 *
 * @param occupationId 조회할 직업의 고유 식별자
 * @return 해당 ID에 매핑된 직업 라벨
 */
String getLabelById(Long occupationId);

    /**
 * 주어진 직업 ID 목록에 대해 각 ID에 해당하는 직업명을 반환합니다.
 *
 * @param occupationIds 직업 ID의 리스트
 * @return 각 직업 ID와 해당 직업명이 매핑된 맵
 */
Map<Long, String> getLabelsByIds(List<Long> occupationIds);
}
