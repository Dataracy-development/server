package com.dataracy.modules.like.adapter.web.api;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.like.adapter.web.mapper.LikeWebMapper;
import com.dataracy.modules.like.adapter.web.request.TargetLikeWebRequest;
import com.dataracy.modules.like.application.dto.request.TargetLikeRequest;
import com.dataracy.modules.like.application.port.in.command.LikeTargetUseCase;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.dataracy.modules.like.domain.status.LikeSuccessStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class LikeController implements LikeApi {
  private final LikeWebMapper likeWebMapper;

  private final LikeTargetUseCase likeTargetUseCase;

  /**
   * 사용자가 프로젝트 또는 댓글에 대해 좋아요 또는 좋아요 취소 요청을 수행할 때, 해당 요청을 처리하고 결과에 따라 성공 상태를 반환합니다.
   *
   * @param userId 좋아요 또는 좋아요 취소를 수행하는 사용자 ID
   * @param webRequest 좋아요 대상 및 이전 좋아요 여부를 포함한 웹 요청 객체
   * @return 좋아요 또는 좋아요 취소 결과에 따라 적절한 성공 상태가 포함된 HTTP 200 OK 응답
   */
  @Override
  public ResponseEntity<SuccessResponse<Void>> modifyTargetLike(
      Long userId, TargetLikeWebRequest webRequest) {
    Instant startTime =
        LoggerFactory.api().logRequest("[ModifyTargetLike] 타겟에 대한 좋아요 처리 API 요청 시작");
    TargetLikeRequest requestDto;
    TargetType targetType;

    try {
      requestDto = likeWebMapper.toApplicationDto(webRequest);
      targetType = likeTargetUseCase.likeTarget(userId, requestDto);
    } finally {
      LoggerFactory.api().logResponse("[ModifyTargetLike] 타겟에 대한 좋아요 처리 API 응답 완료", startTime);
    }

    LoggerFactory.api().logResponse("[ModifyTargetLike] 타겟에 대한 좋아요 처리 API 응답 완료", startTime);
    if (requestDto.previouslyLiked()) {
      return switch (targetType) {
        case PROJECT ->
            ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(LikeSuccessStatus.UNLIKE_PROJECT));
        case COMMENT ->
            ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(LikeSuccessStatus.UNLIKE_COMMENT));
      };
    } else {
      return switch (targetType) {
        case PROJECT ->
            ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(LikeSuccessStatus.LIKE_PROJECT));
        case COMMENT ->
            ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(LikeSuccessStatus.LIKE_COMMENT));
      };
    }
  }
}
