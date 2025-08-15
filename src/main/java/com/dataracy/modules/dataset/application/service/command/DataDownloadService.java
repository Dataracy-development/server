package com.dataracy.modules.dataset.application.service.command;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.dto.response.download.GetDataPreSignedUrlResponse;
import com.dataracy.modules.dataset.application.port.in.command.content.DownloadDataFileUseCase;
import com.dataracy.modules.dataset.application.port.out.command.projection.EnqueueDataProjectionPort;
import com.dataracy.modules.dataset.application.port.out.command.update.UpdateDataDownloadPort;
import com.dataracy.modules.dataset.application.port.out.query.extractor.FindDownloadDataFileUrlPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import com.dataracy.modules.filestorage.application.port.in.DownloadFileUseCase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class DataDownloadService implements DownloadDataFileUseCase {
    private final FindDownloadDataFileUrlPort findDownloadDataFileUrlPort;
    private final UpdateDataDownloadPort updateDataDownloadDbPort;
    private final EnqueueDataProjectionPort enqueueDataProjectionPort;

    private final DownloadFileUseCase downloadFileUseCase;

    public DataDownloadService(
            FindDownloadDataFileUrlPort findDownloadDataFileUrlPort,
            @Qualifier("updateDataDownloadDbAdapter") UpdateDataDownloadPort updateDataDownloadDbPort,
            EnqueueDataProjectionPort enqueueDataProjectionPort,
            DownloadFileUseCase downloadFileUseCase
    ) {
        this.findDownloadDataFileUrlPort = findDownloadDataFileUrlPort;
        this.updateDataDownloadDbPort = updateDataDownloadDbPort;
        this.enqueueDataProjectionPort = enqueueDataProjectionPort;
        this.downloadFileUseCase = downloadFileUseCase;
    }

    /**
     * 데이터셋 파일의 S3 URL을 조회하여, 지정한 만료 시간(초) 동안 유효한 preSigned 다운로드 URL을 생성하여 반환합니다.
     *
     * @param dataId 다운로드할 데이터셋의 고유 ID
     * @param expirationSeconds preSigned URL의 만료 시간(초)
     * @return preSigned S3 다운로드 URL이 포함된 응답 객체
     * @throws DataException 데이터셋이 존재하지 않거나 preSigned URL 생성에 실패한 경우 발생
     */
    @Override
    @Transactional
    public GetDataPreSignedUrlResponse downloadDataFile(Long dataId, int expirationSeconds) {
        Instant startTime = LoggerFactory.service().logStart("DownloadDataFileUseCase", "데이터셋 파일 다운로드 서비스 시작 dataId=" + dataId);
        String s3Url = findDownloadDataFileUrlPort.findDownloadedDataFileUrl(dataId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning("DownloadDataFileUseCase", "해당 데이터셋이 존재하지 않습니다. dataId=" + dataId);
                    return new DataException(DataErrorStatus.NOT_FOUND_DATA);
                });

        GetDataPreSignedUrlResponse getDataPresignedUrlResponse;
        try {
            String preSignedUrl = downloadFileUseCase.generatePreSignedUrl(s3Url, expirationSeconds).preSignedUrl();
            getDataPresignedUrlResponse = new GetDataPreSignedUrlResponse(preSignedUrl);
        } catch (Exception e) {
            LoggerFactory.service().logException("DownloadDataFileUseCase", "Pre-signed URL 생성 실패 dataId=" + dataId, e);
            throw new DataException(DataErrorStatus.DOWNLOAD_URL_GENERATION_FAILED);
        }

        // DB만 확정
        updateDataDownloadDbPort.increaseDownloadCount(dataId);
        // 큐 적재
        enqueueDataProjectionPort.enqueueDownloadDelta(dataId, +1);

        LoggerFactory.service().logSuccess("DownloadDataFileUseCase", "데이터셋 파일 다운로드 서비스 종료 dataId=" + dataId, startTime);
        return getDataPresignedUrlResponse;
    }
}
