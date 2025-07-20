package com.dataracy.modules.reference.application.port.in.visitsource;

import java.util.List;
import java.util.Map;

public interface GetVisitSourceLabelFromIdUseCase {
    /**
 * 주어진 방문 소스 ID에 해당하는 라벨을 반환합니다.
 *
 * @param visitSourceId 방문 소스의 고유 식별자
 * @return 방문 소스에 해당하는 라벨 문자열
 */
String getLabelById(Long visitSourceId);

    /**
 * 여러 방문 소스 ID에 대해 각 ID에 해당하는 라벨을 매핑하여 반환합니다.
 *
 * @param visitSourceIds 방문 소스의 고유 식별자 목록
 * @return 각 방문 소스 ID와 해당 라벨이 매핑된 Map 객체
 */
Map<Long, String> getLabelsByIds(List<Long> visitSourceIds);
}
