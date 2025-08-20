package com.dataracy.modules.dataset.application.port.out.query.projection;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEsProjectionTaskEntity;
import com.dataracy.modules.dataset.domain.enums.DataEsProjectionType;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface LoadDataProjectionTaskPort {
    List<DataEsProjectionTaskEntity> findBatchForWork(LocalDateTime now, List<DataEsProjectionType> statuses, Pageable pageable);
}
