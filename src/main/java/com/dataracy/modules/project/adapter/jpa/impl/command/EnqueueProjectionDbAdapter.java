package com.dataracy.modules.project.adapter.jpa.impl.command;

import com.dataracy.modules.project.adapter.jpa.entity.EsProjectionTaskEntity;
import com.dataracy.modules.project.adapter.jpa.repository.EsProjectionTaskRepository;
import com.dataracy.modules.project.application.port.out.command.projection.EnqueueProjectionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnqueueProjectionDbAdapter implements EnqueueProjectionPort {
    private final EsProjectionTaskRepository repo;

    @Override
    public void enqueueCommentDelta(Long projectId, int deltaComment) {
        repo.save(EsProjectionTaskEntity.builder()
                .projectId(projectId)
                .deltaComment(deltaComment)
                .build());
    }

    @Override
    public void enqueueLikeDelta(Long projectId, int deltaLike) {
        repo.save(EsProjectionTaskEntity.builder()
                .projectId(projectId)
                .deltaLike(deltaLike)
                .build());
    }

    @Override
    public void enqueueSetDeleted(Long projectId, boolean deleted) {
        repo.save(EsProjectionTaskEntity.builder()
                .projectId(projectId)
                .setDeleted(deleted)
                .build());
    }
}

