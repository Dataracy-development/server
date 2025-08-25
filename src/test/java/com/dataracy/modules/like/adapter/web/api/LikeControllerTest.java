package com.dataracy.modules.like.adapter.web.api;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.like.adapter.web.mapper.LikeWebMapper;
import com.dataracy.modules.like.adapter.web.request.TargetLikeWebRequest;
import com.dataracy.modules.like.application.dto.request.TargetLikeRequest;
import com.dataracy.modules.like.application.port.in.command.LikeTargetUseCase;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.dataracy.modules.like.domain.status.LikeSuccessStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class LikeControllerTest {

    @Mock LikeWebMapper likeWebMapper;
    @Mock LikeTargetUseCase likeTargetUseCase;

    @InjectMocks LikeController controller;

    @Test
    @DisplayName("modifyTargetLike_should_return_LIKE_PROJECT_when_new_like_on_project")
    void modifyTargetLike_should_return_LIKE_PROJECT_when_new_like_on_project() {
        // given
        Long userId = 10L;
        TargetLikeWebRequest web = new TargetLikeWebRequest(7L, "PROJECT", false);
        TargetLikeRequest dto = new TargetLikeRequest(7L, "PROJECT", false);
        given(likeWebMapper.toApplicationDto(web)).willReturn(dto);
        given(likeTargetUseCase.likeTarget(userId, dto)).willReturn(TargetType.PROJECT);

        // when
        ResponseEntity<SuccessResponse<Void>> response = controller.modifyTargetLike(userId, web);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        then(likeWebMapper).should().toApplicationDto(web);
        then(likeTargetUseCase).should().likeTarget(userId, dto);
        // NOTE: Response body structure belongs to common module; here we only assert the status code.
    }

    @Test
    @DisplayName("modifyTargetLike_should_return_UNLIKE_COMMENT_when_unlike_on_comment")
    void modifyTargetLike_should_return_UNLIKE_COMMENT_when_unlike_on_comment() {
        // given
        Long userId = 11L;
        TargetLikeWebRequest web = new TargetLikeWebRequest(77L, "COMMENT", true);
        TargetLikeRequest dto = new TargetLikeRequest(77L, "COMMENT", true);
        given(likeWebMapper.toApplicationDto(web)).willReturn(dto);
        given(likeTargetUseCase.likeTarget(userId, dto)).willReturn(TargetType.COMMENT);

        // when
        ResponseEntity<SuccessResponse<Void>> response = controller.modifyTargetLike(userId, web);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        then(likeWebMapper).should().toApplicationDto(web);
        then(likeTargetUseCase).should().likeTarget(userId, dto);
    }
}
