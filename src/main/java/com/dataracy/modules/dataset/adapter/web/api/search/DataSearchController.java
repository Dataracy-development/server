package com.dataracy.modules.dataset.adapter.web.api.search;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.adapter.web.mapper.search.DataFilterWebMapper;
import com.dataracy.modules.dataset.adapter.web.mapper.search.DataSearchWebMapper;
import com.dataracy.modules.dataset.adapter.web.request.search.FilteringDataWebRequest;
import com.dataracy.modules.dataset.adapter.web.response.read.RecentMinimalDataWebResponse;
import com.dataracy.modules.dataset.adapter.web.response.search.FilteredDataWebResponse;
import com.dataracy.modules.dataset.adapter.web.response.search.SimilarDataWebResponse;
import com.dataracy.modules.dataset.application.dto.request.search.FilteringDataRequest;
import com.dataracy.modules.dataset.application.dto.response.read.RecentMinimalDataResponse;
import com.dataracy.modules.dataset.application.dto.response.search.FilteredDataResponse;
import com.dataracy.modules.dataset.application.dto.response.search.SimilarDataResponse;
import com.dataracy.modules.dataset.application.port.in.query.search.SearchFilteredDataSetsUseCase;
import com.dataracy.modules.dataset.application.port.in.query.search.SearchRealTimeDataSetsUseCase;
import com.dataracy.modules.dataset.application.port.in.query.search.SearchSimilarDataSetsUseCase;
import com.dataracy.modules.dataset.domain.status.DataSuccessStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DataSearchController implements DataSearchApi {
  private final DataSearchWebMapper dataSearchWebMapper;
  private final DataFilterWebMapper dataFilterWebMapper;

  private final SearchSimilarDataSetsUseCase searchSimilarDataSetsUseCase;
  private final SearchFilteredDataSetsUseCase searchFilteredDataSetsUseCase;
  private final SearchRealTimeDataSetsUseCase searchRealTimeDataSetsUseCase;

  /**
   * 지정한 데이터 ID를 기준으로 유사한 데이터셋을 최대 size만큼 조회하여 반환합니다.
   *
   * @param dataId 유사 데이터셋 검색의 기준이 되는 데이터 ID
   * @param size 반환할 유사 데이터셋의 최대 개수
   * @return 유사 데이터셋 목록과 성공 상태가 포함된 HTTP 200 OK 응답
   */
  @Override
  public ResponseEntity<SuccessResponse<List<SimilarDataWebResponse>>> searchSimilarDataSets(
      Long dataId, int size) {
    Instant startTime =
        LoggerFactory.api().logRequest("[SearchSimilarDataSets] 유사 데이터셋 목록 반환 API 요청 시작");
    List<SimilarDataWebResponse> webResponse;

    try {
      List<SimilarDataResponse> responseDto =
          searchSimilarDataSetsUseCase.searchSimilarDataSets(dataId, size);
      webResponse = responseDto.stream().map(dataSearchWebMapper::toWebDto).toList();
    } finally {
      LoggerFactory.api().logResponse("[SearchSimilarDataSets] 유사 데이터셋 목록 반환 API 응답 완료", startTime);
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body(SuccessResponse.of(DataSuccessStatus.FIND_SIMILAR_DATASETS, webResponse));
  }

  /**
   * 필터 조건과 페이지 정보를 기반으로 데이터셋을 검색하여 페이징된 결과를 반환합니다.
   *
   * @param webRequest 데이터셋 필터링 조건이 포함된 요청 객체
   * @param pageable 결과의 페이지네이션 정보를 담은 객체
   * @return 필터링 및 페이징된 데이터셋 목록이 포함된 성공 응답
   */
  @Override
  public ResponseEntity<SuccessResponse<Page<FilteredDataWebResponse>>> searchFilteredDataSets(
      FilteringDataWebRequest webRequest, Pageable pageable) {
    Instant startTime =
        LoggerFactory.api().logRequest("[SearchFilteredDataSets] 필터링된 데이터셋 목록 반환 API 요청 시작");
    Page<FilteredDataWebResponse> webResponse;

    try {
      FilteringDataRequest requestDto = dataFilterWebMapper.toApplicationDto(webRequest);
      Page<FilteredDataResponse> responseDto =
          searchFilteredDataSetsUseCase.searchFilteredDataSets(requestDto, pageable);
      webResponse = responseDto.map(dataFilterWebMapper::toWebDto);
    } finally {
      LoggerFactory.api()
          .logResponse("[SearchFilteredDataSets] 필터링된 데이터셋 목록 반환 API 응답 완료", startTime);
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body(SuccessResponse.of(DataSuccessStatus.FIND_FILTERED_DATASETS, webResponse));
  }

  /**
   * 주어진 키워드로 최근 데이터셋 목록을 최대 지정된 개수만큼 조회하여 반환합니다.
   *
   * @param keyword 데이터셋 검색에 사용할 키워드
   * @param size 반환할 데이터셋의 최대 개수
   * @return 실시간 데이터셋 목록과 성공 상태가 포함된 HTTP 200 응답
   */
  @Override
  public ResponseEntity<SuccessResponse<List<RecentMinimalDataWebResponse>>> getRealTimeDataSets(
      String keyword, int size) {
    Instant startTime =
        LoggerFactory.api().logRequest("[GetRealTimeDataSets] 주어진 키워드로 실시간 데이터셋 목록 조회 API 요청 시작");
    List<RecentMinimalDataWebResponse> webResponse;

    try {
      List<RecentMinimalDataResponse> responseDto =
          searchRealTimeDataSetsUseCase.searchRealTimeDataSets(keyword, size);
      webResponse = responseDto.stream().map(dataSearchWebMapper::toWebDto).toList();
    } finally {
      LoggerFactory.api()
          .logResponse("[GetRealTimeDataSets] 주어진 키워드로 실시간 데이터셋 목록 조회환 API 응답 완료", startTime);
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body(SuccessResponse.of(DataSuccessStatus.FIND_REAL_TIME_DATASETS, webResponse));
  }
}
