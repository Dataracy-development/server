package com.dataracy.modules.imagestorage.application.port.out;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStoragePort {
    String upload(String directory, MultipartFile file);
    void delete(String fileUrl);
    String getUrl(String key);
}
