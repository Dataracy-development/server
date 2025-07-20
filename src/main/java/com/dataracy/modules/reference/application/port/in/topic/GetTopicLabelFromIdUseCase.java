package com.dataracy.modules.reference.application.port.in.topic;

import java.util.List;
import java.util.Map;

public interface GetTopicLabelFromIdUseCase {
    /**
 * 주어진 토픽 ID에 해당하는 라벨을 반환합니다.
 *
 * @param topicId 라벨을 조회할 토픽의 ID
 * @return 해당 토픽 ID에 연결된 라벨 문자열
 */
String getLabelById(Long topicId);

    /**
 * 여러 토픽 ID에 대해 각 토픽의 라벨을 조회하여 ID와 라벨의 매핑을 반환합니다.
 *
 * @param topicIds 라벨을 조회할 토픽 ID 목록
 * @return 각 토픽 ID와 해당 라벨이 매핑된 Map 객체
 */
Map<Long, String> getLabelsByIds(List<Long> topicIds);
}
