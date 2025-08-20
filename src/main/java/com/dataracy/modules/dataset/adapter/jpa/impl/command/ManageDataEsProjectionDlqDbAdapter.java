package com.dataracy.modules.dataset.adapter.jpa.impl.command;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEsProjectionDlqEntity;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataEsProjectionDlqRepository;
import com.dataracy.modules.dataset.application.port.out.command.projection.ManageDataProjectionDlqPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManageDataEsProjectionDlqDbAdapter implements ManageDataProjectionDlqPort {
    private final DataEsProjectionDlqRepository dlqRepo;

    @Override
    public void save(
            Long dataId,
            Integer deltaDownload,
            Boolean setDeleted,
            String lastError
    ) {
        dlqRepo.save(DataEsProjectionDlqEntity.builder()
                .dataId(dataId)
                .deltaDownload(deltaDownload)
                .setDeleted(setDeleted)
                .lastError(lastError)
                .build());
    }
}
