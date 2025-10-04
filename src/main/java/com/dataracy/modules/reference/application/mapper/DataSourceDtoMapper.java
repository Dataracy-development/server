package com.dataracy.modules.reference.application.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.dataracy.modules.reference.application.dto.response.allview.AllDataSourcesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.DataSourceResponse;
import com.dataracy.modules.reference.domain.model.DataSource;

/** DataSource 도메인 DTO와 DataSource 도메인 모델을 변환하는 매퍼 */
@Component
public class DataSourceDtoMapper {
  /**
   * DataSource 도메인 모델 객체를 DataSourceResponse DTO로 변환합니다.
   *
   * @param dataSource 변환할 DataSource 도메인 모델 객체
   * @return 변환된 DataSourceResponse DTO
   */
  public DataSourceResponse toResponseDto(DataSource dataSource) {
    return new DataSourceResponse(dataSource.id(), dataSource.value(), dataSource.label());
  }

  /**
   * DataSource 도메인 모델 리스트를 AllDataSourcesResponse DTO로 변환합니다.
   *
   * @param dataSources 변환할 DataSource 도메인 모델 리스트
   * @return 변환된 DataSourceResponse DTO 리스트를 포함하는 AllDataSourcesResponse 객체
   */
  public AllDataSourcesResponse toResponseDto(List<DataSource> dataSources) {
    return new AllDataSourcesResponse(dataSources.stream().map(this::toResponseDto).toList());
  }
}
