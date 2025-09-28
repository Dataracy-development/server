package com.dataracy.modules.project.adapter.jpa.impl.query;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectDataJpaRepository;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectJpaRepository;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;
@ExtendWith(MockitoExtension.class)
class ExtractProjectDbAdapterTest {

    @Mock
    private ProjectJpaRepository projectJpaRepository;

    @Mock
    private ProjectDataJpaRepository projectDataJpaRepository;

    @InjectMocks
    private ExtractProjectDbAdapter adapter;

    @Nested
    @DisplayName("findUserIdByProjectId")
    class FindUserIdByProjectId {

        @Test
        @DisplayName("성공 → 프로젝트 존재 시 userId 반환")
        void success() {
            // given
            ProjectEntity project = ProjectEntity.builder()
                    .id(1L)
                    .userId(100L)
                    .title("title")
                    .analysisPurposeId(1L)
                    .dataSourceId(1L)
                    .authorLevelId(1L)
                    .topicId(1L)
                    .isContinue(true)
                    .content("c")
                    .build();
            given(projectJpaRepository.findById(1L)).willReturn(Optional.of(project));

            // when
            Long userId = adapter.findUserIdByProjectId(1L);

            // then
            assertThat(userId).isEqualTo(100L);
        }

        @Test
        @DisplayName("실패 → 프로젝트 없으면 ProjectException(NOT_FOUND_PROJECT)")
        void failNotFound() {
            // given
            given(projectJpaRepository.findById(99L)).willReturn(Optional.empty());

            // when
            ProjectException ex = catchThrowableOfType(
                    () -> adapter.findUserIdByProjectId(99L),
                    ProjectException.class
            );

            // then
            assertThat(ex.getErrorCode()).isEqualTo(ProjectErrorStatus.NOT_FOUND_PROJECT);
        }
    }

    @Nested
    @DisplayName("findUserIdIncludingDeleted")
    class FindUserIdIncludingDeleted {

        @Test
        @DisplayName("성공 → 삭제된 프로젝트 포함 userId 반환")
        void success() {
            // given
            ProjectEntity project = ProjectEntity.builder()
                    .id(2L)
                    .userId(200L)
                    .title("t")
                    .analysisPurposeId(1L)
                    .dataSourceId(1L)
                    .authorLevelId(1L)
                    .topicId(1L)
                    .isContinue(false)
                    .content("c")
                    .build();
            given(projectJpaRepository.findIncludingDeleted(2L)).willReturn(Optional.of(project));

            // when
            Long userId = adapter.findUserIdIncludingDeleted(2L);

            // then
            assertThat(userId).isEqualTo(200L);
        }

        @Test
        @DisplayName("실패 → 프로젝트 없으면 ProjectException(NOT_FOUND_PROJECT)")
        void failNotFound() {
            // given
            given(projectJpaRepository.findIncludingDeleted(100L)).willReturn(Optional.empty());

            // when
            ProjectException ex = catchThrowableOfType(
                    () -> adapter.findUserIdIncludingDeleted(100L),
                    ProjectException.class
            );

            // then
            assertThat(ex.getErrorCode()).isEqualTo(ProjectErrorStatus.NOT_FOUND_PROJECT);
        }
    }

    @Nested
    @DisplayName("findDataIdsByProjectId")
    class FindDataIdsByProjectId {

        @Test
        @DisplayName("성공 → 연결된 dataId 집합 반환")
        void success() {
            // given
            given(projectDataJpaRepository.findDataIdsByProjectId(1L))
                    .willReturn(Set.of(10L, 20L));

            // when
            Set<Long> dataIds = adapter.findDataIdsByProjectId(1L);

            // then
            assertThat(dataIds).containsExactlyInAnyOrder(10L, 20L);
            then(projectDataJpaRepository).should().findDataIdsByProjectId(1L);
        }

        @Test
        @DisplayName("성공 → 연결된 데이터 없으면 빈 집합 반환")
        void empty() {
            // given
            given(projectDataJpaRepository.findDataIdsByProjectId(2L))
                    .willReturn(Set.of());

            // when
            Set<Long> dataIds = adapter.findDataIdsByProjectId(2L);

            // then
            assertThat(dataIds).isEmpty();
        }
    }
}
