package com.dataracy.modules.project.adapter.jpa.impl.command;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectEsProjectionTaskEntity;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectEsProjectionTaskRepository;
import org.junit.jupiter.api.DisplayName;
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

    @Mock ProjectEsProjectionTaskRepository repo;
    @InjectMocks ManageProjectEsProjectionTaskDbAdapter adapter;
    @Captor ArgumentCaptor<ProjectEsProjectionTaskEntity> entityCaptor;

    @Test
    @DisplayName("enqueueViewDelta_should_save_entity_with_deltaView")
    void enqueueViewDelta_should_save_entity_with_deltaView() {
        // given
        Long projectId = 10L;

        // when
        adapter.enqueueViewDelta(projectId, 7L);

        // then
        then(repo).should().save(entityCaptor.capture());
        ProjectEsProjectionTaskEntity saved = entityCaptor.getValue();
        assertThat(saved.getProjectId()).isEqualTo(projectId);
        assertThat(saved.getDeltaView()).isEqualTo(7L);
    }
}
