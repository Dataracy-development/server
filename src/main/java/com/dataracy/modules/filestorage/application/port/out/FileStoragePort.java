package com.dataracy.modules.filestorage.application.port.out;

import org.springframework.web.multipart.MultipartFile;

public interface FileStoragePort {
    String upload(String directory, MultipartFile file);
    void delete(String fileUrl);
    String getUrl(String key);
}
