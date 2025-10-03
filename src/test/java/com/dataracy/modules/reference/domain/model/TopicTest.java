/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Topic 테스트")
class TopicTest {

  @Test
  @DisplayName("Topic record 생성 및 속성 확인")
  void topic_ShouldCreateCorrectly() {
    // Given
    Long id = 1L;
    String value = "data-analysis";
    String label = "데이터 분석";

    // When
    Topic topic = new Topic(id, value, label);

    // Then
    assertAll(
        () -> assertThat(topic.id()).isEqualTo(id),
        () -> assertThat(topic.value()).isEqualTo(value),
        () -> assertThat(topic.label()).isEqualTo(label));
  }

  @Test
  @DisplayName("Topic record equals 및 hashCode 테스트")
  void topic_ShouldHaveCorrectEqualsAndHashCode() {
    // Given
    Topic topic1 = new Topic(1L, "machine-learning", "머신러닝");
    Topic topic2 = new Topic(1L, "machine-learning", "머신러닝");
    Topic topic3 = new Topic(2L, "machine-learning", "머신러닝");

    // When & Then
    assertThat(topic1).isEqualTo(topic2).hasSameHashCodeAs(topic2).isNotEqualTo(topic3);
  }

  @Test
  @DisplayName("Topic record toString 테스트")
  void topic_ShouldHaveCorrectToString() {
    // Given
    Topic topic = new Topic(1L, "ai", "인공지능");

    // When
    String toString = topic.toString();

    // Then
    assertThat(toString).contains("Topic").contains("1").contains("ai").contains("인공지능");
  }

  @Test
  @DisplayName("Topic record - null 값 처리")
  void topic_ShouldHandleNullValues() {
    // Given & When
    Topic topic = new Topic(null, null, null);

    // Then
    assertAll(
        () -> assertThat(topic.id()).isNull(),
        () -> assertThat(topic.value()).isNull(),
        () -> assertThat(topic.label()).isNull());
  }

  @Test
  @DisplayName("Topic record - 다양한 값들 테스트")
  void topic_ShouldHandleVariousValues() {
    // Given & When
    Topic topic1 = new Topic(1L, "data-science", "데이터 사이언스");
    Topic topic2 = new Topic(2L, "big-data", "빅데이터");
    Topic topic3 = new Topic(3L, "visualization", "시각화");

    // Then
    assertAll(
        () -> assertThat(topic1.value()).isEqualTo("data-science"),
        () -> assertThat(topic2.value()).isEqualTo("big-data"),
        () -> assertThat(topic3.value()).isEqualTo("visualization"),
        () -> assertThat(topic1.label()).isEqualTo("데이터 사이언스"),
        () -> assertThat(topic2.label()).isEqualTo("빅데이터"),
        () -> assertThat(topic3.label()).isEqualTo("시각화"));
  }
}
