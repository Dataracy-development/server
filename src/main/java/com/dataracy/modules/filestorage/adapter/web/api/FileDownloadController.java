package com.dataracy.modules.filestorage.adapter.web.api;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.status.CommonSuccessStatus;
import com.dataracy.modules.filestorage.adapter.web.mapper.FileDownloadWebMapper;
import com.dataracy.modules.filestorage.adapter.web.response.GetPresignedUrlWebResponse;
import com.dataracy.modules.filestorage.application.dto.response.GetPresignedUrlResponse;
import com.dataracy.modules.filestorage.application.port.in.DownloadFileUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
public class FileDownloadController implements FileApi {
    private final FileDownloadWebMapper fileDownloadWebMapper;

    private final DownloadFileUseCase downloadFileUseCase;

    /**
     * 지정된 S3 파일 URL과 만료 시간(초)을 이용해 파일 다운로드용 프리사인드 URL을 반환합니다.
     *
     * @param s3Url 프리사인드 URL을 생성할 S3 파일의 URL
     * @param expirationSeconds 프리사인드 URL의 만료 시간(초)
     * @return preSigned URL이 포함된 성공 응답의 HTTP 200 OK 객체
     */
    @Override
    public ResponseEntity<SuccessResponse<GetPresignedUrlWebResponse>> getPreSignedUrl(String s3Url, int expirationSeconds) {
        Instant startTime = LoggerFactory.api().logRequest("[GetPreSignedUrl] 파일 다운로드를 위한 프리사인드 URL 반환 API 요청 시작");
        GetPresignedUrlWebResponse webResponse;

        try {
            GetPresignedUrlResponse responseDto = downloadFileUseCase.generatePreSignedUrl(s3Url, expirationSeconds);
            webResponse = fileDownloadWebMapper.toWebDto(responseDto);
        } finally {
            LoggerFactory.api().logResponse("[GetPreSignedUrl] 파일 다운로드를 위한 preSigned URL 반환 API 응답 완료", startTime);
        }

        return ResponseEntity.ok(SuccessResponse.of(CommonSuccessStatus.OK, webResponse));
    }
}
