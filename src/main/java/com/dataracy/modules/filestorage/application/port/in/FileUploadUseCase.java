package com.dataracy.modules.filestorage.application.port.in;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadUseCase {
    String uploadFile(String directory, MultipartFile file);
    void deleteFile(String fileUrl);
    String replaceFile(String directory, MultipartFile newFile, String oldFileUrl);
}
