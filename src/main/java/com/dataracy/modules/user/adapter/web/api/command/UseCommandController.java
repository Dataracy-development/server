package com.dataracy.modules.user.adapter.web.api.command;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.util.CookieUtil;
import com.dataracy.modules.user.adapter.web.mapper.command.UserCommandWebMapper;
import com.dataracy.modules.user.adapter.web.request.command.ModifyUserInfoWebRequest;
import com.dataracy.modules.user.application.dto.request.command.ModifyUserInfoRequest;
import com.dataracy.modules.user.application.port.in.command.command.LogoutUserUseCase;
import com.dataracy.modules.user.application.port.in.command.command.ModifyUserInfoUseCase;
import com.dataracy.modules.user.application.port.in.command.command.WithdrawUserUseCase;
import com.dataracy.modules.user.domain.status.UserSuccessStatus;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UseCommandController implements UserCommandApi {
  private final UserCommandWebMapper userCommandWebMapper;

  private final ModifyUserInfoUseCase modifyUserInfoUseCase;
  private final WithdrawUserUseCase withdrawUserUseCase;
  private final LogoutUserUseCase logoutUserUseCase;
  private final CookieUtil cookieUtil;

  /**
   * 사용자 정보(프로필 이미지 포함)를 수정하고 성공 응답을 반환한다.
   *
   * <p>요청 웹 DTO를 애플리케이션 DTO로 변환한 뒤 해당 사용자의 정보를 수정하도록 유스케이스에 위임한다.
   *
   * @param userId 수정 대상 사용자 식별자
   * @param profileImageFile 업로드된 프로필 이미지(없을 수 있음)
   * @param webRequest 클라이언트에서 전달된 수정 요청 데이터
   * @return HTTP 200 응답의 SuccessResponse<Void>로, UserSuccessStatus.OK_MODIFY_USER_INFO 를 포함한다
   */
  @Override
  public ResponseEntity<SuccessResponse<Void>> modifyUserInfo(
      Long userId, MultipartFile profileImageFile, ModifyUserInfoWebRequest webRequest) {
    Instant startTime = LoggerFactory.api().logRequest("[ModifyUserInfo] 회원정보 수정 API 요청 시작");

    try {
      ModifyUserInfoRequest requestDto = userCommandWebMapper.toApplicationDto(webRequest);
      modifyUserInfoUseCase.modifyUserInfo(userId, profileImageFile, requestDto);
    } finally {
      LoggerFactory.api().logResponse("[ModifyUserInfo] 회원정보 수정 API 응답 완료", startTime);
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body(SuccessResponse.of(UserSuccessStatus.OK_MODIFY_USER_INFO));
  }

  /**
   * 회원 탈퇴를 수행하고 성공 응답을 반환합니다.
   *
   * <p>요청된 사용자 ID로 회원 탈퇴 유스케이스를 호출합니다.
   *
   * @param userId 탈퇴할 사용자의 식별자
   * @return HTTP 200 OK와 함께 UserSuccessStatus.OK_WITHDRAW_USER를 담은 표준 성공 응답
   */
  @Override
  public ResponseEntity<SuccessResponse<Void>> withdrawUser(Long userId) {
    Instant startTime = LoggerFactory.api().logRequest("[WithdrawUser] 회원 탈퇴 API 요청 시작");

    try {
      withdrawUserUseCase.withdrawUser(userId);
    } finally {
      LoggerFactory.api().logResponse("[WithdrawUser] 회원 탈퇴 API 응답 완료", startTime);
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body(SuccessResponse.of(UserSuccessStatus.OK_WITHDRAW_USER));
  }

  /**
   * 사용자 로그아웃을 수행하고 표준 성공 응답을 반환한다.
   *
   * <p>지정된 사용자의 리프레시 토큰을 무효화하도록 로그아웃 처리를 use case에 위임하고, 모든 인증 관련 쿠키를 삭제한 후 HTTP 200과
   * UserSuccessStatus.OK_LOGOUT을 담은 SuccessResponse<Void>를 반환한다.
   *
   * @param userId 로그아웃 대상 사용자의 식별자
   * @param refreshToken 무효화할 리프레시 토큰 (null일 수 있음)
   * @param request HTTP 요청 객체 (쿠키 삭제용)
   * @param response HTTP 응답 객체 (쿠키 삭제용)
   * @return HTTP 200과 성공 상태를 포함한 SuccessResponse<Void>
   */
  @Override
  public ResponseEntity<SuccessResponse<Void>> logout(
      Long userId, String refreshToken, HttpServletRequest request, HttpServletResponse response) {
    Instant startTime = LoggerFactory.api().logRequest("[Logout] 회원 로그아웃 API 요청 시작");

    try {
      logoutUserUseCase.logout(userId, refreshToken);
      // 모든 인증 관련 쿠키 삭제
      cookieUtil.deleteAllAuthCookies(request, response);
    } finally {
      LoggerFactory.api().logResponse("[Logout] 회원 로그아웃 API 응답 완료", startTime);
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body(SuccessResponse.of(UserSuccessStatus.OK_LOGOUT));
  }
}
