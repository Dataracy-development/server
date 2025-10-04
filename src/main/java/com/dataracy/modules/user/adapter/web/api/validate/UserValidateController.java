package com.dataracy.modules.user.adapter.web.api.validate;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.adapter.web.mapper.validate.UserValidationWebMapper;
import com.dataracy.modules.user.adapter.web.request.validate.DuplicateNicknameWebRequest;
import com.dataracy.modules.user.application.dto.request.validate.DuplicateNicknameRequest;
import com.dataracy.modules.user.application.port.in.validate.DuplicateNicknameUseCase;
import com.dataracy.modules.user.domain.status.UserSuccessStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserValidateController implements UserValidateApi {
  private final UserValidationWebMapper userWebMapper;

  private final DuplicateNicknameUseCase duplicateNicknameUseCase;

  /**
   * 닉네임의 중복 여부를 검사하여 중복이 아닐 경우 성공 상태의 HTTP 200 응답을 반환한다.
   *
   * @param webRequest 중복 검사를 요청하는 닉네임 정보가 포함된 요청 객체
   * @return 닉네임이 중복되지 않을 때 성공 상태를 담은 HTTP 200 응답
   */
  @Override
  public ResponseEntity<SuccessResponse<Void>> duplicateNickname(
      DuplicateNicknameWebRequest webRequest) {
    Instant startTime = LoggerFactory.api().logRequest("[DuplicateNickname] 닉네임 중복 API 요청 시작");

    try {
      DuplicateNicknameRequest requestDto = userWebMapper.toApplicationDto(webRequest);
      duplicateNicknameUseCase.validateDuplicatedNickname(requestDto.nickname());
    } finally {
      LoggerFactory.api().logResponse("[DuplicateNickname] 닉네임 중복 API 응답 완료", startTime);
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body(SuccessResponse.of(UserSuccessStatus.OK_NOT_DUPLICATED_NICKNAME));
  }
}
