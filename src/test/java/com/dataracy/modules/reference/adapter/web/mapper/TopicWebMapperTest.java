package com.dataracy.modules.reference.adapter.web.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.reference.adapter.web.response.allview.AllTopicsWebResponse;
import com.dataracy.modules.reference.adapter.web.response.singleview.TopicWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllTopicsResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.TopicResponse;

class TopicWebMapperTest {

  private TopicWebMapper topicWebMapper;

  @BeforeEach
  void setUp() {
    topicWebMapper = new TopicWebMapper();
  }

  @Test
  @DisplayName("toWebDto - TopicResponse를 TopicWebResponse로 변환한다")
  void toWebDtoWhenTopicResponseConvertsToTopicWebResponse() {
    // given
    TopicResponse topicResponse = new TopicResponse(1L, "AI", "인공지능");

    // when
    TopicWebResponse result = topicWebMapper.toWebDto(topicResponse);

    // then

    assertAll(
        () -> assertThat(result.id()).isEqualTo(1L),
        () -> assertThat(result.value()).isEqualTo("AI"),
        () -> assertThat(result.label()).isEqualTo("인공지능"));
  }

  @Test
  @DisplayName("toWebDto - AllTopicsResponse를 AllTopicsWebResponse로 변환한다")
  void toWebDtoWhenAllTopicsResponseConvertsToAllTopicsWebResponse() {
    // given
    List<TopicResponse> topics =
        List.of(
            new TopicResponse(1L, "AI", "인공지능"),
            new TopicResponse(2L, "ML", "머신러닝"),
            new TopicResponse(3L, "DL", "딥러닝"));
    AllTopicsResponse allTopicsResponse = new AllTopicsResponse(topics);

    // when
    AllTopicsWebResponse result = topicWebMapper.toWebDto(allTopicsResponse);

    // then
    assertAll(
        () -> assertThat(result.topics()).hasSize(3),
        () -> assertThat(result.topics().get(0).id()).isEqualTo(1L),
        () -> assertThat(result.topics().get(0).value()).isEqualTo("AI"),
        () -> assertThat(result.topics().get(0).label()).isEqualTo("인공지능"),
        () -> assertThat(result.topics().get(1).id()).isEqualTo(2L),
        () -> assertThat(result.topics().get(1).value()).isEqualTo("ML"),
        () -> assertThat(result.topics().get(1).label()).isEqualTo("머신러닝"),
        () -> assertThat(result.topics().get(2).id()).isEqualTo(3L),
        () -> assertThat(result.topics().get(2).value()).isEqualTo("DL"),
        () -> assertThat(result.topics().get(2).label()).isEqualTo("딥러닝"));
  }

  @Test
  @DisplayName("toWebDto - AllTopicsResponse가 null인 경우 빈 리스트를 반환한다")
  void toWebDtoWhenAllTopicsResponseIsNullReturnsEmptyList() {
    // when
    AllTopicsWebResponse result = topicWebMapper.toWebDto((AllTopicsResponse) null);

    // then
    assertThat(result.topics()).isEmpty();
  }

  @Test
  @DisplayName("toWebDto - AllTopicsResponse의 topics가 null인 경우 빈 리스트를 반환한다")
  void toWebDtoWhenTopicsIsNullReturnsEmptyList() {
    // given
    AllTopicsResponse allTopicsResponse = new AllTopicsResponse(null);

    // when
    AllTopicsWebResponse result = topicWebMapper.toWebDto(allTopicsResponse);

    // then
    assertThat(result.topics()).isEmpty();
  }

  @Test
  @DisplayName("toWebDto - AllTopicsResponse의 topics가 빈 리스트인 경우 빈 리스트를 반환한다")
  void toWebDtoWhenTopicsIsEmptyReturnsEmptyList() {
    // given
    AllTopicsResponse allTopicsResponse = new AllTopicsResponse(List.of());

    // when
    AllTopicsWebResponse result = topicWebMapper.toWebDto(allTopicsResponse);

    // then
    assertThat(result.topics()).isEmpty();
  }

  @Test
  @DisplayName("toWebDto - TopicResponse의 모든 필드가 null인 경우에도 변환한다")
  void toWebDtoWhenTopicResponseFieldsAreNullConvertsToTopicWebResponse() {
    // given
    TopicResponse topicResponse = new TopicResponse(null, null, null);

    // when
    TopicWebResponse result = topicWebMapper.toWebDto(topicResponse);

    // then
    assertAll(
        () -> assertThat(result.id()).isNull(),
        () -> assertThat(result.value()).isNull(),
        () -> assertThat(result.label()).isNull());
  }
}
