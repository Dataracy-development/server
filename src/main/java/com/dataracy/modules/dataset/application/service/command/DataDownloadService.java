package com.dataracy.modules.dataset.application.service.command;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.port.in.command.content.DownloadDataFileUseCase;
import com.dataracy.modules.dataset.application.port.out.query.extractor.FindDownloadDataFileUrlPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import com.dataracy.modules.filestorage.application.port.in.DownloadFileUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class DataDownloadService implements DownloadDataFileUseCase {
    private final FindDownloadDataFileUrlPort findDownloadDataFileUrlPort;

    private final DownloadFileUseCase downloadFileUseCase;

    /**
     * 데이터셋 파일의 S3 URL을 조회하여, 지정된 만료 시간(초) 동안 유효한 프리사인드 다운로드 URL을 반환합니다.
     *
     * @param dataId 다운로드할 데이터셋의 ID
     * @param expirationSeconds 프리사인드 URL의 만료 시간(초)
     * @return 생성된 프리사인드 S3 다운로드 URL
     * @throws DataException 데이터셋이 존재하지 않거나, 프리사인드 URL 생성에 실패한 경우 발생
     */
    @Override
    @Transactional(readOnly = true)
    public String download(Long dataId, int expirationSeconds) {
        Instant startTime = LoggerFactory.service().logStart("DownloadDataFileUseCase", "데이터셋 파일 다운로드 서비스 시작 dataId=" + dataId);
        String s3Url = findDownloadDataFileUrlPort.findDownloadedDataFileUrl(dataId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning("DownloadDataFileUseCase", "해당 데이터셋이 존재하지 않습니다. dataId=" + dataId);
                    return new DataException(DataErrorStatus.NOT_FOUND_DATA);
                });
        String preSignedUrl;
        try {
            preSignedUrl = downloadFileUseCase.generatePreSignedUrl(s3Url, expirationSeconds);
        } catch (Exception e) {
            LoggerFactory.service().logException("DownloadDataFileUseCase", "Pre-signed URL 생성 실패 dataId=" + dataId, e);
            throw new DataException(DataErrorStatus.DOWNLOAD_URL_GENERATION_FAILED);
        }
        LoggerFactory.service().logSuccess("DownloadDataFileUseCase", "데이터셋 파일 다운로드 서비스 종료 dataId=" + dataId, startTime);
        return preSignedUrl;
    }
}
