package com.dataracy.modules.project.adapter.elasticsearch.query;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.dto.document.ProjectSearchDocument;
import com.dataracy.modules.project.application.dto.response.search.RealTimeProjectResponse;
import com.dataracy.modules.project.application.port.out.query.search.SearchRealTimeProjectsPort;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SearchRealTimeProjectsEsAdapter implements SearchRealTimeProjectsPort {
  private final ElasticsearchClient client;
  private static final String INDEX = "project_index";

  /**
   * 키워드로 Elasticsearch "project_index"에서 삭제되지 않은 프로젝트를 검색해 실시간 자동완성 결과를 반환합니다.
   *
   * <p>title(가중치 3)과 username(가중치 2)에 대해 멀티매치 쿼리(fuzziness="AUTO")를 적용하고, isDeleted=true인 문서는
   * 제외합니다. 결과는 createdAt 내림차순으로 정렬됩니다.
   *
   * @param keyword 검색에 사용할 키워드
   * @param size 반환할 최대 결과 개수
   * @return 검색된 프로젝트 정보를 담은 RealTimeProjectResponse 리스트
   * @throws ProjectException 실시간 프로젝트 검색이 실패한 경우 발생합니다.
   */
  @Override
  public List<RealTimeProjectResponse> searchByKeyword(String keyword, int size) {
    try {
      Instant startTime =
          LoggerFactory.elastic()
              .logQueryStart(
                  INDEX, "프로젝트 검색어 자동완성 실시간 검색 시작: keyword=" + keyword + ", size=" + size);
      SearchResponse<ProjectSearchDocument> response =
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
                                                          mm.fields("title^3", "username^2")
                                                              .query(keyword)
                                                              .fuzziness("AUTO")))
                                          .filter(
                                              f ->
                                                  f.term(t -> t.field("isDeleted").value(false))))),
              ProjectSearchDocument.class);

      List<RealTimeProjectResponse> realTimeProjectResponses =
          response.hits().hits().stream()
              .map(
                  hit -> {
                    ProjectSearchDocument doc =
                        Objects.requireNonNull(hit.source(), "검색된 document가 null값입니다.");
                    return new RealTimeProjectResponse(
                        doc.id(),
                        doc.title(),
                        doc.userId(),
                        doc.username(),
                        doc.userProfileImageUrl(),
                        doc.projectThumbnailUrl());
                  })
              .toList();
      LoggerFactory.elastic()
          .logQueryEnd(
              INDEX, "프로젝트 검색어 자동완성 실시간 검색 종료: keyword=" + keyword + ", size=" + size, startTime);
      return realTimeProjectResponses;
    } catch (IOException e) {
      LoggerFactory.elastic()
          .logError(INDEX, "프로젝트 실시간 검색 실패: keyword=" + keyword + ", size=" + size, e);
      throw new ProjectException(ProjectErrorStatus.FAIL_REAL_TIME_SEARCH_PROJECT);
    }
  }
}
