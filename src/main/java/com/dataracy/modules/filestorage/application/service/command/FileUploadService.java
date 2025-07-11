package com.dataracy.modules.filestorage.application.service.command;

import com.dataracy.modules.filestorage.application.port.in.FileUploadUseCase;
import com.dataracy.modules.filestorage.application.port.out.FileStoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class FileUploadService implements FileUploadUseCase {

    private final FileStoragePort fileStoragePort;

    @Override
    public String uploadFile(String directory, MultipartFile file) {
        return fileStoragePort.upload(directory, file);
    }

    @Override
    public void deleteFile(String fileUrl) {
        fileStoragePort.delete(fileUrl);
    }
}
