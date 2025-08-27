package com.dataracy.modules.project.adapter.jpa.impl.query;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectEsProjectionTaskEntity;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectEsProjectionTaskRepository;
import com.dataracy.modules.project.domain.enums.ProjectEsProjectionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class LoadProjectEsProjectionTaskDbAdapterTest {

    @Mock
    private ProjectEsProjectionTaskRepository repo;

    @InjectMocks
    private LoadProjectEsProjectionTaskDbAdapter adapter;

    @Test
    @DisplayName("findBatchForWork → 지정된 조건으로 repo.findBatchForWork 호출 및 결과 반환")
    void findBatchForWorkSuccess() {
        // given
        LocalDateTime now = LocalDateTime.now();
        List<ProjectEsProjectionType> statuses = List.of(ProjectEsProjectionType.PENDING);
        PageRequest pageable = PageRequest.of(0, 10);

        ProjectEsProjectionTaskEntity task = ProjectEsProjectionTaskEntity.builder()
                .id(1L)
                .projectId(100L)
                .status(ProjectEsProjectionType.PENDING)
                .nextRunAt(now.minusMinutes(1))
                .build();

        given(repo.findBatchForWork(now, statuses, pageable))
                .willReturn(List.of(task));

        // when
        List<ProjectEsProjectionTaskEntity> result = adapter.findBatchForWork(now, statuses, pageable);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProjectId()).isEqualTo(100L);
        then(repo).should().findBatchForWork(now, statuses, pageable);
    }

    @Test
    @DisplayName("findBatchForWork → 결과가 없으면 빈 리스트 반환")
    void findBatchForWorkEmpty() {
        // given
        LocalDateTime now = LocalDateTime.now();
        List<ProjectEsProjectionType> statuses = List.of(ProjectEsProjectionType.PENDING);
        PageRequest pageable = PageRequest.of(0, 5);

        given(repo.findBatchForWork(now, statuses, pageable))
                .willReturn(List.of());

        // when
        List<ProjectEsProjectionTaskEntity> result = adapter.findBatchForWork(now, statuses, pageable);

        // then
        assertThat(result).isEmpty();
    }
}
