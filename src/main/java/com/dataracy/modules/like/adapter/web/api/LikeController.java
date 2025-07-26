package com.dataracy.modules.like.adapter.web.api;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.like.adapter.web.mapper.LikeWebMapper;
import com.dataracy.modules.like.adapter.web.request.TargetLikeWebRequest;
import com.dataracy.modules.like.application.dto.request.TargetLikeRequest;
import com.dataracy.modules.like.application.port.in.TargetLikeUseCase;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.dataracy.modules.like.domain.status.LikeSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController implements LikeApi {
    private final LikeWebMapper likeWebMapper;

    private final TargetLikeUseCase targetLikeUseCase;

    @Override
    public ResponseEntity<SuccessResponse<Void>> modifyTargetLike(Long userId, TargetLikeWebRequest webRequest) {
        TargetLikeRequest requestDto = likeWebMapper.toApplicationDto(webRequest);
        TargetType targetType = TargetType.of(requestDto.targetType());
        targetLikeUseCase.targetLike(userId, requestDto, targetType);

        if (!requestDto.isLiked()) {
            return switch (targetType) {
                case PROJECT -> ResponseEntity.status(HttpStatus.OK)
                        .body(SuccessResponse.of(LikeSuccessStatus.LIKE_PROJECT));
                case COMMENT -> ResponseEntity.status(HttpStatus.OK)
                        .body(SuccessResponse.of(LikeSuccessStatus.LIKE_COMMENT));
            };
        } else {
            return switch (targetType) {
                case PROJECT -> ResponseEntity.status(HttpStatus.OK)
                        .body(SuccessResponse.of(LikeSuccessStatus.UNLIKE_PROJECT));
                case COMMENT -> ResponseEntity.status(HttpStatus.OK)
                        .body(SuccessResponse.of(LikeSuccessStatus.UNLIKE_COMMENT));
            };
        }
    }
}
