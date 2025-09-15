package com.dataracy.modules.filestorage.application.port.in;

import com.dataracy.modules.filestorage.application.dto.response.GetPreSignedUrlResponse;

public interface DownloadFileUseCase {
    /**
     * 지정된 S3 파일 URL에 대해 주어진 만료 시간(초) 동안 유효한 PreSigned 다운로드 URL 정보를 생성합니다.
     *
     * @param s3Url PreSigned URL을 생성할 S3 파일의 URL
     * @param expirationSeconds PreSigned URL의 만료 시간(초)
     * @return PreSigned 다운로드 URL 및 관련 정보를 담은 응답 객체
     */
    GetPreSignedUrlResponse generatePreSignedUrl(String s3Url, int expirationSeconds);
}
