package com.dataracy.modules.dataset.adapter.web.mapper.download;

import org.springframework.stereotype.Component;

import com.dataracy.modules.dataset.adapter.web.response.download.GetDataPreSignedUrlWebResponse;
import com.dataracy.modules.dataset.application.dto.response.download.GetDataPreSignedUrlResponse;

/** 데이터 다운로드 웹 DTO와 애플리케이션 DTO를 변환하는 매퍼 */
@Component
public class DataDownloadWebMapper {
  /**
   * 애플리케이션 계층의 데이터 사전 서명 URL 응답 DTO를 웹 계층의 응답 DTO로 변환합니다.
   *
   * @param responseDto 변환할 데이터 사전 서명 URL 애플리케이션응답 DTO
   * @return 변환된 웹 계층 데이터 사전 서명 URL 응답 DTO
   */
  public GetDataPreSignedUrlWebResponse toWebDto(GetDataPreSignedUrlResponse responseDto) {
    return new GetDataPreSignedUrlWebResponse(responseDto.preSignedUrl());
  }
}
