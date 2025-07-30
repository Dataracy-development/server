package com.dataracy.modules.dataset.application.port.in;

public interface DownloadDatasetFileUseCase {
    String download(Long dataId, int expirationSeconds);
}
