/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.user.application.port.in.command.command;

import org.springframework.web.multipart.MultipartFile;

import com.dataracy.modules.user.application.dto.request.command.ModifyUserInfoRequest;

public interface ModifyUserInfoUseCase {
  /**
   * 사용자의 정보를 변경합니다.
   *
   * <p>변경 대상인 사용자 ID와(을) 전달받아 프로필 이미지와 기타 수정 정보를 적용합니다.
   *
   * @param userId 대상 사용자의 식별자
   * @param profileImageFile 변경할 프로필 이미지 파일(있을 경우 전달)
   * @param requestDto 프로필 외에 수정할 사용자 정보가 담긴 DTO
   */
  void modifyUserInfo(
      Long userId, MultipartFile profileImageFile, ModifyUserInfoRequest requestDto);
}
