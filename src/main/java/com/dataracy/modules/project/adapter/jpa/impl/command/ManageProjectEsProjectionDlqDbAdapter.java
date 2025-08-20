package com.dataracy.modules.project.adapter.jpa.impl.command;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectEsProjectionDlqEntity;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectEsProjectionDlqRepository;
import com.dataracy.modules.project.application.port.out.command.projection.ManageProjectProjectionDlqPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManageProjectEsProjectionDlqDbAdapter implements ManageProjectProjectionDlqPort {
    private final ProjectEsProjectionDlqRepository dlqRepo;

    @Override
    public void save(
            Long projectId,
            Integer deltaComment,
            Integer deltaLike,
            Long deltaView,
            Boolean setDeleted,
            String lastError
    ) {
        dlqRepo.save(ProjectEsProjectionDlqEntity.builder()
                .projectId(projectId)
                .deltaComment(deltaComment)
                .deltaLike(deltaLike)
                .deltaView(deltaView)
                .setDeleted(setDeleted)
                .lastError(lastError)
                .build());
    }
}
