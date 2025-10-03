/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.adapter.elasticsearch.query;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.dto.document.DataSearchDocument;
import com.dataracy.modules.dataset.application.dto.response.read.RecentMinimalDataResponse;
import com.dataracy.modules.dataset.application.port.out.query.search.SearchRealTimeDataSetsPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SearchRealTimeDataSetsEsAdapter implements SearchRealTimeDataSetsPort {
  private final ElasticsearchClient client;
  private static final String INDEX = "data_index";

  /**
   * 주어진 키워드로 Elasticsearch에서 삭제되지 않은 데이터셋을 실시간으로 검색하여 최소 정보 목록을 반환합니다.
   *
   * <p>검색은 "title"(가중치 2)과 "description" 필드에 대해 자동 퍼지 멀티매치 쿼리로 수행되며, "isDeleted"가 false인 문서만 포함됩니다.
   * 결과는 생성일(createdAt) 기준 내림차순으로 정렬되고 요청한 개수로 제한됩니다. 반환되는 각 RecentMinimalDataResponse에는 데이터셋의 id,
   * title, 작성자(userId, username), 썸네일 URL 및 생성일이 포함됩니다.
   *
   * @param keyword 검색에 사용할 키워드
   * @param size 반환할 최대 데이터셋 개수
   * @return 검색된 데이터셋의 최소 정보 응답 객체 리스트
   * @throws DataException 실시간 데이터셋 검색에 실패한 경우 발생
   */
  @Override
  public List<RecentMinimalDataResponse> searchRealTimeDataSets(String keyword, int size) {
    try {
      Instant startTime =
          LoggerFactory.elastic()
              .logQueryStart(
                  INDEX, "데이터셋 검색어 자동완성 실시간 검색 시작: keyword=" + keyword + ", size=" + size);
      SearchResponse<DataSearchDocument> response =
          client.search(
              s ->
                  s.index(INDEX)
                      .size(size)
                      .sort(sort -> sort.field(f -> f.field("createdAt").order(SortOrder.Desc)))
                      .query(
                          q ->
                              q.bool(
                                  b ->
                                      b.must(
                                              m ->
                                                  m.multiMatch(
                                                      mm ->
                                                          mm.fields("title^2", "description")
                                                              .query(keyword)
                                                              .fuzziness("AUTO")))
                                          .filter(
                                              f ->
                                                  f.term(t -> t.field("isDeleted").value(false))))),
              DataSearchDocument.class);
      List<RecentMinimalDataResponse> recentMinimalDataResponses =
          response.hits().hits().stream()
              .map(
                  hit -> {
                    var doc = hit.source();
                    return new RecentMinimalDataResponse(
                        doc.id(),
                        doc.title(),
                        doc.userId(),
                        doc.username(),
                        doc.userProfileImageUrl(),
                        doc.dataThumbnailUrl(),
                        doc.createdAt());
                  })
              .toList();
      LoggerFactory.elastic()
          .logQueryEnd(
              INDEX, "데이터셋 검색어 자동완성 실시간 검색 종료: keyword=" + keyword + ", size=" + size, startTime);
      return recentMinimalDataResponses;
    } catch (IOException e) {
      LoggerFactory.elastic()
          .logError(INDEX, "데이터셋 실시간 검색 실패: keyword=" + keyword + ", size=" + size, e);
      throw new DataException(DataErrorStatus.FAIL_REAL_TIME_SEARCH_DATASET);
    }
  }
}
