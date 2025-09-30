package com.dataracy.modules.project.application.service.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.port.out.query.extractor.ExtractProjectOwnerPort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectExtractServiceTest {

    @Mock
    private ExtractProjectOwnerPort extractProjectOwnerPort;

    @InjectMocks
    private ProjectExtractService service;

    private MockedStatic<LoggerFactory> loggerFactoryMock;
    private com.dataracy.modules.common.logging.ServiceLogger loggerService;

    @BeforeEach
    void setUp() {
        loggerFactoryMock = mockStatic(LoggerFactory.class);
        loggerService = mock(com.dataracy.modules.common.logging.ServiceLogger.class);
        loggerFactoryMock.when(() -> LoggerFactory.service()).thenReturn(loggerService);
        doReturn(Instant.now()).when(loggerService).logStart(anyString(), anyString());
        doNothing().when(loggerService).logSuccess(anyString(), anyString(), any(Instant.class));
    }

    @AfterEach
    void tearDown() {
        if (loggerFactoryMock != null) {
            loggerFactoryMock.close();
        }
    }

    @Nested
    @DisplayName("findUserIdByProjectId 메서드 테스트")
    class FindUserIdByProjectIdTest {

        @Test
        @DisplayName("프로젝트 ID로 사용자 ID 조회 성공 및 로깅 검증")
        void findUserIdByProjectIdSuccess() {
            // given
            Long projectId = 100L;
            Long expectedUserId = 200L;

            given(extractProjectOwnerPort.findUserIdByProjectId(projectId)).willReturn(expectedUserId);

            // when
            Long result = service.findUserIdByProjectId(projectId);

            // then
            assertThat(result).isEqualTo(expectedUserId);

            // 포트 호출 검증
            then(extractProjectOwnerPort).should().findUserIdByProjectId(projectId);

            // 로깅 검증
            then(loggerService).should().logStart(eq("FindUserIdUseCase"),
                    contains("프로젝트 아이디로부터 유저 아이디 조회 서비스 시작 projectId=" + projectId));
            then(loggerService).should().logSuccess(eq("FindUserIdUseCase"),
                    contains("프로젝트 아이디로부터 유저 아이디 조회 서비스 종료 projectId=" + projectId), any(Instant.class));
        }

        @Test
        @DisplayName("존재하지 않는 프로젝트 ID에 대한 조회")
        void findUserIdByProjectIdWithNonExistentProject() {
            // given
            Long projectId = 999L;
            Long nullUserId = null;

            given(extractProjectOwnerPort.findUserIdByProjectId(projectId)).willReturn(nullUserId);

            // when
            Long result = service.findUserIdByProjectId(projectId);

            // then
            assertThat(result).isNull();

            // 포트 호출 검증
            then(extractProjectOwnerPort).should().findUserIdByProjectId(projectId);

            // 로깅 검증
            then(loggerService).should().logStart(eq("FindUserIdUseCase"),
                    contains("프로젝트 아이디로부터 유저 아이디 조회 서비스 시작 projectId=" + projectId));
            then(loggerService).should().logSuccess(eq("FindUserIdUseCase"),
                    contains("프로젝트 아이디로부터 유저 아이디 조회 서비스 종료 projectId=" + projectId), any(Instant.class));
        }

        @Test
        @DisplayName("null 프로젝트 ID에 대한 조회")
        void findUserIdByProjectIdWithNullProjectId() {
            // given
            Long projectId = null;
            Long nullUserId = null;

            given(extractProjectOwnerPort.findUserIdByProjectId(projectId)).willReturn(nullUserId);

            // when
            Long result = service.findUserIdByProjectId(projectId);

            // then
            assertThat(result).isNull();

            // 포트 호출 검증
            then(extractProjectOwnerPort).should().findUserIdByProjectId(projectId);

            // 로깅 검증
            then(loggerService).should().logStart(eq("FindUserIdUseCase"),
                    contains("프로젝트 아이디로부터 유저 아이디 조회 서비스 시작 projectId=" + projectId));
            then(loggerService).should().logSuccess(eq("FindUserIdUseCase"),
                    contains("프로젝트 아이디로부터 유저 아이디 조회 서비스 종료 projectId=" + projectId), any(Instant.class));
        }
    }

    @Nested
    @DisplayName("findUserIdIncludingDeleted 메서드 테스트")
    class FindUserIdIncludingDeletedTest {

        @Test
        @DisplayName("삭제된 프로젝트 포함하여 사용자 ID 조회 성공 및 로깅 검증")
        void findUserIdIncludingDeletedSuccess() {
            // given
            Long projectId = 100L;
            Long expectedUserId = 200L;

            given(extractProjectOwnerPort.findUserIdIncludingDeleted(projectId)).willReturn(expectedUserId);

            // when
            Long result = service.findUserIdIncludingDeleted(projectId);

            // then
            assertThat(result).isEqualTo(expectedUserId);

            // 포트 호출 검증
            then(extractProjectOwnerPort).should().findUserIdIncludingDeleted(projectId);

            // 로깅 검증
            then(loggerService).should().logStart(eq("FindUserIdIncludingDeletedUseCase"),
                    contains("삭제된 프로젝트를 포함하여 프로젝트 아이디로부터 유저 아이디 조회 서비스 시작 projectId=" + projectId));
            then(loggerService).should().logSuccess(eq("FindUserIdIncludingDeletedUseCase"),
                    contains("삭제된 프로젝트를 포함하여 프로젝트 아이디로부터 유저 아이디 조회 서비스 종료 projectId=" + projectId), any(Instant.class));
        }

        @Test
        @DisplayName("삭제된 프로젝트에 대한 조회 성공")
        void findUserIdIncludingDeletedWithDeletedProject() {
            // given
            Long deletedProjectId = 300L;
            Long expectedUserId = 400L;

            given(extractProjectOwnerPort.findUserIdIncludingDeleted(deletedProjectId)).willReturn(expectedUserId);

            // when
            Long result = service.findUserIdIncludingDeleted(deletedProjectId);

            // then
            assertThat(result).isEqualTo(expectedUserId);

            // 포트 호출 검증
            then(extractProjectOwnerPort).should().findUserIdIncludingDeleted(deletedProjectId);

            // 로깅 검증
            then(loggerService).should().logStart(eq("FindUserIdIncludingDeletedUseCase"),
                    contains("삭제된 프로젝트를 포함하여 프로젝트 아이디로부터 유저 아이디 조회 서비스 시작 projectId=" + deletedProjectId));
            then(loggerService).should().logSuccess(eq("FindUserIdIncludingDeletedUseCase"),
                    contains("삭제된 프로젝트를 포함하여 프로젝트 아이디로부터 유저 아이디 조회 서비스 종료 projectId=" + deletedProjectId), any(Instant.class));
        }

        @Test
        @DisplayName("존재하지 않는 프로젝트에 대한 삭제 포함 조회")
        void findUserIdIncludingDeletedWithNonExistentProject() {
            // given
            Long projectId = 999L;
            Long nullUserId = null;

            given(extractProjectOwnerPort.findUserIdIncludingDeleted(projectId)).willReturn(nullUserId);

            // when
            Long result = service.findUserIdIncludingDeleted(projectId);

            // then
            assertThat(result).isNull();

            // 포트 호출 검증
            then(extractProjectOwnerPort).should().findUserIdIncludingDeleted(projectId);

            // 로깅 검증
            then(loggerService).should().logStart(eq("FindUserIdIncludingDeletedUseCase"),
                    contains("삭제된 프로젝트를 포함하여 프로젝트 아이디로부터 유저 아이디 조회 서비스 시작 projectId=" + projectId));
            then(loggerService).should().logSuccess(eq("FindUserIdIncludingDeletedUseCase"),
                    contains("삭제된 프로젝트를 포함하여 프로젝트 아이디로부터 유저 아이디 조회 서비스 종료 projectId=" + projectId), any(Instant.class));
        }

        @Test
        @DisplayName("null 프로젝트 ID에 대한 삭제 포함 조회")
        void findUserIdIncludingDeletedWithNullProjectId() {
            // given
            Long projectId = null;
            Long nullUserId = null;

            given(extractProjectOwnerPort.findUserIdIncludingDeleted(projectId)).willReturn(nullUserId);

            // when
            Long result = service.findUserIdIncludingDeleted(projectId);

            // then
            assertThat(result).isNull();

            // 포트 호출 검증
            then(extractProjectOwnerPort).should().findUserIdIncludingDeleted(projectId);

            // 로깅 검증
            then(loggerService).should().logStart(eq("FindUserIdIncludingDeletedUseCase"),
                    contains("삭제된 프로젝트를 포함하여 프로젝트 아이디로부터 유저 아이디 조회 서비스 시작 projectId=" + projectId));
            then(loggerService).should().logSuccess(eq("FindUserIdIncludingDeletedUseCase"),
                    contains("삭제된 프로젝트를 포함하여 프로젝트 아이디로부터 유저 아이디 조회 서비스 종료 projectId=" + projectId), any(Instant.class));
        }
    }
}