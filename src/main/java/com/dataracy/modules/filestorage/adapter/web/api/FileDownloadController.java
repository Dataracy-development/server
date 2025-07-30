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

    @Override
    public ResponseEntity<SuccessResponse<String>> getPreSignedUrl(String s3Url, int expirationSeconds) {
        String url = downloadFileUseCase.generatePreSignedUrl(s3Url, expirationSeconds);
        return ResponseEntity.ok(SuccessResponse.of(CommonSuccessStatus.OK, url));
    }
}
