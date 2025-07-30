package com.dataracy.modules.filestorage.application.port.in;

public interface DownloadFileUseCase {
    String generatePreSignedUrl(String s3Url, int expirationSeconds);
}
