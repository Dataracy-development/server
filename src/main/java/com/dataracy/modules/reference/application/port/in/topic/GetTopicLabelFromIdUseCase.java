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

    Map<Long, String> getLabelsByIds(List<Long> topicIds);
}
