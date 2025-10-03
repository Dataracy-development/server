/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.application.port.in.topic;

import com.dataracy.modules.reference.application.dto.response.singleview.TopicResponse;

public interface FindTopicUseCase {
  /**
   * 주어진 토픽 ID에 해당하는 토픽 정보를 조회합니다.
   *
   * @param topicId 조회할 토픽의 고유 식별자
   * @return 조회된 토픽의 상세 정보
   */
  TopicResponse findTopic(Long topicId);
}
