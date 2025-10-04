package com.dataracy.modules.dataset.application.port.in.command.content;

import org.springframework.web.multipart.MultipartFile;

import com.dataracy.modules.dataset.application.dto.request.command.ModifyDataRequest;

public interface ModifyDataUseCase {
  /**
   * 지정된 데이터셋의 정보를 새로운 데이터 파일, 썸네일 파일, 추가 수정 정보와 함께 갱신합니다.
   *
   * @param dataId 수정 대상 데이터셋의 고유 식별자
   * @param dataFile 교체할 데이터 파일
   * @param thumbnailFile 교체할 썸네일 파일
   * @param requestDto 데이터셋 수정에 필요한 부가 정보가 포함된 요청 객체
   */
  void modifyData(
      Long dataId,
      MultipartFile dataFile,
      MultipartFile thumbnailFile,
      ModifyDataRequest requestDto);
}
