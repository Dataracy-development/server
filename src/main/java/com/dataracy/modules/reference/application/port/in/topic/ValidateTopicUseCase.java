/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.application.port.in.topic;

public interface ValidateTopicUseCase {
  /**
   * 주어진 토픽 ID에 해당하는 토픽을 검증합니다.
   *
   * @param topicId 검증할 토픽의 ID
   */
  void validateTopic(Long topicId);
}
