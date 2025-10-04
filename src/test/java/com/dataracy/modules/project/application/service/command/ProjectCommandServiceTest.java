package com.dataracy.modules.project.application.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.multipart.MultipartFile;

import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.status.CommonErrorStatus;
import com.dataracy.modules.dataset.application.port.in.validate.ValidateDataUseCase;
import com.dataracy.modules.filestorage.application.port.in.FileCommandUseCase;
import com.dataracy.modules.project.application.dto.document.ProjectSearchDocument;
import com.dataracy.modules.project.application.dto.request.command.ModifyProjectRequest;
import com.dataracy.modules.project.application.dto.request.command.UploadProjectRequest;
import com.dataracy.modules.project.application.dto.response.command.UploadProjectResponse;
import com.dataracy.modules.project.application.mapper.command.CreateProjectDtoMapper;
import com.dataracy.modules.project.application.port.out.command.create.CreateProjectPort;
import com.dataracy.modules.project.application.port.out.command.delete.DeleteProjectDataPort;
import com.dataracy.modules.project.application.port.out.command.update.UpdateProjectFilePort;
import com.dataracy.modules.project.application.port.out.command.update.UpdateProjectPort;
import com.dataracy.modules.project.application.port.out.indexing.IndexProjectPort;
import com.dataracy.modules.project.application.port.out.query.extractor.ExtractProjectOwnerPort;
import com.dataracy.modules.project.application.port.out.query.read.FindProjectPort;
import com.dataracy.modules.project.application.port.out.query.validate.CheckProjectExistsByIdPort;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import com.dataracy.modules.reference.application.port.in.analysispurpose.GetAnalysisPurposeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUserThumbnailUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUsernameUseCase;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProjectCommandServiceTest {

  @InjectMocks private ProjectCommandService service;

  @Mock private CreateProjectDtoMapper createProjectDtoMapper;

  @Mock private IndexProjectPort indexProjectPort;

  @Mock private CreateProjectPort createProjectPort;

  @Mock private UpdateProjectFilePort updateProjectFilePort;

  @Mock private UpdateProjectPort updateProjectPort;

  @Mock private DeleteProjectDataPort deleteProjectDataPort;

  @Mock private CheckProjectExistsByIdPort checkProjectExistsByIdPort;

  @Mock private FindProjectPort findProjectPort;

  @Mock private ExtractProjectOwnerPort extractProjectOwnerPort;

  @Mock private FindUsernameUseCase findUsernameUseCase;

  @Mock private FindUserThumbnailUseCase findUserThumbnailUseCase;

  @Mock private FileCommandUseCase fileCommandUseCase;

  @Mock private GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;

  @Mock private GetAnalysisPurposeLabelFromIdUseCase getAnalysisPurposeLabelFromIdUseCase;

  @Mock private GetDataSourceLabelFromIdUseCase getDataSourceLabelFromIdUseCase;

  @Mock private GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;

  @Mock private ValidateDataUseCase validateDataUseCase;

  @Mock private MultipartFile thumbnailFile;

  private Project createSampleProject() {
    return Project.builder()
        .id(1L)
        .title("Test Project")
        .content("Test Description")
        .userId(1L)
        .topicId(1L)
        .analysisPurposeId(1L)
        .dataSourceId(1L)
        .authorLevelId(1L)
        .parentProjectId(null)
        .thumbnailUrl("test-url")
        .createdAt(LocalDateTime.now())
        .build();
  }

  private UploadProjectRequest createSampleUploadRequest() {
    return new UploadProjectRequest(
        "Test Project", 1L, 1L, 1L, 1L, false, null, "Test Description", List.of(1L, 2L));
  }

  private ModifyProjectRequest createSampleModifyRequest() {
    return new ModifyProjectRequest(
        "Modified Project", 1L, 1L, 1L, 1L, false, null, "Modified Description", List.of(1L, 2L));
  }

  @Nested
  @DisplayName("프로젝트 업로드")
  class UploadProject {

    @Test
    @DisplayName("프로젝트 업로드 성공")
    void uploadProjectSuccess() {
      // given
      Long userId = 1L;
      UploadProjectRequest request = createSampleUploadRequest();
      Project project = createSampleProject();
      Project savedProject = createSampleProject();

      // Mock MultipartFile 설정
      given(thumbnailFile.isEmpty()).willReturn(false);
      given(thumbnailFile.getOriginalFilename()).willReturn("test.jpg");
      given(thumbnailFile.getSize()).willReturn(1024L);

      given(getTopicLabelFromIdUseCase.getLabelById(1L)).willReturn("Topic Label");
      given(getAnalysisPurposeLabelFromIdUseCase.getLabelById(1L)).willReturn("Analysis Label");
      given(getDataSourceLabelFromIdUseCase.getLabelById(1L)).willReturn("DataSource Label");
      given(getAuthorLevelLabelFromIdUseCase.getLabelById(1L)).willReturn("Author Label");
      willDoNothing().given(validateDataUseCase).validateData(anyLong());
      given(checkProjectExistsByIdPort.checkProjectExistsById(anyLong())).willReturn(true);
      given(createProjectDtoMapper.toDomain(any(), anyLong(), any())).willReturn(project);
      given(createProjectPort.saveProject(any())).willReturn(savedProject);
      given(fileCommandUseCase.uploadFile(anyString(), any())).willReturn("uploaded-url");
      given(findUsernameUseCase.findUsernameById(anyLong())).willReturn("testuser");
      given(findUserThumbnailUseCase.findUserThumbnailById(anyLong())).willReturn("profile-url");

      // when
      UploadProjectResponse response = service.uploadProject(userId, thumbnailFile, request);

      // then
      assertThat(response.id()).isEqualTo(savedProject.getId());
      then(createProjectPort).should().saveProject(any());
      then(fileCommandUseCase).should().uploadFile(anyString(), any());
      then(indexProjectPort).should().index(any(ProjectSearchDocument.class));
    }

    @Test
    @DisplayName("프로젝트 업로드 실패 - 부모 프로젝트가 존재하지 않음")
    void uploadProjectFailParentProjectNotFound() {
      // given
      Long userId = 1L;
      UploadProjectRequest request =
          new UploadProjectRequest(
              "Test Project",
              1L,
              1L,
              1L,
              1L,
              false,
              999L, // 존재하지 않는 부모 프로젝트 ID
              "Test Description",
              List.of(1L));

      // Mock MultipartFile 설정
      given(thumbnailFile.isEmpty()).willReturn(false);
      given(thumbnailFile.getOriginalFilename()).willReturn("test.jpg");
      given(thumbnailFile.getSize()).willReturn(1024L);

      given(getTopicLabelFromIdUseCase.getLabelById(anyLong())).willReturn("Topic Label");
      given(getAnalysisPurposeLabelFromIdUseCase.getLabelById(anyLong()))
          .willReturn("Analysis Label");
      given(getDataSourceLabelFromIdUseCase.getLabelById(anyLong())).willReturn("DataSource Label");
      given(getAuthorLevelLabelFromIdUseCase.getLabelById(anyLong())).willReturn("Author Label");
      willDoNothing().given(validateDataUseCase).validateData(anyLong());
      given(checkProjectExistsByIdPort.checkProjectExistsById(999L)).willReturn(false);

      // when & then
      ProjectException ex =
          catchThrowableOfType(
              () -> service.uploadProject(userId, thumbnailFile, request), ProjectException.class);
      assertThat(ex.getErrorCode()).isEqualTo(ProjectErrorStatus.NOT_FOUND_PROJECT);
    }

    @Test
    @DisplayName("프로젝트 업로드 실패 - 파일 업로드 실패")
    void uploadProjectFailFileUploadFailure() {
      // given
      Long userId = 1L;
      UploadProjectRequest request = createSampleUploadRequest();
      Project project = createSampleProject();
      Project savedProject = createSampleProject();

      // Mock MultipartFile 설정
      given(thumbnailFile.isEmpty()).willReturn(false);
      given(thumbnailFile.getOriginalFilename()).willReturn("test.jpg");
      given(thumbnailFile.getSize()).willReturn(1024L);

      given(getTopicLabelFromIdUseCase.getLabelById(anyLong())).willReturn("Topic Label");
      given(getAnalysisPurposeLabelFromIdUseCase.getLabelById(anyLong()))
          .willReturn("Analysis Label");
      given(getDataSourceLabelFromIdUseCase.getLabelById(anyLong())).willReturn("DataSource Label");
      given(getAuthorLevelLabelFromIdUseCase.getLabelById(anyLong())).willReturn("Author Label");
      willDoNothing().given(validateDataUseCase).validateData(anyLong());
      given(checkProjectExistsByIdPort.checkProjectExistsById(anyLong())).willReturn(true);
      given(createProjectDtoMapper.toDomain(any(), anyLong(), any())).willReturn(project);
      given(createProjectPort.saveProject(any())).willReturn(savedProject);
      given(fileCommandUseCase.uploadFile(anyString(), any()))
          .willThrow(new RuntimeException("Upload failed"));

      // when & then
      CommonException ex =
          catchThrowableOfType(
              () -> service.uploadProject(userId, thumbnailFile, request), CommonException.class);
      assertThat(ex.getErrorCode()).isEqualTo(CommonErrorStatus.FILE_UPLOAD_FAILURE);
    }
  }

  @Nested
  @DisplayName("프로젝트 수정")
  class ModifyProject {

    @Test
    @DisplayName("프로젝트 수정 성공")
    void modifyProjectSuccess() {
      // given
      Long projectId = 1L;
      ModifyProjectRequest request = createSampleModifyRequest();
      Project existingProject = createSampleProject();
      Set<Long> existingDataIds = Set.of(1L, 3L);

      // Mock MultipartFile 설정
      given(thumbnailFile.isEmpty()).willReturn(false);
      given(thumbnailFile.getOriginalFilename()).willReturn("test.jpg");
      given(thumbnailFile.getSize()).willReturn(1024L);

      given(getTopicLabelFromIdUseCase.getLabelById(anyLong())).willReturn("Topic Label");
      given(getAnalysisPurposeLabelFromIdUseCase.getLabelById(anyLong()))
          .willReturn("Analysis Label");
      given(getDataSourceLabelFromIdUseCase.getLabelById(anyLong())).willReturn("DataSource Label");
      given(getAuthorLevelLabelFromIdUseCase.getLabelById(anyLong())).willReturn("Author Label");
      willDoNothing().given(validateDataUseCase).validateData(anyLong());
      given(checkProjectExistsByIdPort.checkProjectExistsById(anyLong())).willReturn(true);
      given(extractProjectOwnerPort.findDataIdsByProjectId(projectId)).willReturn(existingDataIds);
      given(fileCommandUseCase.uploadFile(anyString(), any())).willReturn("uploaded-url");
      given(findProjectPort.findProjectById(projectId)).willReturn(Optional.of(existingProject));
      given(findUsernameUseCase.findUsernameById(anyLong())).willReturn("testuser");
      given(findUserThumbnailUseCase.findUserThumbnailById(anyLong())).willReturn("profile-url");

      // when
      service.modifyProject(projectId, thumbnailFile, request);

      // then
      then(updateProjectPort).should().modifyProject(eq(projectId), any(), any());
      then(deleteProjectDataPort).should().deleteByProjectIdAndDataIdIn(eq(projectId), any());
      then(fileCommandUseCase).should().uploadFile(anyString(), any());
      then(indexProjectPort).should().index(any(ProjectSearchDocument.class));
    }

    @Test
    @DisplayName("프로젝트 수정 실패 - 프로젝트가 존재하지 않음")
    void modifyProjectFailProjectNotFound() {
      // given
      Long projectId = 999L;
      ModifyProjectRequest request = createSampleModifyRequest();

      // Mock MultipartFile 설정
      given(thumbnailFile.isEmpty()).willReturn(false);
      given(thumbnailFile.getOriginalFilename()).willReturn("test.jpg");
      given(thumbnailFile.getSize()).willReturn(1024L);

      given(getTopicLabelFromIdUseCase.getLabelById(anyLong())).willReturn("Topic Label");
      given(getAnalysisPurposeLabelFromIdUseCase.getLabelById(anyLong()))
          .willReturn("Analysis Label");
      given(getDataSourceLabelFromIdUseCase.getLabelById(anyLong())).willReturn("DataSource Label");
      given(getAuthorLevelLabelFromIdUseCase.getLabelById(anyLong())).willReturn("Author Label");
      willDoNothing().given(validateDataUseCase).validateData(anyLong());
      given(checkProjectExistsByIdPort.checkProjectExistsById(anyLong())).willReturn(true);
      given(extractProjectOwnerPort.findDataIdsByProjectId(projectId)).willReturn(Set.of());
      given(fileCommandUseCase.uploadFile(anyString(), any())).willReturn("uploaded-url");
      given(findProjectPort.findProjectById(projectId)).willReturn(Optional.empty());

      // when & then
      ProjectException ex =
          catchThrowableOfType(
              () -> service.modifyProject(projectId, thumbnailFile, request),
              ProjectException.class);
      assertThat(ex.getErrorCode()).isEqualTo(ProjectErrorStatus.NOT_FOUND_PROJECT);
    }

    @Test
    @DisplayName("프로젝트 수정 실패 - 부모 프로젝트가 존재하지 않음")
    void modifyProjectFailParentProjectNotFound() {
      // given
      Long projectId = 1L;
      ModifyProjectRequest request =
          new ModifyProjectRequest(
              "Modified Project",
              1L,
              1L,
              1L,
              1L,
              false,
              999L, // 존재하지 않는 부모 프로젝트 ID
              "Modified Description",
              List.of(1L));

      // Mock MultipartFile 설정
      given(thumbnailFile.isEmpty()).willReturn(false);
      given(thumbnailFile.getOriginalFilename()).willReturn("test.jpg");
      given(thumbnailFile.getSize()).willReturn(1024L);

      given(getTopicLabelFromIdUseCase.getLabelById(anyLong())).willReturn("Topic Label");
      given(getAnalysisPurposeLabelFromIdUseCase.getLabelById(anyLong()))
          .willReturn("Analysis Label");
      given(getDataSourceLabelFromIdUseCase.getLabelById(anyLong())).willReturn("DataSource Label");
      given(getAuthorLevelLabelFromIdUseCase.getLabelById(anyLong())).willReturn("Author Label");
      willDoNothing().given(validateDataUseCase).validateData(anyLong());
      given(checkProjectExistsByIdPort.checkProjectExistsById(999L)).willReturn(false);

      // when & then
      ProjectException ex =
          catchThrowableOfType(
              () -> service.modifyProject(projectId, thumbnailFile, request),
              ProjectException.class);
      assertThat(ex.getErrorCode()).isEqualTo(ProjectErrorStatus.NOT_FOUND_PROJECT);
    }
  }

  @Nested
  @DisplayName("경계값 테스트")
  class BoundaryTests {

    @Test
    @DisplayName("null 썸네일 파일로 업로드")
    void uploadProjectWithNullThumbnail() {
      // given
      Long userId = 1L;
      UploadProjectRequest request = createSampleUploadRequest();
      Project project = createSampleProject();
      Project savedProject = createSampleProject();

      given(getTopicLabelFromIdUseCase.getLabelById(anyLong())).willReturn("Topic Label");
      given(getAnalysisPurposeLabelFromIdUseCase.getLabelById(anyLong()))
          .willReturn("Analysis Label");
      given(getDataSourceLabelFromIdUseCase.getLabelById(anyLong())).willReturn("DataSource Label");
      given(getAuthorLevelLabelFromIdUseCase.getLabelById(anyLong())).willReturn("Author Label");
      willDoNothing().given(validateDataUseCase).validateData(anyLong());
      given(checkProjectExistsByIdPort.checkProjectExistsById(anyLong())).willReturn(true);
      given(createProjectDtoMapper.toDomain(any(), anyLong(), any())).willReturn(project);
      given(createProjectPort.saveProject(any())).willReturn(savedProject);
      given(findUsernameUseCase.findUsernameById(anyLong())).willReturn("testuser");
      given(findUserThumbnailUseCase.findUserThumbnailById(anyLong())).willReturn("profile-url");

      // when
      UploadProjectResponse response = service.uploadProject(userId, null, request);

      // then
      assertThat(response.id()).isEqualTo(savedProject.getId());
      then(fileCommandUseCase).should(never()).uploadFile(anyString(), any());
    }

    @Test
    @DisplayName("빈 데이터 ID 리스트로 수정")
    void modifyProjectWithEmptyDataIds() {
      // given
      Long projectId = 1L;
      ModifyProjectRequest request =
          new ModifyProjectRequest(
              "Modified Project",
              1L,
              1L,
              1L,
              1L,
              false,
              null,
              "Modified Description",
              List.of() // 빈 데이터 ID 리스트
              );
      Project existingProject = createSampleProject();
      Set<Long> existingDataIds = Set.of(1L, 2L);

      // Mock MultipartFile 설정
      given(thumbnailFile.isEmpty()).willReturn(false);
      given(thumbnailFile.getOriginalFilename()).willReturn("test.jpg");
      given(thumbnailFile.getSize()).willReturn(1024L);

      given(getTopicLabelFromIdUseCase.getLabelById(anyLong())).willReturn("Topic Label");
      given(getAnalysisPurposeLabelFromIdUseCase.getLabelById(anyLong()))
          .willReturn("Analysis Label");
      given(getDataSourceLabelFromIdUseCase.getLabelById(anyLong())).willReturn("DataSource Label");
      given(getAuthorLevelLabelFromIdUseCase.getLabelById(anyLong())).willReturn("Author Label");
      given(checkProjectExistsByIdPort.checkProjectExistsById(anyLong())).willReturn(true);
      given(extractProjectOwnerPort.findDataIdsByProjectId(projectId)).willReturn(existingDataIds);
      given(findProjectPort.findProjectById(projectId)).willReturn(Optional.of(existingProject));
      given(findUsernameUseCase.findUsernameById(anyLong())).willReturn("testuser");
      given(findUserThumbnailUseCase.findUserThumbnailById(anyLong())).willReturn("profile-url");

      // when
      service.modifyProject(projectId, thumbnailFile, request);

      // then
      then(deleteProjectDataPort)
          .should()
          .deleteByProjectIdAndDataIdIn(eq(projectId), eq(existingDataIds));
      then(updateProjectPort).should().modifyProject(eq(projectId), any(), any());
    }
  }
}
