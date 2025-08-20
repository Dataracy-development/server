package com.dataracy.modules.project.application.port.out.query.projection;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectEsProjectionTaskEntity;
import com.dataracy.modules.project.domain.enums.ProjectEsProjectionType;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface LoadProjectProjectionTaskPort {
    List<ProjectEsProjectionTaskEntity> findBatchForWork(LocalDateTime now, List<ProjectEsProjectionType> statuses, Pageable pageable);
}
