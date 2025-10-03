/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.adapter.elasticsearch.indexing;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mockStatic;

import java.io.IOException;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dataracy.modules.common.logging.ElasticLogger;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.dto.document.ProjectSearchDocument;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

@ExtendWith(MockitoExtension.class)
class IndexProjectAdapterTest {

  @Mock private ElasticsearchClient elasticsearchClient;

  @Mock private ElasticLogger elasticLogger;

  private IndexProjectAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new IndexProjectAdapter(elasticsearchClient);
  }

  @Test
  @DisplayName("프로젝트 인덱싱 성공 시 정상 동작")
  void indexProjectSuccess() throws IOException {
    // given
    ProjectSearchDocument doc = createTestProjectDocument();

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

      // when
      adapter.index(doc);

      // then
      then(elasticsearchClient).should().index(any(Function.class));
      then(elasticLogger).should().logIndex("project_index", "123", "프로젝트 인덱싱 완료");
    }
  }

  @Test
  @DisplayName("IOException 발생 시 예외를 외부로 전달하지 않고 로깅만 수행")
  void indexProjectFailure() throws IOException {
    // given
    ProjectSearchDocument doc = createTestProjectDocument();
    IOException ioException = new IOException("Connection failed");
    willThrow(ioException).given(elasticsearchClient).index(any(Function.class));

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

      // when
      adapter.index(doc);

      // then
      then(elasticsearchClient).should().index(any(Function.class));
      then(elasticLogger).should().logError("project_index", "프로젝트 인덱싱 실패: docId=123", ioException);
    }
  }

  @Test
  @DisplayName("큰 프로젝트 ID로 인덱싱 성공")
  void indexProjectWithLargeId() throws IOException {
    // given
    ProjectSearchDocument doc =
        ProjectSearchDocument.builder()
            .id(999999L)
            .title("Large Project")
            .content("Large project content")
            .userId(456L)
            .username("testuser")
            .build();

    try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
      loggerFactoryMock.when(LoggerFactory::elastic).thenReturn(elasticLogger);

      // when
      adapter.index(doc);

      // then
      then(elasticsearchClient).should().index(any(Function.class));
      then(elasticLogger).should().logIndex("project_index", "999999", "프로젝트 인덱싱 완료");
    }
  }

  private ProjectSearchDocument createTestProjectDocument() {
    return ProjectSearchDocument.builder()
        .id(123L)
        .title("Test Project")
        .content("Test project content")
        .userId(456L)
        .username("testuser")
        .userProfileImageUrl("http://example.com/profile.jpg")
        .projectThumbnailUrl("http://example.com/thumb.jpg")
        .topicId(1L)
        .topicLabel("Test Topic")
        .dataSourceId(1L)
        .dataSourceLabel("Test Source")
        .analysisPurposeId(1L)
        .analysisPurposeLabel("Test Purpose")
        .authorLevelId(1L)
        .authorLevelLabel("Test Level")
        .isDeleted(false)
        .viewCount(0L)
        .likeCount(0L)
        .commentCount(0L)
        .createdAt(java.time.LocalDateTime.of(2024, 1, 1, 0, 0, 0))
        .build();
  }
}
