/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.filestorage.application.port.in;

import org.springframework.web.multipart.MultipartFile;

public interface FileCommandUseCase {
  /**
   * 지정된 디렉터리에 파일을 업로드하고 업로드된 파일의 URL 또는 식별자를 반환합니다.
   *
   * @param directory 파일을 저장할 디렉터리 경로
   * @param file 업로드할 파일
   * @return 업로드된 파일의 URL 또는 식별자
   */
  String uploadFile(String directory, MultipartFile file);

  /**
   * 지정된 URL에 해당하는 파일을 삭제합니다.
   *
   * @param fileUrl 삭제할 파일의 URL
   */
  void deleteFile(String fileUrl);

  /**
   * 기존 파일을 삭제하고 지정된 디렉터리에 새 파일을 업로드한 후, 새 파일의 위치 또는 URL을 반환합니다.
   *
   * @param directory 파일이 업로드될 디렉터리 경로
   * @param newFile 업로드할 새 Multipart 파일
   * @param oldFileUrl 대체할 기존 파일의 URL 또는 위치
   * @return 새로 업로드된 파일의 위치 또는 URL
   */
  String replaceFile(String directory, MultipartFile newFile, String oldFileUrl);
}
