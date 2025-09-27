package com.dataracy.modules.common.support.aop;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.support.annotation.AuthorizationDataEdit;
import com.dataracy.modules.dataset.application.port.in.query.extractor.FindUserIdIncludingDeletedUseCase;
import com.dataracy.modules.dataset.application.port.in.query.extractor.FindUserIdUseCase;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataAuthPolicyAspectTest {

    @Mock
    private FindUserIdUseCase findUserIdUseCase;

    @Mock
    private FindUserIdIncludingDeletedUseCase findUserIdIncludingDeletedUseCase;

    @Mock
    private AuthorizationDataEdit annotation;

    @InjectMocks
    private DataAuthPolicyAspect dataAuthPolicyAspect;

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
    @DisplayName("checkDataEditPermission - 일반 편집 권한 검증 성공")
    void checkDataEditPermission_EditPermission_Success() {
        // given
        Long dataId = 1L;
        Long authenticatedUserId = 100L;
        Long ownerId = 100L;

        when(annotation.restore()).thenReturn(false);
        securityContextProviderMock.when(SecurityContextProvider::getAuthenticatedUserId).thenReturn(authenticatedUserId);
        when(findUserIdUseCase.findUserIdByDataId(dataId)).thenReturn(ownerId);
        mockLoggerFactory();

        // when & then
        dataAuthPolicyAspect.checkDataEditPermission(annotation, dataId);

        // verify
        verify(findUserIdUseCase).findUserIdByDataId(dataId);
    }

    @Test
    @DisplayName("checkDataEditPermission - 복원 권한 검증 성공")
    void checkDataEditPermission_RestorePermission_Success() {
        // given
        Long dataId = 1L;
        Long authenticatedUserId = 100L;
        Long ownerId = 100L;

        when(annotation.restore()).thenReturn(true);
        securityContextProviderMock.when(SecurityContextProvider::getAuthenticatedUserId).thenReturn(authenticatedUserId);
        when(findUserIdIncludingDeletedUseCase.findUserIdIncludingDeleted(dataId)).thenReturn(ownerId);
        mockLoggerFactory();

        // when & then
        dataAuthPolicyAspect.checkDataEditPermission(annotation, dataId);

        // verify
        verify(findUserIdIncludingDeletedUseCase).findUserIdIncludingDeleted(dataId);
    }

    @Test
    @DisplayName("checkDataEditPermission - 일반 편집 권한 검증 실패 - 작성자 불일치")
    void checkDataEditPermission_EditPermission_Fail_OwnerMismatch() {
        // given
        Long dataId = 1L;
        Long authenticatedUserId = 100L;
        Long ownerId = 200L;

        when(annotation.restore()).thenReturn(false);
        securityContextProviderMock.when(SecurityContextProvider::getAuthenticatedUserId).thenReturn(authenticatedUserId);
        when(findUserIdUseCase.findUserIdByDataId(dataId)).thenReturn(ownerId);
        mockLoggerFactory();

        // when & then
        assertThatThrownBy(() -> dataAuthPolicyAspect.checkDataEditPermission(annotation, dataId))
                .isInstanceOf(DataException.class)
                .hasFieldOrPropertyWithValue("errorCode", DataErrorStatus.NOT_MATCH_CREATOR);

        // verify
        verify(findUserIdUseCase).findUserIdByDataId(dataId);
    }

    @Test
    @DisplayName("checkDataEditPermission - 복원 권한 검증 실패 - 작성자 불일치")
    void checkDataEditPermission_RestorePermission_Fail_OwnerMismatch() {
        // given
        Long dataId = 1L;
        Long authenticatedUserId = 100L;
        Long ownerId = 200L;

        when(annotation.restore()).thenReturn(true);
        securityContextProviderMock.when(SecurityContextProvider::getAuthenticatedUserId).thenReturn(authenticatedUserId);
        when(findUserIdIncludingDeletedUseCase.findUserIdIncludingDeleted(dataId)).thenReturn(ownerId);
        mockLoggerFactory();

        // when & then
        assertThatThrownBy(() -> dataAuthPolicyAspect.checkDataEditPermission(annotation, dataId))
                .isInstanceOf(DataException.class)
                .hasFieldOrPropertyWithValue("errorCode", DataErrorStatus.NOT_MATCH_CREATOR);

        // verify
        verify(findUserIdIncludingDeletedUseCase).findUserIdIncludingDeleted(dataId);
    }

    @Test
    @DisplayName("checkDataEditPermission - 로깅 검증 - 일반 편집 실패")
    void checkDataEditPermission_EditPermission_LoggingVerification() {
        // given
        Long dataId = 1L;
        Long authenticatedUserId = 100L;
        Long ownerId = 200L;

        when(annotation.restore()).thenReturn(false);
        securityContextProviderMock.when(SecurityContextProvider::getAuthenticatedUserId).thenReturn(authenticatedUserId);
        when(findUserIdUseCase.findUserIdByDataId(dataId)).thenReturn(ownerId);
        
        var loggerCommon = mock(com.dataracy.modules.common.logging.CommonLogger.class);
        loggerFactoryMock.when(() -> LoggerFactory.common()).thenReturn(loggerCommon);
        lenient().doNothing().when(loggerCommon).logWarning(anyString(), anyString());

        // when
        assertThatThrownBy(() -> dataAuthPolicyAspect.checkDataEditPermission(annotation, dataId))
                .isInstanceOf(DataException.class);

        // then - 로깅 검증
        verify(loggerCommon).logWarning("Data", "데이터셋 작성자만 수정 및 삭제할 수 있습니다.");
    }

    @Test
    @DisplayName("checkDataEditPermission - 로깅 검증 - 복원 실패")
    void checkDataEditPermission_RestorePermission_LoggingVerification() {
        // given
        Long dataId = 1L;
        Long authenticatedUserId = 100L;
        Long ownerId = 200L;

        when(annotation.restore()).thenReturn(true);
        securityContextProviderMock.when(SecurityContextProvider::getAuthenticatedUserId).thenReturn(authenticatedUserId);
        when(findUserIdIncludingDeletedUseCase.findUserIdIncludingDeleted(dataId)).thenReturn(ownerId);
        
        var loggerCommon = mock(com.dataracy.modules.common.logging.CommonLogger.class);
        loggerFactoryMock.when(() -> LoggerFactory.common()).thenReturn(loggerCommon);
        lenient().doNothing().when(loggerCommon).logWarning(anyString(), anyString());

        // when
        assertThatThrownBy(() -> dataAuthPolicyAspect.checkDataEditPermission(annotation, dataId))
                .isInstanceOf(DataException.class);

        // then - 로깅 검증
        verify(loggerCommon).logWarning("Data", "데이터셋 작성자만 복원할 수 있습니다.");
    }

    private void mockLoggerFactory() {
        var loggerCommon = mock(com.dataracy.modules.common.logging.CommonLogger.class);
        loggerFactoryMock.when(() -> LoggerFactory.common()).thenReturn(loggerCommon);
        lenient().doNothing().when(loggerCommon).logWarning(anyString(), anyString());
    }
}
