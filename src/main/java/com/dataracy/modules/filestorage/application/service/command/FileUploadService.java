package com.dataracy.modules.filestorage.application.service.command;

import com.dataracy.modules.filestorage.adapter.s3.CustomMultipartFile;
import com.dataracy.modules.filestorage.adapter.thumbnail.ThumbnailGenerator;
import com.dataracy.modules.filestorage.application.port.in.FileUploadUseCase;
import com.dataracy.modules.filestorage.application.port.out.FileStoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FileUploadService implements FileUploadUseCase {

    private final FileStoragePort fileStoragePort;
    private final ThumbnailGenerator thumbnailGenerator;

    @Override
    public String uploadFile(String directory, MultipartFile file) {
        String url = fileStoragePort.upload(directory, file);
        log.info("[파일 업로드 성공] url={}", url);
        return url;
    }

    @Override
    public void deleteFile(String fileUrl) {
        fileStoragePort.delete(fileUrl);
        log.info("[파일 삭제 성공] {}", fileUrl);
    }

    @Override
    public String replaceFile(String directory, MultipartFile newFile, String oldFileUrl) {
        String newUrl = uploadFile(directory, newFile);
        deleteFile(oldFileUrl);
        return newUrl;
    }

    public String createThumbnail(MultipartFile original, String directory, String fileName, int width, int height) {
        ByteArrayOutputStream baos = thumbnailGenerator.createThumbnail(original, width, height);
        MultipartFile thumb = convertToMultipartFile(baos, fileName, original.getContentType());
        String thumbUrl = fileStoragePort.upload(directory, thumb);
        log.info("[썸네일 업로드 성공] {}", thumbUrl);
        return thumbUrl;
    }

    private MultipartFile convertToMultipartFile(ByteArrayOutputStream baos, String filename, String contentType) {
        return new CustomMultipartFile(
                baos.toByteArray(),
                filename,
                filename,
                contentType
        );
    }
}
