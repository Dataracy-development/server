/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.adapter.web.mapper.command;

import org.springframework.stereotype.Component;

import com.dataracy.modules.dataset.adapter.web.request.command.ModifyDataWebRequest;
import com.dataracy.modules.dataset.adapter.web.request.command.UploadDataWebRequest;
import com.dataracy.modules.dataset.adapter.web.response.command.UploadDataWebResponse;
import com.dataracy.modules.dataset.application.dto.request.command.ModifyDataRequest;
import com.dataracy.modules.dataset.application.dto.request.command.UploadDataRequest;
import com.dataracy.modules.dataset.application.dto.response.command.UploadDataResponse;

/** 데이터 command 웹 DTO와 애플리케이션 DTO를 변환하는 매퍼 */
@Component
public class DataCommandWebMapper {
  /**
   * 객체를 UploadDataRequest DTO로 변환합니다. 웹 계층의 데이터 업로드 요청을 애플리케이션 계층에서 사용하는 DTO로 매핑합니다.
   *
   * @param webRequest 변환할 데이터 업로드 웹 요청 객체
   * @return 매핑된 데이터 업로드 애플리케이션 요청 DTO
   */
  public UploadDataRequest toApplicationDto(UploadDataWebRequest webRequest) {
    return new UploadDataRequest(
        webRequest.title(),
        webRequest.topicId(),
        webRequest.dataSourceId(),
        webRequest.dataTypeId(),
        webRequest.startDate(),
        webRequest.endDate(),
        webRequest.description(),
        webRequest.analysisGuide());
  }

  /**
   * 애플리케이션 계층의 UploadDataResponse를 웹 계층의 UploadDataWebResponse로 변환합니다.
   *
   * @param responseDto 변환할 애플리케이션 응답 DTO (생성된 데이터셋의 식별자(id)를 포함)
   * @return 생성된 데이터셋의 id를 담은 UploadDataWebResponse
   */
  public UploadDataWebResponse toWebDto(UploadDataResponse responseDto) {
    return new UploadDataWebResponse(responseDto.id());
  }

  /**
   * 데이터 수정 웹 요청 객체를 애플리케이션 계층의 데이터 수정 요청 DTO로 변환합니다.
   *
   * @param webRequest 데이터 수정 정보를 포함한 웹 요청 객체
   * @return 변환된 데이터 수정 애플리케이션 요청 DTO
   */
  public ModifyDataRequest toApplicationDto(ModifyDataWebRequest webRequest) {
    return new ModifyDataRequest(
        webRequest.title(),
        webRequest.topicId(),
        webRequest.dataSourceId(),
        webRequest.dataTypeId(),
        webRequest.startDate(),
        webRequest.endDate(),
        webRequest.description(),
        webRequest.analysisGuide());
  }
}
