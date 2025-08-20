package com.dataracy.modules.dataset.adapter.jpa.impl.query;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEsProjectionTaskEntity;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataEsProjectionTaskRepository;
import com.dataracy.modules.dataset.application.port.out.query.projection.LoadDataProjectionTaskPort;
import com.dataracy.modules.dataset.domain.enums.DataEsProjectionType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LoadDataEsProjectionTaskDbAdapter implements
        LoadDataProjectionTaskPort
{
    private final DataEsProjectionTaskRepository repo;

    @Override
    public List<DataEsProjectionTaskEntity> findBatchForWork(
            LocalDateTime now,
            List<DataEsProjectionType> statuses,
            Pageable pageable
    ) {
        return repo.findBatchForWork(
                now,
                statuses,
                pageable
        );
    }
}
