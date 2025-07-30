package com.dataracy.modules.filestorage.application.service.command;

import com.dataracy.modules.filestorage.application.port.in.DownloadFileUseCase;
import com.dataracy.modules.filestorage.application.port.out.FileStoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.basjes.shaded.org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileDownloadService implements DownloadFileUseCase {

    private final FileStoragePort fileStoragePort;

    @Override
    public String generatePreSignedUrl(String s3Url, int expirationSeconds) {
        if (!StringUtils.hasText(s3Url)) {
            throw new IllegalArgumentException("S3 URL은 비어있을 수 없습니다");
        }
        if (expirationSeconds <= 0) {
            throw new IllegalArgumentException("만료 시간은 0보다 커야 합니다");
        }
        log.debug("Pre-signed URL 생성 요청: s3Url={}, expirationSeconds={}", s3Url, expirationSeconds);
        return fileStoragePort.getPreSignedUrl(s3Url, expirationSeconds);
    }
}
