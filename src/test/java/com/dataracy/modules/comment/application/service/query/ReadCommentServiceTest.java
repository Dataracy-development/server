package com.dataracy.modules.comment.application.service.query;

import com.dataracy.modules.comment.application.dto.response.read.FindCommentResponse;
import com.dataracy.modules.comment.application.dto.response.read.FindReplyCommentResponse;
import com.dataracy.modules.comment.application.dto.response.support.FindCommentWithReplyCountResponse;
import com.dataracy.modules.comment.application.mapper.read.FindCommentDtoMapper;
import com.dataracy.modules.comment.application.port.out.query.read.ReadCommentPort;
import com.dataracy.modules.comment.domain.exception.CommentException;
import com.dataracy.modules.comment.domain.model.Comment;
import com.dataracy.modules.comment.domain.status.CommentErrorStatus;
import com.dataracy.modules.like.application.port.in.query.FindTargetIdsUseCase;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUserAuthorLevelIdsUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUserThumbnailUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUsernameUseCase;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;

class ReadCommentServiceTest {

    private ReadCommentPort readCommentPort;
    private FindCommentDtoMapper mapper;
    private FindUsernameUseCase findUsernameUseCase;
    private FindUserThumbnailUseCase findUserThumbnailUseCase;
    private FindUserAuthorLevelIdsUseCase findUserAuthorLevelIdsUseCase;
    private GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;
    private FindTargetIdsUseCase findTargetIdsUseCase;

    private ReadCommentService service;

    @BeforeEach
    void setup() {
        readCommentPort = mock(ReadCommentPort.class);
        mapper = new FindCommentDtoMapper();
        findUsernameUseCase = mock(FindUsernameUseCase.class);
        findUserThumbnailUseCase = mock(FindUserThumbnailUseCase.class);
        findUserAuthorLevelIdsUseCase = mock(FindUserAuthorLevelIdsUseCase.class);
        getAuthorLevelLabelFromIdUseCase = mock(GetAuthorLevelLabelFromIdUseCase.class);
        findTargetIdsUseCase = mock(FindTargetIdsUseCase.class);

        service = new ReadCommentService(
                mapper,
                readCommentPort,
                findUsernameUseCase,
                findUserThumbnailUseCase,
                findUserAuthorLevelIdsUseCase,
                getAuthorLevelLabelFromIdUseCase,
                findTargetIdsUseCase
        );
    }

    @Test
    @DisplayName("댓글 목록 조회 성공")
    void findCommentsSuccess() {
        Comment comment = Comment.of(1L, 1L, 1L, "내용1", null, 0L, LocalDateTime.now());
        Page<FindCommentWithReplyCountResponse> page =
                new PageImpl<>(List.of(new FindCommentWithReplyCountResponse(comment, 2L)));

        given(readCommentPort.findComments(any(), any(Pageable.class))).willReturn(page);
        given(findUsernameUseCase.findUsernamesByIds(any())).willReturn(Map.of(1L, "유저1"));
        given(findUserThumbnailUseCase.findUserThumbnailsByIds(any())).willReturn(Map.of(1L, "thumb.png"));
        given(findUserAuthorLevelIdsUseCase.findUserAuthorLevelIds(any())).willReturn(Map.of(1L, "10"));
        given(getAuthorLevelLabelFromIdUseCase.getLabelsByIds(any())).willReturn(Map.of(10L, "등급"));
        given(findTargetIdsUseCase.findLikedTargetIds(any(), any(), eq(TargetType.COMMENT))).willReturn(List.of());

        Page<FindCommentResponse> result = service.findComments(1L, 1L, Pageable.ofSize(5));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).username()).isEqualTo("유저1");
        assertThat(result.getContent().get(0).content()).isEqualTo("내용1");
    }

    @Test
    @DisplayName("답글 목록 조회 성공")
    void findReplyCommentsSuccess() {
        Comment reply = Comment.of(2L, 1L, 1L, "답글", 1L, 0L, LocalDateTime.now());
        Page<Comment> page = new PageImpl<>(List.of(reply));

        given(readCommentPort.findReplyComments(any(), any(), any(Pageable.class))).willReturn(page);
        given(findUsernameUseCase.findUsernamesByIds(any())).willReturn(Map.of(1L, "유저1"));
        given(findUserThumbnailUseCase.findUserThumbnailsByIds(any())).willReturn(Map.of(1L, "thumb.png"));
        given(findUserAuthorLevelIdsUseCase.findUserAuthorLevelIds(any())).willReturn(Map.of(1L, "10"));
        given(getAuthorLevelLabelFromIdUseCase.getLabelsByIds(any())).willReturn(Map.of(10L, "등급"));
        given(findTargetIdsUseCase.findLikedTargetIds(any(), any(), eq(TargetType.COMMENT))).willReturn(List.of());

        Page<FindReplyCommentResponse> result = service.findReplyComments(1L, 1L, 1L, Pageable.ofSize(5));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).content()).isEqualTo("답글");
    }

    private Comment dummyComment(Long id, Long userId) {
        return Comment.of(id, 1L, userId, "content", null, 0L, LocalDateTime.now());
    }

    @Test
    @DisplayName("댓글 목록 조회 실패 → User 정보 조회 중 예외 발생")
    void findCommentsFailWhenUserInfoThrows() {
        Pageable pageable = PageRequest.of(0, 5);

        Comment c = dummyComment(10L, 100L);
        Page<FindCommentWithReplyCountResponse> page =
                new PageImpl<>(List.of(new FindCommentWithReplyCountResponse(c, 0L)));

        given(readCommentPort.findComments(1L, pageable)).willReturn(page);
        willThrow(new UserException(UserErrorStatus.NOT_FOUND_USER))
                .given(findUsernameUseCase).findUsernamesByIds(any());

        UserException ex = catchThrowableOfType(
                () -> service.findComments(1L, 1L, pageable),
                UserException.class
        );

        assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.NOT_FOUND_USER);
    }

    @Test
    @DisplayName("답글 목록 조회 실패 → ReadCommentPort 가 예외 발생 시 전파")
    void findReplyCommentsFailWhenReadPortThrows() {
        Pageable pageable = PageRequest.of(0, 5);
        willThrow(new CommentException(CommentErrorStatus.NOT_FOUND_COMMENT))
                .given(readCommentPort).findReplyComments(1L, 10L, pageable);

        CommentException ex = catchThrowableOfType(
                () -> service.findReplyComments(1L, 1L, 10L, pageable),
                CommentException.class
        );

        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorStatus.NOT_FOUND_COMMENT);
    }

    @Test
    @DisplayName("답글 목록 조회 실패 → 좋아요 정보 조회 중 예외 발생")
    void findReplyCommentsFailWhenLikeThrows() {
        Pageable pageable = PageRequest.of(0, 5);

        Comment c = dummyComment(20L, 200L);
        Page<Comment> page = new PageImpl<>(List.of(c));

        given(readCommentPort.findReplyComments(1L, 10L, pageable)).willReturn(page);
        willThrow(new RuntimeException("Like Service Down"))
                .given(findTargetIdsUseCase).findLikedTargetIds(1L, List.of(20L), TargetType.COMMENT);

        RuntimeException ex = catchThrowableOfType(
                () -> service.findReplyComments(1L, 1L, 10L, pageable),
                RuntimeException.class
        );

        assertThat(ex).hasMessage("Like Service Down");
    }
}
