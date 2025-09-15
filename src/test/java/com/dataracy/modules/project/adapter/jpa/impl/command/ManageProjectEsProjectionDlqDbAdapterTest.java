package com.dataracy.modules.project.adapter.jpa.impl.command;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectEsProjectionDlqEntity;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectEsProjectionDlqRepository;
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
class ManageProjectEsProjectionDlqDbAdapterTest {

    @InjectMocks
    private ManageProjectEsProjectionDlqDbAdapter adapter;

    @Mock
    private ProjectEsProjectionDlqRepository repo;

    @Captor
    private ArgumentCaptor<ProjectEsProjectionDlqEntity> captor;

    @Test
    @DisplayName("save 호출 시 ProjectEsProjectionDlqEntity 가 생성되어 repo.save 에 전달된다")
    void saveShouldPersistEntity() {
        // given
        Long projectId = 1L;
        Integer deltaComment = 3;
        Integer deltaLike = 5;
        Long deltaView = 10L;
        Boolean setDeleted = true;
        String lastError = "error occurred";

        // when
        adapter.save(projectId, deltaComment, deltaLike, deltaView, setDeleted, lastError);

        // then
        then(repo).should().save(captor.capture());
        ProjectEsProjectionDlqEntity saved = captor.getValue();

        assertThat(saved.getProjectId()).isEqualTo(projectId);
        assertThat(saved.getDeltaComment()).isEqualTo(deltaComment);
        assertThat(saved.getDeltaLike()).isEqualTo(deltaLike);
        assertThat(saved.getDeltaView()).isEqualTo(deltaView);
        assertThat(saved.getSetDeleted()).isTrue();
        assertThat(saved.getLastError()).isEqualTo("error occurred");
    }
}
