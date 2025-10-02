package com.dataracy.modules.project.application.service.command;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.port.in.validate.ValidateDataUseCase;
import com.dataracy.modules.filestorage.application.port.in.FileCommandUseCase;
import com.dataracy.modules.project.application.dto.request.command.ModifyProjectRequest;
import com.dataracy.modules.project.application.dto.request.command.UploadProjectRequest;
import com.dataracy.modules.project.application.mapper.command.CreateProjectDtoMapper;
import com.dataracy.modules.project.application.port.out.command.create.CreateProjectPort;
import com.dataracy.modules.project.application.port.out.command.delete.DeleteProjectDataPort;
import com.dataracy.modules.project.application.port.out.command.update.UpdateProjectFilePort;
import com.dataracy.modules.project.application.port.out.command.update.UpdateProjectPort;
import com.dataracy.modules.project.application.port.out.indexing.IndexProjectPort;
import com.dataracy.modules.project.application.port.out.query.extractor.ExtractProjectOwnerPort;
import com.dataracy.modules.project.application.port.out.query.read.FindProjectPort;
import com.dataracy.modules.project.application.port.out.query.validate.CheckProjectExistsByIdPort;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.project.domain.model.vo.ValidatedProjectInfo;
import com.dataracy.modules.reference.application.port.in.analysispurpose.GetAnalysisPurposeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUserThumbnailUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUsernameUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ProjectCommandServiceTest {

    @Mock
    private CreateProjectDtoMapper createProjectDtoMapper;
    @Mock
    private IndexProjectPort indexProjectPort;
    @Mock
    private CreateProjectPort createProjectPort;
    @Mock
    private UpdateProjectFilePort updateProjectFilePort;
    @Mock
    private UpdateProjectPort updateProjectPort;
    @Mock
    private DeleteProjectDataPort deleteProjectDataPort;
    @Mock
    private CheckProjectExistsByIdPort checkProjectExistsByIdPort;
    @Mock
    private FindProjectPort findProjectPort;
    @Mock
    private ExtractProjectOwnerPort extractProjectOwnerPort;
    @Mock
    private FindUsernameUseCase findUsernameUseCase;
    @Mock
    private FindUserThumbnailUseCase findUserThumbnailUseCase;
    @Mock
    private FileCommandUseCase fileCommandUseCase;
    @Mock
    private GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;
    @Mock
    private GetAnalysisPurposeLabelFromIdUseCase getAnalysisPurposeLabelFromIdUseCase;
    @Mock
    private GetDataSourceLabelFromIdUseCase getDataSourceLabelFromIdUseCase;
    @Mock
    private GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;
    @Mock
    private ValidateDataUseCase validateDataUseCase;

    @InjectMocks
    private ProjectCommandService projectCommandService;

    private MockedStatic<LoggerFactory> loggerFactoryMock;

    @BeforeEach
    void setUp() {
        loggerFactoryMock = mockStatic(LoggerFactory.class);
        var serviceLogger = mock(LoggerFactory.ServiceLogger.class);
        loggerFactoryMock.when(LoggerFactory::service).thenReturn(serviceLogger);
        given(serviceLogger.logStart(any(), any())).willReturn(Instant.now());
    }

    @Test
    @DisplayName("modifyProject - 프로젝트가 존재하지 않을 때 예외 발생")
    void modifyProject_ShouldThrowExceptionWhenProjectNotFound() {
        // given
        Long userId = 1L;
        Long projectId = 999L;
        ModifyProjectRequest request = new ModifyProjectRequest(
                "Updated Title", "Updated Description", 
                List.of(1L), List.of(1L), List.of(1L), List.of(1L), 
                List.of(1L), null, null
        );

        given(checkProjectExistsByIdPort.checkProjectExistsById(projectId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> projectCommandService.modifyProject(userId, projectId, request))
                .isInstanceOf(RuntimeException.class);

        then(checkProjectExistsByIdPort).should().checkProjectExistsById(projectId);
        then(findProjectPort).should(never()).findProject(projectId);
    }

    @Test
    @DisplayName("modifyProject - 부모 프로젝트가 존재하지 않을 때 예외 발생")
    void modifyProject_ShouldThrowExceptionWhenParentProjectNotFound() {
        // given
        Long userId = 1L;
        Long projectId = 1L;
        Long parentProjectId = 999L;
        ModifyProjectRequest request = new ModifyProjectRequest(
                "Updated Title", "Updated Description", 
                List.of(1L), List.of(1L), List.of(1L), List.of(1L), 
                List.of(1L), parentProjectId, null
        );

        given(checkProjectExistsByIdPort.checkProjectExistsById(projectId)).willReturn(true);
        given(checkProjectExistsByIdPort.checkProjectExistsById(parentProjectId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> projectCommandService.modifyProject(userId, projectId, request))
                .isInstanceOf(RuntimeException.class);

        then(checkProjectExistsByIdPort).should().checkProjectExistsById(projectId);
        then(checkProjectExistsByIdPort).should().checkProjectExistsById(parentProjectId);
    }

    @Test
    @DisplayName("uploadProject - 부모 프로젝트가 존재하지 않을 때 예외 발생")
    void uploadProject_ShouldThrowExceptionWhenParentProjectNotFound() {
        // given
        Long userId = 1L;
        Long parentProjectId = 999L;
        MultipartFile thumbnailFile = mock(MultipartFile.class);
        UploadProjectRequest request = new UploadProjectRequest(
                "Test Project", "Test Description", 
                List.of(1L), List.of(1L), List.of(1L), List.of(1L), 
                List.of(1L), parentProjectId, null
        );

        given(checkProjectExistsByIdPort.checkProjectExistsById(parentProjectId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> projectCommandService.uploadProject(userId, thumbnailFile, request))
                .isInstanceOf(RuntimeException.class);

        then(checkProjectExistsByIdPort).should().checkProjectExistsById(parentProjectId);
    }

    @Test
    @DisplayName("uploadProject - 데이터 ID가 유효하지 않을 때 예외 발생")
    void uploadProject_ShouldThrowExceptionWhenDataIdInvalid() {
        // given
        Long userId = 1L;
        MultipartFile thumbnailFile = mock(MultipartFile.class);
        UploadProjectRequest request = new UploadProjectRequest(
                "Test Project", "Test Description", 
                List.of(1L), List.of(1L), List.of(1L), List.of(1L), 
                List.of(999L), null, null
        );

        // when & then
        assertThatThrownBy(() -> projectCommandService.uploadProject(userId, thumbnailFile, request))
                .isInstanceOf(RuntimeException.class);
    }
}
