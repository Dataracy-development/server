package com.dataracy.modules.reference.application.port.in.occupation;

import java.util.List;
import java.util.Map;

public interface GetOccupationLabelFromIdUseCase {
    /**
 * 주어진 직업 ID에 해당하는 직업 라벨을 반환합니다.
 *
 * @param occupationId 직업의 고유 식별자
 * @return 해당 ID에 매핑되는 직업 라벨
 */
String getLabelById(Long occupationId);

    Map<Long, String> getLabelsByIds(List<Long> occupationIds);
}
