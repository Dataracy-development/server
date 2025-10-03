/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.like.adapter.kafka.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.like.application.port.out.command.SendLikeEventPort;
import com.dataracy.modules.like.domain.enums.TargetType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LikeKafkaProducerAdapter implements SendLikeEventPort {
  private final KafkaTemplate<String, Long> kafkaTemplate;

  @Value("${spring.kafka.producer.project-like-increase.topic:project-like-increase-topic}")
  private String topicProjectLikeIncrease;

  @Value("${spring.kafka.producer.project-like-decrease.topic:project-like-decrease-topic}")
  private String topicProjectLikeDecrease;

  @Value("${spring.kafka.producer.comment-like-increase.topic:comment-like-increase-topic}")
  private String topicCommentLikeIncrease;

  @Value("${spring.kafka.producer.comment-like-decrease.topic:comment-like-decrease-topic}")
  private String topicCommentLikeDecrease;

  /**
   * 프로젝트 또는 댓글에 대한 좋아요 또는 좋아요 취소 이벤트를 Kafka로 비동기 전송합니다.
   *
   * @param targetType 이벤트 대상의 유형(PROJECT 또는 COMMENT)
   * @param targetId 이벤트 대상의 고유 식별자
   * @param previouslyLiked true이면 좋아요 취소 이벤트, false이면 좋아요 이벤트를 전송합니다.
   */
  @Override
  public void sendLikeEvent(TargetType targetType, Long targetId, boolean previouslyLiked) {
    switch (targetType) {
      case PROJECT -> sendProjectLikeEvent(targetId, previouslyLiked);
      case COMMENT -> sendCommentLikeEvent(targetId, previouslyLiked);
    }
  }

  private void sendProjectLikeEvent(Long targetId, boolean previouslyLiked) {
    if (previouslyLiked) {
      sendKafkaEvent(topicProjectLikeDecrease, targetId, "프로젝트 좋아요 취소", "projectId");
    } else {
      sendKafkaEvent(topicProjectLikeIncrease, targetId, "프로젝트 좋아요", "projectId");
    }
  }

  private void sendCommentLikeEvent(Long targetId, boolean previouslyLiked) {
    if (previouslyLiked) {
      sendKafkaEvent(topicCommentLikeDecrease, targetId, "댓글 좋아요 취소", "commentId");
    } else {
      sendKafkaEvent(topicCommentLikeIncrease, targetId, "댓글 좋아요", "commentId");
    }
  }

  private void sendKafkaEvent(
      String topic, Long targetId, String eventDescription, String idLabel) {
    kafkaTemplate
        .send(topic, String.valueOf(targetId), targetId)
        .whenComplete(
            (result, ex) -> logKafkaResult(topic, targetId, eventDescription, idLabel, ex));
  }

  private void logKafkaResult(
      String topic, Long targetId, String eventDescription, String idLabel, Throwable ex) {
    if (ex == null) {
      LoggerFactory.kafka()
          .logProduce(
              topic, String.format("%s 이벤트 발송됨: %s=%d", eventDescription, idLabel, targetId));
    } else {
      LoggerFactory.kafka()
          .logError(
              topic,
              String.format("%s 이벤트 발송 처리 실패: %s=%d", eventDescription, idLabel, targetId),
              ex);
    }
  }
}
