/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.filestorage.adapter.web.api;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.status.CommonSuccessStatus;
import com.dataracy.modules.filestorage.adapter.web.mapper.FileDownloadWebMapper;
import com.dataracy.modules.filestorage.adapter.web.response.GetPreSignedUrlWebResponse;
import com.dataracy.modules.filestorage.application.dto.response.GetPreSignedUrlResponse;
import com.dataracy.modules.filestorage.application.port.in.DownloadFileUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FileDownloadController implements FileApi {
  private final FileDownloadWebMapper fileDownloadWebMapper;

  private final DownloadFileUseCase downloadFileUseCase;

  /**
   * S3 파일의 URL과 만료 시간을 받아 다운로드용 PreSigned URL을 생성하여 반환합니다.
   *
   * @param s3Url PreSigned URL을 생성할 S3 파일의 URL
   * @param expirationSeconds PreSigned URL의 만료 시간(초)
   * @return PreSigned URL 정보가 포함된 성공 응답의 HTTP 200 OK 객체
   */
  @Override
  public ResponseEntity<SuccessResponse<GetPreSignedUrlWebResponse>> getPreSignedUrl(
      String s3Url, int expirationSeconds) {
    Instant startTime =
        LoggerFactory.api().logRequest("[GetPreSignedUrl] 파일 다운로드를 위한 프리사인드 URL 반환 API 요청 시작");
    GetPreSignedUrlWebResponse webResponse;

    try {
      GetPreSignedUrlResponse responseDto =
          downloadFileUseCase.generatePreSignedUrl(s3Url, expirationSeconds);
      webResponse = fileDownloadWebMapper.toWebDto(responseDto);
    } finally {
      LoggerFactory.api()
          .logResponse("[GetPreSignedUrl] 파일 다운로드를 위한 preSigned URL 반환 API 응답 완료", startTime);
    }

    return ResponseEntity.ok(SuccessResponse.of(CommonSuccessStatus.OK, webResponse));
  }
}
