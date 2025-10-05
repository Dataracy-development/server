package com.dataracy.modules.user.adapter.web.api.read;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.adapter.web.mapper.read.OtherUserReadWebMapper;
import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserDataWebResponse;
import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserInfoWebResponse;
import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserProjectWebResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserDataResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserInfoResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserProjectResponse;
import com.dataracy.modules.user.application.port.in.query.extractor.GetOtherUserInfoUseCase;
import com.dataracy.modules.user.domain.status.UserSuccessStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OtherUserReadController implements OtherUserReadApi {
  private final OtherUserReadWebMapper otherUserReadWebMapper;

  private final GetOtherUserInfoUseCase getOtherUserInfoUseCase;

  /**
   * 타인(다른 사용자)의 공개 회원 정보를 조회하여 성공 응답을 반환한다.
   *
   * <p>요청을 받아 도메인 유스케이스에서 사용자 정보를 조회한 뒤 웹 응답 DTO로 변환하여 HTTP 200과 함께 SuccessResponse로 감싸 반환한다.
   * 유스케이스에서 발생한 예외는 전파되어 전역 예외 처리기에 의해 처리된다.
   *
   * @param userId 조회할 타인 회원의 ID
   * @return HTTP 200과 함께 UserSuccessStatus.OK_GET_OTHER_USER_INFO 및 GetOtherUserInfoWebResponse를
   *     포함한 SuccessResponse
   */
  @Override
  public ResponseEntity<SuccessResponse<GetOtherUserInfoWebResponse>> getOtherUserInfo(
      Long userId) {
    Instant startTime = LoggerFactory.api().logRequest("[GetOtherUserInfo] 타인 회원정보 조회 API 요청 시작");
    GetOtherUserInfoWebResponse webResponse;

    try {
      GetOtherUserInfoResponse responseDto = getOtherUserInfoUseCase.getOtherUserInfo(userId);
      webResponse = otherUserReadWebMapper.toWebDto(responseDto);
    } finally {
      LoggerFactory.api().logResponse("[GetOtherUserInfo] 타인 회원정보 조회 API 응답 완료", startTime);
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body(SuccessResponse.of(UserSuccessStatus.OK_GET_OTHER_USER_INFO, webResponse));
  }

  /**
   * 타인(다른 사용자)이 업로드한 프로젝트 목록을 페이지 단위로 조회하여 반환한다.
   *
   * <p>요청을 처리한 결과를 GetOtherUserProjectWebResponse의 Page로 매핑해 SuccessResponse에 담아 HTTP 200(OK)로
   * 응답한다.
   *
   * @param userId 조회 대상 사용자의 식별자
   * @param pageable 페이지 번호·크기·정렬을 포함한 페이징 정보
   * @return HTTP 200과 함께 페이지화된 프로젝트 목록을 담은 SuccessResponse를 포함한 ResponseEntity
   */
  @Override
  public ResponseEntity<SuccessResponse<Page<GetOtherUserProjectWebResponse>>> getOtherProjects(
      Long userId, Pageable pageable) {
    Instant startTime =
        LoggerFactory.api().logRequest("[GetOtherProjects] 타인이 업로드한 프로젝트 목록 추가 조회 API 요청 시작");
    Page<GetOtherUserProjectWebResponse> webResponse;

    try {
      Page<GetOtherUserProjectResponse> responseDto =
          getOtherUserInfoUseCase.getOtherExtraProjects(userId, pageable);
      webResponse = responseDto.map(otherUserReadWebMapper::toWebDto);
    } finally {
      LoggerFactory.api()
          .logResponse("[GetOtherProjects] 타인이 업로드한 프로젝트 목록 추가 조회 API 응답 완료", startTime);
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body(SuccessResponse.of(UserSuccessStatus.OK_GET_OTHER_EXTRA_PROJECTS, webResponse));
  }

  /**
   * 다른 사용자가 업로드한 데이터셋의 추가 페이지를 조회하여 페이징된 웹 응답으로 반환한다.
   *
   * <p>요청된 사용자(userId)가 업로드한 데이터셋을 use case에서 조회하고, 각 도메인 응답을 GetOtherUserDataWebResponse로 매핑한 후
   * SuccessResponse(UserSuccessStatus.OK_GET_OTHER_EXTRA_DATASETS) 로 감싸서 HTTP 200 응답으로 반환한다.
   *
   * @param userId 조회 대상 다른 사용자의 ID
   * @param pageable 페이징 및 정렬 정보
   * @return HTTP 200에 래핑된 SuccessResponse에 Page<GetOtherUserDataWebResponse>를 포함한 ResponseEntity
   */
  @Override
  public ResponseEntity<SuccessResponse<Page<GetOtherUserDataWebResponse>>> getOtherDataSets(
      Long userId, Pageable pageable) {
    Instant startTime =
        LoggerFactory.api().logRequest("[GetOtherDataSets] 타인이 업로드한 데이터셋 목록 추가 조회 API 요청 시작");
    Page<GetOtherUserDataWebResponse> webResponse;

    try {
      Page<GetOtherUserDataResponse> responseDto =
          getOtherUserInfoUseCase.getOtherExtraDataSets(userId, pageable);
      webResponse = responseDto.map(otherUserReadWebMapper::toWebDto);
    } finally {
      LoggerFactory.api()
          .logResponse("[GetOtherDataSets] 타인이 업로드한 데이터셋 목록 추가 조회 API 응답 완료", startTime);
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body(SuccessResponse.of(UserSuccessStatus.OK_GET_OTHER_EXTRA_DATASETS, webResponse));
  }
}
