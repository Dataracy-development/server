package com.dataracy.modules.project.adapter.jpa.impl.command;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectDataEntity;
import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectDataJpaRepository;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectJpaRepository;
import com.dataracy.modules.project.application.dto.request.command.ModifyProjectRequest;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectCommandDbAdapterTest {

    @InjectMocks
    private ProjectCommandDbAdapter adapter;

    @Mock
    private ProjectJpaRepository projectJpaRepository;

    @Mock
    private ProjectDataJpaRepository projectDataJpaRepository;

    @Captor
    private ArgumentCaptor<ProjectDataEntity> dataEntityCaptor;

    @Nested
    @DisplayName("프로젝트 업로드")
    class SaveProject {

        @Test
        @DisplayName("프로젝트 저장 성공 시 최소 정보 도메인 반환")
        void saveProjectSuccess() {
            // given
            Project project = mock(Project.class);
            ProjectEntity entity = ProjectEntity.builder().id(1L).title("t").build();
            given(projectJpaRepository.save(any(ProjectEntity.class))).willReturn(entity);

            // when
            Project result = adapter.saveProject(project);

            // then
            assertThat(result.getId()).isEqualTo(1L);
            then(projectJpaRepository).should().save(any(ProjectEntity.class));
        }

        @Test
        @DisplayName("저장 실패 시 ProjectException(FAIL_SAVE_PROJECT) 발생")
        void saveProjectFail() {
            // given
            Project project = mock(Project.class);
            given(projectJpaRepository.save(any())).willThrow(new RuntimeException("fail"));

            // when
            ProjectException ex = catchThrowableOfType(
                    () -> adapter.saveProject(project),
                    ProjectException.class
            );

            // then
            assertThat(ex.getErrorCode()).isEqualTo(ProjectErrorStatus.FAIL_SAVE_PROJECT);
        }
    }

    @Nested
    @DisplayName("프로젝트 썸네일 파일 업데이트")
    class UpdateThumbnailFile {

        @Test
        @DisplayName("존재하는 프로젝트의 썸네일 갱신 성공")
        void updateThumbnailSuccess() {
            // given
            ProjectEntity entity = ProjectEntity.builder().id(1L).title("t").thumbnailUrl("old").build();
            given(projectJpaRepository.findById(1L)).willReturn(Optional.of(entity));

            // when
            adapter.updateThumbnailFile(1L, "newThumb");

            // then
            assertThat(entity.getThumbnailUrl()).isEqualTo("newThumb");
            then(projectJpaRepository).should().save(entity);
        }

        @Test
        @DisplayName("프로젝트가 존재하지 않으면 ProjectException(NOT_FOUND_PROJECT)")
        void updateThumbnailFailWhenNotFound() {
            // given
            given(projectJpaRepository.findById(1L)).willReturn(Optional.empty());

            // when
            ProjectException ex = catchThrowableOfType(
                    () -> adapter.updateThumbnailFile(1L, "newThumb"),
                    ProjectException.class
            );

            // then
            assertThat(ex.getErrorCode()).isEqualTo(ProjectErrorStatus.NOT_FOUND_PROJECT);
        }
    }

    @Nested
    @DisplayName("프로젝트 수정")
    class ModifyProject {

        @Test
        @DisplayName("프로젝트와 부모 모두 존재 → 수정 성공")
        void modifyProjectSuccess() {
            // given
            ProjectEntity project = ProjectEntity.builder().id(1L).title("old").build();
            ProjectEntity parent = ProjectEntity.builder().id(2L).title("p").build();

            ModifyProjectRequest req = new ModifyProjectRequest(
                    "new", 1L, 2L, 3L, 4L, true, 2L, "c", List.of()
            );

            given(projectJpaRepository.findById(1L)).willReturn(Optional.of(project));
            given(projectJpaRepository.findById(2L)).willReturn(Optional.of(parent));

            // when
            adapter.modifyProject(1L, req, Set.of(10L));

            // then
            then(projectDataJpaRepository).should().saveAll(anyList());
            then(projectJpaRepository).should().save(project);
            assertThat(project.getTitle()).isEqualTo("new");
            assertThat(project.getParentProject()).isEqualTo(parent);
        }

        @Test
        @DisplayName("프로젝트가 존재하지 않으면 ProjectException(NOT_FOUND_PROJECT)")
        void modifyProjectFailWhenProjectNotFound() {
            // given
            ModifyProjectRequest req = new ModifyProjectRequest(
                    "new", 1L, 2L, 3L, 4L, true, null, "c", List.of()
            );
            given(projectJpaRepository.findById(1L)).willReturn(Optional.empty());

            // when
            ProjectException ex = catchThrowableOfType(
                    () -> adapter.modifyProject(1L, req, Set.of()),
                    ProjectException.class
            );

            // then
            assertThat(ex.getErrorCode()).isEqualTo(ProjectErrorStatus.NOT_FOUND_PROJECT);
        }

        @Test
        @DisplayName("부모 프로젝트가 존재하지 않으면 ProjectException(NOT_FOUND_PROJECT)")
        void modifyProjectFailWhenParentNotFound() {
            // given
            ProjectEntity project = ProjectEntity.builder().id(1L).title("old").build();
            ModifyProjectRequest req = new ModifyProjectRequest(
                    "new", 1L, 2L, 3L, 4L, true, 99L, "c", List.of()
            );

            given(projectJpaRepository.findById(1L)).willReturn(Optional.of(project));
            given(projectJpaRepository.findById(99L)).willReturn(Optional.empty());

            // when
            ProjectException ex = catchThrowableOfType(
                    () -> adapter.modifyProject(1L, req, Set.of()),
                    ProjectException.class
            );

            // then
            assertThat(ex.getErrorCode()).isEqualTo(ProjectErrorStatus.NOT_FOUND_PROJECT);
        }
    }

    @Test
    @DisplayName("deleteByProjectIdAndDataIdIn → Repo에 위임")
    void deleteByProjectIdAndDataIdIn() {
        // given
        Set<Long> dataIds = Set.of(1L, 2L);

        // when
        adapter.deleteByProjectIdAndDataIdIn(1L, dataIds);

        // then
        then(projectDataJpaRepository).should().deleteByProjectIdAndDataIdIn(1L, dataIds);
    }
}
