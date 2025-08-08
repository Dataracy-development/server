package com.dataracy.modules.filestorage.application.service.command;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.filestorage.application.port.in.DownloadFileUseCase;
import com.dataracy.modules.filestorage.application.port.out.FileStoragePort;
import lombok.RequiredArgsConstructor;
import nl.basjes.shaded.org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class FileDownloadService implements DownloadFileUseCase {

    private final FileStoragePort fileStoragePort;

    /**
     * 지정된 S3 객체 URL과 만료 시간을 기반으로 사전 서명된(pre-signed) S3 URL을 생성합니다.
     *
     * @param s3Url 사전 서명된 URL을 생성할 S3 객체의 URL
     * @param expirationSeconds URL의 만료 시간(초)
     * @return 생성된 사전 서명된 S3 URL
     * @throws IllegalArgumentException s3Url이 비어있거나 만료 시간이 0 이하인 경우
     */
    @Override
    public String generatePreSignedUrl(String s3Url, int expirationSeconds) {
        Instant startTime = LoggerFactory.service().logStart("DownloadFileUseCase", "서명된(pre-signed) URL을 생성 서비스 시작");
        if (!StringUtils.hasText(s3Url)) {
            LoggerFactory.service().logWarning("DownloadFileUseCase", "S3 URL은 비어있을 수 없습니다.");
            throw new IllegalArgumentException("S3 URL은 비어있을 수 없습니다.");
        }
        if (expirationSeconds <= 0) {
            LoggerFactory.service().logWarning("DownloadFileUseCase", "만료 시간은 0보다 커야 합니다.");
            throw new IllegalArgumentException("만료 시간은 0보다 커야 합니다.");
        }
        String preSignedUrl = fileStoragePort.getPreSignedUrl(s3Url, expirationSeconds);
        LoggerFactory.service().logSuccess("DownloadFileUseCase", "서명된(pre-signed) URL을 생성 서비스 종료", startTime);
        return preSignedUrl;
    }
}
