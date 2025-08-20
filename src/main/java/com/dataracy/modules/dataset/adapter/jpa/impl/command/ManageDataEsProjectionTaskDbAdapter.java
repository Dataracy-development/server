package com.dataracy.modules.dataset.adapter.jpa.impl.command;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEsProjectionTaskEntity;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataEsProjectionTaskRepository;
import com.dataracy.modules.dataset.application.port.out.command.projection.ManageDataProjectionTaskPort;
import com.dataracy.modules.dataset.application.port.out.query.projection.LoadDataProjectionTaskPort;
import com.dataracy.modules.dataset.domain.enums.DataEsProjectionType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ManageDataEsProjectionTaskDbAdapter implements
        ManageDataProjectionTaskPort,
        LoadDataProjectionTaskPort
{
    private final DataEsProjectionTaskRepository repo;

    @Override
    public void enqueueSetDeleted(Long dataId, boolean deleted) {
        repo.save(DataEsProjectionTaskEntity.builder()
                .dataId(dataId)
                .setDeleted(deleted)
                .build());
    }

    @Override
    public void enqueueDownloadDelta(Long dataId, int deltaDownload) {
        repo.save(DataEsProjectionTaskEntity.builder()
                .dataId(dataId)
                .deltaDownload(deltaDownload)
                .build());
    }

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

    @Override
    public void delete(DataEsProjectionTaskEntity dataEsProjectionTask) {
        repo.delete(dataEsProjectionTask);
    }
}
