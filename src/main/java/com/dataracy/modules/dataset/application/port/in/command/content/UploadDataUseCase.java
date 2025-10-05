package com.dataracy.modules.dataset.application.port.in.command.content;

import org.springframework.web.multipart.MultipartFile;

import com.dataracy.modules.dataset.application.dto.request.command.UploadDataRequest;
import com.dataracy.modules.dataset.application.dto.response.command.UploadDataResponse;

public interface UploadDataUseCase {
  /**
   * 데이터셋 파일과 썸네일을 업로드하고 업로드 결과를 반환합니다.
   *
   * <p>데이터셋 파일(dataFile)과 해당 썸네일(thumbnailFile)을 저장하고, 요청(requestDto)에 포함된 메타정보를 반영하여 업로드된 데이터의 식별자
   * 및 관련 결과를 담은 {@code UploadDataResponse}를 반환합니다.
   *
   * @param userId 업로드를 요청한 사용자 ID
   * @param dataFile 업로드할 데이터셋 파일
   * @param thumbnailFile 데이터셋에 연결할 썸네일 파일
   * @param requestDto 업로드 요청에 대한 추가 메타정보를 담은 DTO
   * @return 업로드 결과(생성된 데이터셋 ID 등)를 포함한 {@code UploadDataResponse}
   */
  UploadDataResponse uploadData(
      Long userId,
      MultipartFile dataFile,
      MultipartFile thumbnailFile,
      UploadDataRequest requestDto);
}
