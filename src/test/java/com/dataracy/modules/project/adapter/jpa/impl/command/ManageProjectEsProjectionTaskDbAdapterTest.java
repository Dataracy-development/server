package com.dataracy.modules.project.adapter.jpa.impl.command;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectEsProjectionTaskEntity;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectEsProjectionTaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ManageProjectEsProjectionTaskDbAdapterTest {

    @InjectMocks
    private ManageProjectEsProjectionTaskDbAdapter adapter;

    @Mock
    private ProjectEsProjectionTaskRepository repo;

    @Captor
    private ArgumentCaptor<ProjectEsProjectionTaskEntity> captor;

    @Nested
    @DisplayName("댓글 증감 큐잉")
    class EnqueueCommentDelta {
        @Test
        @DisplayName("댓글 delta 값이 저장된다")
        void enqueueCommentDeltaShouldSaveEntity() {
            // given
            Long projectId = 1L;
            int delta = 3;

            // when
            adapter.enqueueCommentDelta(projectId, delta);

            // then
            then(repo).should().save(captor.capture());
            ProjectEsProjectionTaskEntity saved = captor.getValue();
            assertThat(saved.getProjectId()).isEqualTo(projectId);
            assertThat(saved.getDeltaComment()).isEqualTo(delta);
        }
    }

    @Nested
    @DisplayName("좋아요 증감 큐잉")
    class EnqueueLikeDelta {
        @Test
        @DisplayName("좋아요 delta 값이 저장된다")
        void enqueueLikeDeltaShouldSaveEntity() {
            // given
            Long projectId = 2L;
            int delta = -1;

            // when
            adapter.enqueueLikeDelta(projectId, delta);

            // then
            then(repo).should().save(captor.capture());
            ProjectEsProjectionTaskEntity saved = captor.getValue();
            assertThat(saved.getProjectId()).isEqualTo(projectId);
            assertThat(saved.getDeltaLike()).isEqualTo(delta);
        }
    }

    @Nested
    @DisplayName("조회수 증감 큐잉")
    class EnqueueViewDelta {
        @Test
        @DisplayName("조회수 delta 값이 저장된다")
        void enqueueViewDeltaShouldSaveEntity() {
            // given
            Long projectId = 3L;
            Long delta = 100L;

            // when
            adapter.enqueueViewDelta(projectId, delta);

            // then
            then(repo).should().save(captor.capture());
            ProjectEsProjectionTaskEntity saved = captor.getValue();
            assertThat(saved.getProjectId()).isEqualTo(projectId);
            assertThat(saved.getDeltaView()).isEqualTo(delta);
        }
    }

    @Nested
    @DisplayName("삭제 상태 큐잉")
    class EnqueueSetDeleted {
        @Test
        @DisplayName("삭제 상태 true 값이 저장된다")
        void enqueueSetDeletedShouldSaveEntity() {
            // given
            Long projectId = 4L;

            // when
            adapter.enqueueSetDeleted(projectId, true);

            // then
            then(repo).should().save(captor.capture());
            ProjectEsProjectionTaskEntity saved = captor.getValue();
            assertThat(saved.getProjectId()).isEqualTo(projectId);
            assertThat(saved.getSetDeleted()).isTrue();
        }
    }

    @Test
    @DisplayName("delete 호출 시 repo.deleteImmediate 위임")
    void deleteShouldDelegateToRepo() {
        // given
        Long taskId = 99L;

        // when
        adapter.delete(taskId);

        // then
        then(repo).should().deleteImmediate(taskId);
    }
}
