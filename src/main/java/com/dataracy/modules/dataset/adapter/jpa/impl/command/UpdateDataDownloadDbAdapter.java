package com.dataracy.modules.dataset.adapter.jpa.impl.command;

import com.dataracy.modules.dataset.adapter.jpa.repository.DataJpaRepository;
import com.dataracy.modules.dataset.application.port.out.command.update.UpdateDataDownloadPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("updateDataDownloadDbAdapter")
@RequiredArgsConstructor
public class UpdateDataDownloadDbAdapter implements UpdateDataDownloadPort {
    private final DataJpaRepository repo;

    @Override
    @Transactional
    public void increaseDownloadCount(Long dataId) {
        repo.increaseDownload(dataId);
    }
}
