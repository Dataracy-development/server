package com.dataracy.modules.project.adapter.jpa.impl.query;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectEsProjectionTaskEntity;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectEsProjectionTaskRepository;
import com.dataracy.modules.project.application.port.out.query.projection.LoadProjectProjectionTaskPort;
import com.dataracy.modules.project.domain.enums.ProjectEsProjectionType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LoadProjectEsProjectionTaskDbAdapter implements LoadProjectProjectionTaskPort {
    private final ProjectEsProjectionTaskRepository repo;

    @Override
    public List<ProjectEsProjectionTaskEntity> findBatchForWork(
            LocalDateTime now,
            List<ProjectEsProjectionType> statuses,
            Pageable pageable
    ) {
        return repo.findBatchForWork(
                now,
                statuses,
                pageable
        );
    }
}
