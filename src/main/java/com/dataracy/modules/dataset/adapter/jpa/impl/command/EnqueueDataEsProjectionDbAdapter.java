package com.dataracy.modules.dataset.adapter.jpa.impl.command;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEsProjectionTaskEntity;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataEsProjectionTaskRepository;
import com.dataracy.modules.dataset.application.port.out.command.projection.EnqueueDataProjectionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnqueueDataEsProjectionDbAdapter implements EnqueueDataProjectionPort {
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
}
