/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.filestorage.application.service.command;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.filestorage.application.dto.response.GetPreSignedUrlResponse;
import com.dataracy.modules.filestorage.application.port.in.DownloadFileUseCase;
import com.dataracy.modules.filestorage.application.port.out.FileStoragePort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileDownloadService implements DownloadFileUseCase {
  private final FileStoragePort fileStoragePort;

  // Use Case 상수 정의
  private static final String DOWNLOAD_FILE_USE_CASE = "DownloadFileUseCase";

  /**
   * 지정된 S3 객체 URL과 만료 시간을 기반으로 사전 서명된 S3 URL을 생성하여 반환합니다.
   *
   * @param s3Url 사전 서명된 URL을 생성할 S3 객체의 URL
   * @param expirationSeconds URL의 만료 시간(초)
   * @return 생성된 사전 서명된 S3 URL이 포함된 응답 객체
   * @throws IllegalArgumentException s3Url이 비어있거나 만료 시간이 0 이하인 경우
   */
  @Override
  public GetPreSignedUrlResponse generatePreSignedUrl(String s3Url, int expirationSeconds) {
    Instant startTime =
        LoggerFactory.service().logStart(DOWNLOAD_FILE_USE_CASE, "서명된(pre-signed) URL을 생성 서비스 시작");
    if (!StringUtils.hasText(s3Url)) {
      LoggerFactory.service().logWarning(DOWNLOAD_FILE_USE_CASE, "S3 URL은 비어있을 수 없습니다.");
      throw new IllegalArgumentException("S3 URL은 비어있을 수 없습니다.");
    }
    if (expirationSeconds <= 0) {
      LoggerFactory.service().logWarning(DOWNLOAD_FILE_USE_CASE, "만료 시간은 0보다 커야 합니다.");
      throw new IllegalArgumentException("만료 시간은 0보다 커야 합니다.");
    }
    String preSignedUrl = fileStoragePort.getPreSignedUrl(s3Url, expirationSeconds);
    GetPreSignedUrlResponse getPresignedUrlResponse = new GetPreSignedUrlResponse(preSignedUrl);
    LoggerFactory.service()
        .logSuccess(DOWNLOAD_FILE_USE_CASE, "서명된(pre-signed) URL을 생성 서비스 종료", startTime);
    return getPresignedUrlResponse;
  }
}
