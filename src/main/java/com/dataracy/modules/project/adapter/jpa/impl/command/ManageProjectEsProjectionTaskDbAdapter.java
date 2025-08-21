package com.dataracy.modules.project.adapter.jpa.impl.command;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectEsProjectionTaskEntity;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectEsProjectionTaskRepository;
import com.dataracy.modules.project.application.port.out.command.projection.ManageProjectProjectionTaskPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManageProjectEsProjectionTaskDbAdapter implements ManageProjectProjectionTaskPort {
    private final ProjectEsProjectionTaskRepository repo;

    @Override
    public void enqueueCommentDelta(Long projectId, int deltaComment) {
        repo.save(ProjectEsProjectionTaskEntity.builder()
                .projectId(projectId)
                .deltaComment(deltaComment)
                .build());
    }

    @Override
    public void enqueueLikeDelta(Long projectId, int deltaLike) {
        repo.save(ProjectEsProjectionTaskEntity.builder()
                .projectId(projectId)
                .deltaLike(deltaLike)
                .build());
    }

    @Override
    public void enqueueViewDelta(Long projectId, Long deltaView) {
        repo.save(ProjectEsProjectionTaskEntity.builder()
                .projectId(projectId)
                .deltaView(deltaView)
                .build());
    }

    @Override
    public void enqueueSetDeleted(Long projectId, boolean deleted) {
        repo.save(ProjectEsProjectionTaskEntity.builder()
                .projectId(projectId)
                .setDeleted(deleted)
                .build());
    }

    @Override
    public void delete(Long projectEsProjectionTaskId) {
        repo.deleteImmediate(projectEsProjectionTaskId);
    }
}
