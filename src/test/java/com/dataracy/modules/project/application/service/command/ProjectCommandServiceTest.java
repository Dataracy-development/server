package com.dataracy.modules.project.application.service.command;

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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

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
    private ProjectCommandService service;

    @Mock
    private MultipartFile thumbnailFile;

    @BeforeEach
    void setup() {
        given(getTopicLabelFromIdUseCase.getLabelById(anyLong())).willReturn("Topic");
        given(getAnalysisPurposeLabelFromIdUseCase.getLabelById(anyLong())).willReturn("Purpose");
        given(getDataSourceLabelFromIdUseCase.getLabelById(anyLong())).willReturn("Source");
        given(getAuthorLevelLabelFromIdUseCase.getLabelById(anyLong())).willReturn("Author");
    }

    @Test
    @DisplayName("프로젝트 업로드 성공 - 썸네일 업로드 및 인덱싱")
    void uploadProjectSuccessWithThumbnailUploadAndIndexing() {
        // given
        Long userId = 77L;
        UploadProjectRequest req = new UploadProjectRequest(
                "t", 1L, 2L, 3L, 4L, false, null, "c", List.of(10L, 20L)
        );
        Project toSave = Project.builder().title("t").userId(userId).build();
        Project saved = Project.builder().id(999L).userId(userId).title("t").build();

        given(createProjectDtoMapper.toDomain(req, userId, null)).willReturn(toSave);
        given(createProjectPort.saveProject(toSave)).willReturn(saved);
        given(findUsernameUseCase.findUsernameById(userId)).willReturn("user");
        given(findUserThumbnailUseCase.findUserThumbnailById(userId)).willReturn("https://profile");
        given(thumbnailFile.isEmpty()).willReturn(false);
        given(thumbnailFile.getOriginalFilename()).willReturn("file.png");
        given(fileCommandUseCase.uploadFile(anyString(), eq(thumbnailFile))).willReturn("https://file");

        // when
        UploadProjectResponse res = service.uploadProject(userId, thumbnailFile, req);

        // then
        assertThat(res).isNotNull();
        assertThat(res.id()).isEqualTo(999L);
        then(updateProjectFilePort).should().updateThumbnailFile(999L, "https://file");
        then(indexProjectPort).should().index(any(ProjectSearchDocument.class));
    }

    @Test
    @DisplayName("프로젝트 업로드 실패 - 썸네일 업로드 오류 발생")
    void uploadProjectFailWhenThumbnailUploadFails() {
        // given
        Long userId = 77L;
        UploadProjectRequest req = new UploadProjectRequest(
                "t", 1L, 2L, 3L, 4L, false, null, "c", Collections.emptyList()
        );
        Project toSave = Project.builder().title("t").userId(userId).build();
        Project saved = Project.builder().id(1000L).userId(userId).title("t").build();

        given(createProjectDtoMapper.toDomain(req, userId, null)).willReturn(toSave);
        given(createProjectPort.saveProject(toSave)).willReturn(saved);
        given(thumbnailFile.isEmpty()).willReturn(false);
        given(thumbnailFile.getOriginalFilename()).willReturn("file.png");
        given(fileCommandUseCase.uploadFile(anyString(), eq(thumbnailFile)))
                .willThrow(new RuntimeException("S3 down"));

        // when
        RuntimeException ex = catchThrowableOfType(
                () -> service.uploadProject(userId, thumbnailFile, req),
                RuntimeException.class
        );

        // then
        assertThat(ex).isNotNull();
        then(updateProjectFilePort).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("프로젝트 수정 성공 - 연관 관계 업데이트 및 인덱싱")
    void modifyProjectSuccessShouldUpdateRelationsAndIndex() {
        // given
        Long projectId = 55L;
        ModifyProjectRequest req = new ModifyProjectRequest("t2", 1L, 2L, 3L, 4L, false, null, "c2", List.of(10L, 30L));
        Project updated = Project.builder().id(projectId).userId(5L).dataIds(List.of(10L, 30L)).build();

        given(extractProjectOwnerPort.findDataIdsByProjectId(projectId)).willReturn(Set.of(10L, 20L));
        given(findProjectPort.findProjectById(projectId)).willReturn(Optional.of(updated));
        given(findUsernameUseCase.findUsernameById(5L)).willReturn("user");
        given(findUserThumbnailUseCase.findUserThumbnailById(5L)).willReturn("https://profile");
        given(thumbnailFile.isEmpty()).willReturn(true);

        // when
        service.modifyProject(projectId, thumbnailFile, req);

        // then
        then(updateProjectPort).should().modifyProject(eq(projectId), eq(req), any(Set.class));
        then(deleteProjectDataPort).should().deleteByProjectIdAndDataIdIn(eq(projectId), any(Set.class));
        then(indexProjectPort).should().index(any(ProjectSearchDocument.class));
    }

    @Test
    @DisplayName("프로젝트 수정 실패 - 프로젝트 없음")
    void modifyProjectFailWhenProjectNotFound() {
        // given
        Long projectId = 66L;
        ModifyProjectRequest req = new ModifyProjectRequest("t2", 1L, 2L, 3L, 4L, false, null, "c2", List.of());

        given(findProjectPort.findProjectById(projectId)).willReturn(Optional.empty());
        given(thumbnailFile.isEmpty()).willReturn(true);

        // when
        ProjectException ex = catchThrowableOfType(
                () -> service.modifyProject(projectId, thumbnailFile, req),
                ProjectException.class
        );

        // then
        assertThat(ex).isNotNull();
    }
}
