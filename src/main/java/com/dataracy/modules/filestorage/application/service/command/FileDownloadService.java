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

    /****
     * 지정된 S3 URL과 만료 시간(초)을 사용하여 사전 서명된(pre-signed) URL을 생성합니다.
     *
     * @param s3Url 사전 서명된 URL을 생성할 S3 객체의 URL
     * @param expirationSeconds URL의 만료 시간(초)
     * @return 생성된 사전 서명된 S3 URL
     * @throws IllegalArgumentException s3Url이 비어있거나 만료 시간이 0 이하인 경우
     */
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
