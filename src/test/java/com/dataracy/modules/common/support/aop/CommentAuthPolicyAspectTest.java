package com.dataracy.modules.common.support.aop;

import com.dataracy.modules.comment.application.port.in.query.extractor.FindUserIdByCommentIdUseCase;
import com.dataracy.modules.comment.domain.exception.CommentException;
import com.dataracy.modules.comment.domain.status.CommentErrorStatus;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.support.annotation.AuthorizationCommentEdit;
import com.dataracy.modules.security.handler.SecurityContextProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommentAuthPolicyAspectTest {

    @Mock
    private FindUserIdByCommentIdUseCase findUserIdByCommentIdUseCase;

    @Mock
    private AuthorizationCommentEdit annotation;

    @InjectMocks
    private CommentAuthPolicyAspect commentAuthPolicyAspect;

    private MockedStatic<SecurityContextProvider> securityContextProviderMock;
    private MockedStatic<LoggerFactory> loggerFactoryMock;

    @BeforeEach
    void setUp() {
        securityContextProviderMock = mockStatic(SecurityContextProvider.class);
        loggerFactoryMock = mockStatic(LoggerFactory.class);
    }

    @AfterEach
    void tearDown() {
        if (securityContextProviderMock != null) {
            securityContextProviderMock.close();
        }
        if (loggerFactoryMock != null) {
            loggerFactoryMock.close();
        }
    }

    @Test
    @DisplayName("checkCommentEditPermission - 댓글 편집 권한 검증 성공")
    void checkCommentEditPermission_Success() {
        // given
        Long commentId = 1L;
        Long authenticatedUserId = 100L;
        Long ownerId = 100L;

        securityContextProviderMock.when(SecurityContextProvider::getAuthenticatedUserId).thenReturn(authenticatedUserId);
        when(findUserIdByCommentIdUseCase.findUserIdByCommentId(commentId)).thenReturn(ownerId);
        mockLoggerFactory();

        // when & then
        commentAuthPolicyAspect.checkCommentEditPermission(annotation, commentId);

        // verify
        then(findUserIdByCommentIdUseCase).should().findUserIdByCommentId(commentId);
    }

    @Test
    @DisplayName("checkCommentEditPermission - 댓글 편집 권한 검증 실패 - 작성자 불일치")
    void checkCommentEditPermission_Fail_OwnerMismatch() {
        // given
        Long commentId = 1L;
        Long authenticatedUserId = 100L;
        Long ownerId = 200L;

        securityContextProviderMock.when(SecurityContextProvider::getAuthenticatedUserId).thenReturn(authenticatedUserId);
        when(findUserIdByCommentIdUseCase.findUserIdByCommentId(commentId)).thenReturn(ownerId);
        mockLoggerFactory();

        // when
        CommentException exception = catchThrowableOfType(() -> commentAuthPolicyAspect.checkCommentEditPermission(annotation, commentId), CommentException.class);
        
        // then
        assertAll(
                () -> assertThat(exception).isNotNull(),
                () -> assertThat(exception.getErrorCode()).isEqualTo(CommentErrorStatus.NOT_MATCH_CREATOR)
        );

        // verify
        then(findUserIdByCommentIdUseCase).should().findUserIdByCommentId(commentId);
    }

    @Test
    @DisplayName("checkCommentEditPermission - 로깅 검증")
    void checkCommentEditPermission_LoggingVerification() {
        // given
        Long commentId = 1L;
        Long authenticatedUserId = 100L;
        Long ownerId = 200L;

        securityContextProviderMock.when(SecurityContextProvider::getAuthenticatedUserId).thenReturn(authenticatedUserId);
        when(findUserIdByCommentIdUseCase.findUserIdByCommentId(commentId)).thenReturn(ownerId);
        
        var loggerCommon = mock(com.dataracy.modules.common.logging.CommonLogger.class);
        loggerFactoryMock.when(LoggerFactory::common).thenReturn(loggerCommon);
        doNothing().when(loggerCommon).logWarning(anyString(), anyString());

        // when
        CommentException exception = catchThrowableOfType(() -> commentAuthPolicyAspect.checkCommentEditPermission(annotation, commentId), CommentException.class);
        
        // then
        assertThat(exception).isNotNull();
        // 로깅 검증
        verify(loggerCommon).logWarning("Comment", "댓글 작성자만 수정 및 삭제 할 수 있습니다.");
    }

    @Test
    @DisplayName("checkCommentEditPermission - 동일한 사용자 ID 검증")
    void checkCommentEditPermission_SameUserId_Success() {
        // given
        Long commentId = 1L;
        Long userId = 100L;

        securityContextProviderMock.when(SecurityContextProvider::getAuthenticatedUserId).thenReturn(userId);
        when(findUserIdByCommentIdUseCase.findUserIdByCommentId(commentId)).thenReturn(userId);
        mockLoggerFactory();

        // when & then
        commentAuthPolicyAspect.checkCommentEditPermission(annotation, commentId);

        // verify
        then(findUserIdByCommentIdUseCase).should().findUserIdByCommentId(commentId);
    }

    @Test
    @DisplayName("checkCommentEditPermission - null 값 처리")
    void checkCommentEditPermission_NullValues_ThrowsException() {
        // given
        Long commentId = 1L;
        Long authenticatedUserId = 100L;
        Long ownerId = null;

        securityContextProviderMock.when(SecurityContextProvider::getAuthenticatedUserId).thenReturn(authenticatedUserId);
        when(findUserIdByCommentIdUseCase.findUserIdByCommentId(commentId)).thenReturn(ownerId);
        mockLoggerFactory();

        // when
        NullPointerException exception = catchThrowableOfType(() -> commentAuthPolicyAspect.checkCommentEditPermission(annotation, commentId), NullPointerException.class);
        
        // then
        assertThat(exception).isNotNull();

        // verify
        then(findUserIdByCommentIdUseCase).should().findUserIdByCommentId(commentId);
    }

    private void mockLoggerFactory() {
        var loggerCommon = mock(com.dataracy.modules.common.logging.CommonLogger.class);
        loggerFactoryMock.when(LoggerFactory::common).thenReturn(loggerCommon);
        doNothing().when(loggerCommon).logWarning(anyString(), anyString());
    }
}
