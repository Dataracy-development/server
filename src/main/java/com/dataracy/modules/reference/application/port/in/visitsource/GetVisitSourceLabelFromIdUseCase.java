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

    Map<Long, String> getLabelsByIds(List<Long> visitSourceIds);
}
