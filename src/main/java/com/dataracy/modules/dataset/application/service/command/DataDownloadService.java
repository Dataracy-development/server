package com.dataracy.modules.dataset.application.service.command;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.dto.response.download.GetDataPreSignedUrlResponse;
import com.dataracy.modules.dataset.application.port.in.command.content.DownloadDataFileUseCase;
import com.dataracy.modules.dataset.application.port.out.command.projection.ManageDataProjectionTaskPort;
import com.dataracy.modules.dataset.application.port.out.command.update.UpdateDataDownloadPort;
import com.dataracy.modules.dataset.application.port.out.query.extractor.FindDownloadDataFileUrlPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import com.dataracy.modules.filestorage.application.port.in.DownloadFileUseCase;

@Service
public class DataDownloadService implements DownloadDataFileUseCase {
  private final FindDownloadDataFileUrlPort findDownloadDataFileUrlPort;
  private final UpdateDataDownloadPort updateDataDownloadDbPort;
  private final ManageDataProjectionTaskPort manageDataProjectionTaskPort;

  private final DownloadFileUseCase downloadFileUseCase;

  // Use Case 상수 정의
  private static final String DOWNLOAD_DATA_FILE_USE_CASE = "DownloadDataFileUseCase";

  // 메시지 상수 정의
  private static final String DATA_NOT_FOUND_MESSAGE = "해당 데이터셋이 존재하지 않습니다. dataId=";

  /**
   * DataDownloadService의 인스턴스를 생성합니다.
   *
   * <p>데이터 파일 URL 조회 포트, 다운로드 카운트 갱신 포트, 데이터 프로젝션 작업 관리 포트, 및 파일 다운로드 유스케이스를 주입하여 클래스가 다운로드 URL 생성과
   * 관련된 작업을 수행할 수 있게 합니다.
   */
  public DataDownloadService(
      FindDownloadDataFileUrlPort findDownloadDataFileUrlPort,
      @Qualifier("updateDataDownloadDbAdapter") UpdateDataDownloadPort updateDataDownloadDbPort,
      ManageDataProjectionTaskPort manageDataProjectionTaskPort,
      DownloadFileUseCase downloadFileUseCase) {
    this.findDownloadDataFileUrlPort = findDownloadDataFileUrlPort;
    this.updateDataDownloadDbPort = updateDataDownloadDbPort;
    this.manageDataProjectionTaskPort = manageDataProjectionTaskPort;
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
    Instant startTime =
        LoggerFactory.service()
            .logStart(DOWNLOAD_DATA_FILE_USE_CASE, "데이터셋 파일 다운로드 서비스 시작 dataId=" + dataId);
    String s3Url =
        findDownloadDataFileUrlPort
            .findDownloadedDataFileUrl(dataId)
            .orElseThrow(
                () -> {
                  LoggerFactory.service()
                      .logWarning(DOWNLOAD_DATA_FILE_USE_CASE, DATA_NOT_FOUND_MESSAGE + dataId);
                  return new DataException(DataErrorStatus.NOT_FOUND_DATA);
                });

    GetDataPreSignedUrlResponse getDataPresignedUrlResponse;
    try {
      String preSignedUrl =
          downloadFileUseCase.generatePreSignedUrl(s3Url, expirationSeconds).preSignedUrl();
      getDataPresignedUrlResponse = new GetDataPreSignedUrlResponse(preSignedUrl);
    } catch (Exception e) {
      LoggerFactory.service()
          .logException(DOWNLOAD_DATA_FILE_USE_CASE, "Pre-signed URL 생성 실패 dataId=" + dataId, e);
      throw new DataException(DataErrorStatus.DOWNLOAD_URL_GENERATION_FAILED);
    }

    // DB만 확정
    updateDataDownloadDbPort.increaseDownloadCount(dataId);
    // 큐 적재
    manageDataProjectionTaskPort.enqueueDownloadDelta(dataId, +1);

    LoggerFactory.service()
        .logSuccess(DOWNLOAD_DATA_FILE_USE_CASE, "데이터셋 파일 다운로드 서비스 종료 dataId=" + dataId, startTime);
    return getDataPresignedUrlResponse;
  }
}
