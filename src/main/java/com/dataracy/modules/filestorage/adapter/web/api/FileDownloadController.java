package com.dataracy.modules.filestorage.adapter.web.api;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.status.CommonSuccessStatus;
import com.dataracy.modules.filestorage.application.port.in.DownloadFileUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FileDownloadController implements FileApi {

    private final DownloadFileUseCase downloadFileUseCase;

    /**
     * 지정된 S3 URL과 만료 시간(초)로 파일 다운로드를 위한 프리사인드 URL을 반환합니다.
     *
     * @param s3Url 프리사인드 URL을 생성할 S3 파일의 URL
     * @param expirationSeconds 프리사인드 URL의 만료 시간(초)
     * @return 프리사인드 URL이 포함된 성공 응답 객체의 HTTP 200 OK 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<String>> getPreSignedUrl(String s3Url, int expirationSeconds) {
        String url = downloadFileUseCase.generatePreSignedUrl(s3Url, expirationSeconds);
        return ResponseEntity.ok(SuccessResponse.of(CommonSuccessStatus.OK, url));
    }
}
