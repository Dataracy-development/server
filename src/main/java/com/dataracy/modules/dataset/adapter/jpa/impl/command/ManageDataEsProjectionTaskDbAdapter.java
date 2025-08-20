package com.dataracy.modules.dataset.adapter.jpa.impl.command;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEsProjectionTaskEntity;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataEsProjectionTaskRepository;
import com.dataracy.modules.dataset.application.port.out.command.projection.ManageDataProjectionTaskPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManageDataEsProjectionTaskDbAdapter implements ManageDataProjectionTaskPort {
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
    public void delete(DataEsProjectionTaskEntity dataEsProjectionTask) {
        repo.delete(dataEsProjectionTask);
    }
}
