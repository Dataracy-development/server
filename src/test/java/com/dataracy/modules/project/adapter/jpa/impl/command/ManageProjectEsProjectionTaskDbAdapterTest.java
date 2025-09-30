package com.dataracy.modules.project.adapter.jpa.impl.command;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectEsProjectionTaskEntity;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectEsProjectionTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.*;

/**
 * ManageProjectEsProjectionTaskDbAdapter 테스트
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ManageProjectEsProjectionTaskDbAdapterTest {

    @InjectMocks
    private ManageProjectEsProjectionTaskDbAdapter manageProjectEsProjectionTaskDbAdapter;

    @Mock
    private ProjectEsProjectionTaskRepository projectEsProjectionTaskRepository;

    @BeforeEach
    void setUp() {
        // 기본 mock 설정
        given(projectEsProjectionTaskRepository.save(any(ProjectEsProjectionTaskEntity.class)))
                .willReturn(ProjectEsProjectionTaskEntity.builder().id(1L).build());
        given(projectEsProjectionTaskRepository.saveAll(anyList()))
                .willReturn(List.of());
        willDoNothing().given(projectEsProjectionTaskRepository).deleteImmediate(anyLong());
    }

    @Test
    @DisplayName("댓글 델타 큐잇 성공")
    void enqueueCommentDelta_성공() {
        // given
        Long projectId = 1L;
        int deltaComment = 5;

        // when
        manageProjectEsProjectionTaskDbAdapter.enqueueCommentDelta(projectId, deltaComment);

        // then
        then(projectEsProjectionTaskRepository).should().save(any(ProjectEsProjectionTaskEntity.class));
    }

    @Test
    @DisplayName("댓글 델타 큐잇 성공 - 음수 값")
    void enqueueCommentDelta_성공_음수값() {
        // given
        Long projectId = 1L;
        int deltaComment = -3; // 댓글 삭제

        // when
        manageProjectEsProjectionTaskDbAdapter.enqueueCommentDelta(projectId, deltaComment);

        // then
        then(projectEsProjectionTaskRepository).should().save(any(ProjectEsProjectionTaskEntity.class));
    }

    @Test
    @DisplayName("좋아요 델타 큐잇 성공")
    void enqueueLikeDelta_성공() {
        // given
        Long projectId = 1L;
        int deltaLike = 1;

        // when
        manageProjectEsProjectionTaskDbAdapter.enqueueLikeDelta(projectId, deltaLike);

        // then
        then(projectEsProjectionTaskRepository).should().save(any(ProjectEsProjectionTaskEntity.class));
    }

    @Test
    @DisplayName("좋아요 델타 큐잇 성공 - 음수 값")
    void enqueueLikeDelta_성공_음수값() {
        // given
        Long projectId = 1L;
        int deltaLike = -1; // 좋아요 취소

        // when
        manageProjectEsProjectionTaskDbAdapter.enqueueLikeDelta(projectId, deltaLike);

        // then
        then(projectEsProjectionTaskRepository).should().save(any(ProjectEsProjectionTaskEntity.class));
    }

    @Test
    @DisplayName("조회수 델타 큐잇 성공")
    void enqueueViewDelta_성공() {
        // given
        Long projectId = 1L;
        Long deltaView = 10L;

        // when
        manageProjectEsProjectionTaskDbAdapter.enqueueViewDelta(projectId, deltaView);

        // then
        then(projectEsProjectionTaskRepository).should().save(any(ProjectEsProjectionTaskEntity.class));
    }

    @Test
    @DisplayName("조회수 델타 큐잇 성공 - 0 값")
    void enqueueViewDelta_성공_0값() {
        // given
        Long projectId = 1L;
        Long deltaView = 0L;

        // when
        manageProjectEsProjectionTaskDbAdapter.enqueueViewDelta(projectId, deltaView);

        // then
        then(projectEsProjectionTaskRepository).should().save(any(ProjectEsProjectionTaskEntity.class));
    }

    @Test
    @DisplayName("삭제 상태 설정 큐잇 성공 - 삭제")
    void enqueueSetDeleted_성공_삭제() {
        // given
        Long projectId = 1L;
        boolean deleted = true;

        // when
        manageProjectEsProjectionTaskDbAdapter.enqueueSetDeleted(projectId, deleted);

        // then
        then(projectEsProjectionTaskRepository).should().save(any(ProjectEsProjectionTaskEntity.class));
    }

    @Test
    @DisplayName("삭제 상태 설정 큐잇 성공 - 복원")
    void enqueueSetDeleted_성공_복원() {
        // given
        Long projectId = 1L;
        boolean deleted = false;

        // when
        manageProjectEsProjectionTaskDbAdapter.enqueueSetDeleted(projectId, deleted);

        // then
        then(projectEsProjectionTaskRepository).should().save(any(ProjectEsProjectionTaskEntity.class));
    }

    @Test
    @DisplayName("프로젝션 작업 삭제 성공")
    void delete_성공() {
        // given
        Long projectEsProjectionTaskId = 1L;

        // when
        manageProjectEsProjectionTaskDbAdapter.delete(projectEsProjectionTaskId);

        // then
        then(projectEsProjectionTaskRepository).should().deleteImmediate(projectEsProjectionTaskId);
    }

    @Test
    @DisplayName("배치 조회수 델타 큐잇 성공 - 여러 프로젝트")
    void enqueueViewDeltaBatch_성공_여러프로젝트() {
        // given
        Map<Long, Long> viewCountUpdates = new HashMap<>();
        viewCountUpdates.put(1L, 5L);
        viewCountUpdates.put(2L, 3L);
        viewCountUpdates.put(3L, 10L);

        // when
        manageProjectEsProjectionTaskDbAdapter.enqueueViewDeltaBatch(viewCountUpdates);

        // then
        then(projectEsProjectionTaskRepository).should().saveAll(anyList());
    }

    @Test
    @DisplayName("배치 조회수 델타 큐잇 성공 - 단일 프로젝트")
    void enqueueViewDeltaBatch_성공_단일프로젝트() {
        // given
        Map<Long, Long> viewCountUpdates = new HashMap<>();
        viewCountUpdates.put(1L, 7L);

        // when
        manageProjectEsProjectionTaskDbAdapter.enqueueViewDeltaBatch(viewCountUpdates);

        // then
        then(projectEsProjectionTaskRepository).should().saveAll(anyList());
    }

    @Test
    @DisplayName("배치 조회수 델타 큐잇 - 빈 맵으로 호출 시 아무것도 하지 않음")
    void enqueueViewDeltaBatch_빈맵_아무것도하지않음() {
        // given
        Map<Long, Long> viewCountUpdates = new HashMap<>(); // 빈 맵

        // when
        manageProjectEsProjectionTaskDbAdapter.enqueueViewDeltaBatch(viewCountUpdates);

        // then
        then(projectEsProjectionTaskRepository).should(never()).saveAll(anyList());
    }

    @Test
    @DisplayName("배치 조회수 델타 큐잇 성공 - null 값 포함")
    void enqueueViewDeltaBatch_성공_null값포함() {
        // given
        Map<Long, Long> viewCountUpdates = new HashMap<>();
        viewCountUpdates.put(1L, 5L);
        viewCountUpdates.put(2L, null); // null 값
        viewCountUpdates.put(3L, 10L);

        // when
        manageProjectEsProjectionTaskDbAdapter.enqueueViewDeltaBatch(viewCountUpdates);

        // then
        then(projectEsProjectionTaskRepository).should().saveAll(anyList());
    }

    @Test
    @DisplayName("배치 조회수 델타 큐잇 성공 - 0 값 포함")
    void enqueueViewDeltaBatch_성공_0값포함() {
        // given
        Map<Long, Long> viewCountUpdates = new HashMap<>();
        viewCountUpdates.put(1L, 5L);
        viewCountUpdates.put(2L, 0L); // 0 값
        viewCountUpdates.put(3L, 10L);

        // when
        manageProjectEsProjectionTaskDbAdapter.enqueueViewDeltaBatch(viewCountUpdates);

        // then
        then(projectEsProjectionTaskRepository).should().saveAll(anyList());
    }
}