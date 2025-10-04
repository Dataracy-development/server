package com.dataracy.modules.reference.adapter.web.api.datasource;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.reference.adapter.web.mapper.DataSourceWebMapper;
import com.dataracy.modules.reference.adapter.web.response.allview.AllDataSourcesWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllDataSourcesResponse;
import com.dataracy.modules.reference.application.port.in.datasource.FindAllDataSourcesUseCase;
import com.dataracy.modules.reference.domain.status.ReferenceSuccessStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DataSourceController implements DataSourceApi {
  private final DataSourceWebMapper dataSourceWebMapper;
  private final FindAllDataSourcesUseCase findAllDataSourcesUseCase;

  /**
   * 전체 데이터 출처 목록을 조회하여 HTTP 200 OK로 반환합니다. 애플리케이션 계층에서 데이터 출처 목록을 조회한 뒤, 웹 응답 DTO로 변환하여 성공 상태 코드와
   * 함께 반환합니다.
   *
   * @return 전체 데이터 출처 목록이 포함된 성공 응답 엔티티
   */
  @Override
  public ResponseEntity<SuccessResponse<AllDataSourcesWebResponse>> findAllDataSources() {
    Instant startTime =
        LoggerFactory.api().logRequest("[FindAllDataSources] 전체 데이터 출처 목록을 조회 API 요청 시작");
    AllDataSourcesWebResponse allDataSourcesWebResponse;

    try {
      AllDataSourcesResponse allDataSourcesResponse =
          findAllDataSourcesUseCase.findAllDataSources();
      allDataSourcesWebResponse = dataSourceWebMapper.toWebDto(allDataSourcesResponse);
    } finally {
      LoggerFactory.api().logResponse("[FindAllDataSources] 전체 데이터 출처 목록을 조회 API 응답 완료", startTime);
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body(
            SuccessResponse.of(
                ReferenceSuccessStatus.OK_TOTAL_DATA_SOURCE_LIST, allDataSourcesWebResponse));
  }
}
