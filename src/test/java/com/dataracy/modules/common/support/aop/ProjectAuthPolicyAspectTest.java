package com.dataracy.modules.common.support.aop;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.support.annotation.AuthorizationProjectEdit;
import com.dataracy.modules.project.application.port.in.query.extractor.FindUserIdIncludingDeletedUseCase;
import com.dataracy.modules.project.application.port.in.query.extractor.FindUserIdUseCase;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectAuthPolicyAspectTest {

    @Mock
    private FindUserIdUseCase findUserIdUseCase;

    @Mock
    private FindUserIdIncludingDeletedUseCase findUserIdIncludingDeletedUseCase;

    @Mock
    private AuthorizationProjectEdit annotation;

    @InjectMocks
    private ProjectAuthPolicyAspect projectAuthPolicyAspect;

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
    @DisplayName("checkProjectEditPermission - 일반 편집 권한 검증 성공")
    void checkProjectEditPermission_EditPermission_Success() {
        // given
        Long projectId = 1L;
        Long authenticatedUserId = 100L;
        Long ownerId = 100L;

        when(annotation.restore()).thenReturn(false);
        securityContextProviderMock.when(SecurityContextProvider::getAuthenticatedUserId).thenReturn(authenticatedUserId);
        when(findUserIdUseCase.findUserIdByProjectId(projectId)).thenReturn(ownerId);
        mockLoggerFactory();

        // when & then
        projectAuthPolicyAspect.checkProjectEditPermission(annotation, projectId);

        // verify
        verify(findUserIdUseCase).findUserIdByProjectId(projectId);
    }

    @Test
    @DisplayName("checkProjectEditPermission - 복원 권한 검증 성공")
    void checkProjectEditPermission_RestorePermission_Success() {
        // given
        Long projectId = 1L;
        Long authenticatedUserId = 100L;
        Long ownerId = 100L;

        when(annotation.restore()).thenReturn(true);
        securityContextProviderMock.when(SecurityContextProvider::getAuthenticatedUserId).thenReturn(authenticatedUserId);
        when(findUserIdIncludingDeletedUseCase.findUserIdIncludingDeleted(projectId)).thenReturn(ownerId);
        mockLoggerFactory();

        // when & then
        projectAuthPolicyAspect.checkProjectEditPermission(annotation, projectId);

        // verify
        verify(findUserIdIncludingDeletedUseCase).findUserIdIncludingDeleted(projectId);
    }

    @Test
    @DisplayName("checkProjectEditPermission - 일반 편집 권한 검증 실패 - 작성자 불일치")
    void checkProjectEditPermission_EditPermission_Fail_OwnerMismatch() {
        // given
        Long projectId = 1L;
        Long authenticatedUserId = 100L;
        Long ownerId = 200L;

        when(annotation.restore()).thenReturn(false);
        securityContextProviderMock.when(SecurityContextProvider::getAuthenticatedUserId).thenReturn(authenticatedUserId);
        when(findUserIdUseCase.findUserIdByProjectId(projectId)).thenReturn(ownerId);
        mockLoggerFactory();

        // when & then
        assertThatThrownBy(() -> projectAuthPolicyAspect.checkProjectEditPermission(annotation, projectId))
                .isInstanceOf(ProjectException.class)
                .hasFieldOrPropertyWithValue("errorCode", ProjectErrorStatus.NOT_MATCH_CREATOR);

        // verify
        verify(findUserIdUseCase).findUserIdByProjectId(projectId);
    }

    @Test
    @DisplayName("checkProjectEditPermission - 복원 권한 검증 실패 - 작성자 불일치")
    void checkProjectEditPermission_RestorePermission_Fail_OwnerMismatch() {
        // given
        Long projectId = 1L;
        Long authenticatedUserId = 100L;
        Long ownerId = 200L;

        when(annotation.restore()).thenReturn(true);
        securityContextProviderMock.when(SecurityContextProvider::getAuthenticatedUserId).thenReturn(authenticatedUserId);
        when(findUserIdIncludingDeletedUseCase.findUserIdIncludingDeleted(projectId)).thenReturn(ownerId);
        mockLoggerFactory();

        // when & then
        assertThatThrownBy(() -> projectAuthPolicyAspect.checkProjectEditPermission(annotation, projectId))
                .isInstanceOf(ProjectException.class)
                .hasFieldOrPropertyWithValue("errorCode", ProjectErrorStatus.NOT_MATCH_CREATOR);

        // verify
        verify(findUserIdIncludingDeletedUseCase).findUserIdIncludingDeleted(projectId);
    }

    @Test
    @DisplayName("checkProjectEditPermission - 로깅 검증 - 일반 편집 실패")
    void checkProjectEditPermission_EditPermission_LoggingVerification() {
        // given
        Long projectId = 1L;
        Long authenticatedUserId = 100L;
        Long ownerId = 200L;

        when(annotation.restore()).thenReturn(false);
        securityContextProviderMock.when(SecurityContextProvider::getAuthenticatedUserId).thenReturn(authenticatedUserId);
        when(findUserIdUseCase.findUserIdByProjectId(projectId)).thenReturn(ownerId);
        
        var loggerCommon = mock(com.dataracy.modules.common.logging.CommonLogger.class);
        loggerFactoryMock.when(() -> LoggerFactory.common()).thenReturn(loggerCommon);
        lenient().doNothing().when(loggerCommon).logWarning(anyString(), anyString());

        // when
        assertThatThrownBy(() -> projectAuthPolicyAspect.checkProjectEditPermission(annotation, projectId))
                .isInstanceOf(ProjectException.class);

        // then - 로깅 검증
        verify(loggerCommon).logWarning("Project", "프로젝트 작성자만 수정 및 삭제할 수 있습니다.");
    }

    @Test
    @DisplayName("checkProjectEditPermission - 로깅 검증 - 복원 실패")
    void checkProjectEditPermission_RestorePermission_LoggingVerification() {
        // given
        Long projectId = 1L;
        Long authenticatedUserId = 100L;
        Long ownerId = 200L;

        when(annotation.restore()).thenReturn(true);
        securityContextProviderMock.when(SecurityContextProvider::getAuthenticatedUserId).thenReturn(authenticatedUserId);
        when(findUserIdIncludingDeletedUseCase.findUserIdIncludingDeleted(projectId)).thenReturn(ownerId);
        
        var loggerCommon = mock(com.dataracy.modules.common.logging.CommonLogger.class);
        loggerFactoryMock.when(() -> LoggerFactory.common()).thenReturn(loggerCommon);
        lenient().doNothing().when(loggerCommon).logWarning(anyString(), anyString());

        // when
        assertThatThrownBy(() -> projectAuthPolicyAspect.checkProjectEditPermission(annotation, projectId))
                .isInstanceOf(ProjectException.class);

        // then - 로깅 검증
        verify(loggerCommon).logWarning("Project", "프로젝트 작성자만 복원할 수 있습니다.");
    }

    @Test
    @DisplayName("checkProjectEditPermission - 동일한 사용자 ID 검증")
    void checkProjectEditPermission_SameUserId_Success() {
        // given
        Long projectId = 1L;
        Long userId = 100L;

        when(annotation.restore()).thenReturn(false);
        securityContextProviderMock.when(SecurityContextProvider::getAuthenticatedUserId).thenReturn(userId);
        when(findUserIdUseCase.findUserIdByProjectId(projectId)).thenReturn(userId);
        mockLoggerFactory();

        // when & then
        projectAuthPolicyAspect.checkProjectEditPermission(annotation, projectId);

        // verify
        verify(findUserIdUseCase).findUserIdByProjectId(projectId);
    }

    @Test
    @DisplayName("checkProjectEditPermission - null 값 처리")
    void checkProjectEditPermission_NullValues_ThrowsException() {
        // given
        Long projectId = 1L;
        Long authenticatedUserId = 100L;
        Long ownerId = null;

        when(annotation.restore()).thenReturn(false);
        securityContextProviderMock.when(SecurityContextProvider::getAuthenticatedUserId).thenReturn(authenticatedUserId);
        when(findUserIdUseCase.findUserIdByProjectId(projectId)).thenReturn(ownerId);
        mockLoggerFactory();

        // when & then
        assertThatThrownBy(() -> projectAuthPolicyAspect.checkProjectEditPermission(annotation, projectId))
                .isInstanceOf(NullPointerException.class);

        // verify
        verify(findUserIdUseCase).findUserIdByProjectId(projectId);
    }

    private void mockLoggerFactory() {
        var loggerCommon = mock(com.dataracy.modules.common.logging.CommonLogger.class);
        loggerFactoryMock.when(() -> LoggerFactory.common()).thenReturn(loggerCommon);
        lenient().doNothing().when(loggerCommon).logWarning(anyString(), anyString());
    }
}
