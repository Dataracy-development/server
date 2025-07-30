package com.dataracy.modules.filestorage.application.service.command;

import com.dataracy.modules.filestorage.application.port.in.DownloadFileUseCase;
import com.dataracy.modules.filestorage.application.port.out.FileStoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileDownloadService implements DownloadFileUseCase {

    private final FileStoragePort fileStoragePort;

    @Override
    public String generatePreSignedUrl(String s3Url, int expirationSeconds) {
        return fileStoragePort.getPreSignedUrl(s3Url, expirationSeconds);
    }
}
