package com.dataracy.modules.project.adapter.web.mapper.search;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.project.adapter.web.response.search.RealTimeProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.search.SimilarProjectWebResponse;
import com.dataracy.modules.project.application.dto.response.search.RealTimeProjectResponse;
import com.dataracy.modules.project.application.dto.response.search.SimilarProjectResponse;

class ProjectSearchWebMapperTest {

  private final ProjectSearchWebMapper mapper = new ProjectSearchWebMapper();

  @Test
  @DisplayName("성공 → RealTimeProjectResponse를 RealTimeProjectWebResponse로 매핑")
  void toWebRealTimeProject() {
    // given
    RealTimeProjectResponse responseDto =
        new RealTimeProjectResponse(1L, "real-time title", 1L, "userA", "https://~~", "thumb.png");

    // when
    RealTimeProjectWebResponse webResponse = mapper.toWebDto(responseDto);

    // then
    assertAll(
        () -> assertThat(webResponse.id()).isEqualTo(1L),
        () -> assertThat(webResponse.title()).isEqualTo("real-time title"),
        () -> assertThat(webResponse.creatorName()).isEqualTo("userA"),
        () -> assertThat(webResponse.projectThumbnailUrl()).isEqualTo("thumb.png"));
  }

  @Test
  @DisplayName("성공 → SimilarProjectResponse를 SimilarProjectWebResponse로 매핑")
  void toWebSimilarProject() {
    // given
    SimilarProjectResponse responseDto =
        new SimilarProjectResponse(
            2L,
            "similar title",
            "some content",
            1L,
            "userB",
            "https://~~",
            "thumb2.png",
            "topicLabel",
            "analysisPurposeLabel",
            "dataSourceLabel",
            "authorLevelLabel",
            10L,
            20L,
            30L);

    // when
    SimilarProjectWebResponse webResponse = mapper.toWebDto(responseDto);

    // then
    assertAll(
        () -> assertThat(webResponse.id()).isEqualTo(2L),
        () -> assertThat(webResponse.title()).isEqualTo("similar title"),
        () -> assertThat(webResponse.content()).isEqualTo("some content"),
        () -> assertThat(webResponse.creatorName()).isEqualTo("userB"),
        () -> assertThat(webResponse.projectThumbnailUrl()).isEqualTo("thumb2.png"),
        () -> assertThat(webResponse.topicLabel()).isEqualTo("topicLabel"),
        () -> assertThat(webResponse.analysisPurposeLabel()).isEqualTo("analysisPurposeLabel"),
        () -> assertThat(webResponse.dataSourceLabel()).isEqualTo("dataSourceLabel"),
        () -> assertThat(webResponse.authorLevelLabel()).isEqualTo("authorLevelLabel"),
        () -> assertThat(webResponse.commentCount()).isEqualTo(10L),
        () -> assertThat(webResponse.likeCount()).isEqualTo(20L),
        () -> assertThat(webResponse.viewCount()).isEqualTo(30L));
  }
}
