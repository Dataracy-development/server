package com.dataracy.modules.filestorage.application.service.command;

import com.dataracy.modules.filestorage.adapter.thumbnail.ThumbnailGenerator;
import com.dataracy.modules.filestorage.application.port.in.FileUploadUseCase;
import com.dataracy.modules.filestorage.application.port.out.FileStoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FileUploadService implements FileUploadUseCase {

    private final FileStoragePort fileStoragePort;
    private final ThumbnailGenerator thumbnailGenerator;

    @Override
    public String uploadFile(String directory, MultipartFile file) {
        String originalUrl = fileStoragePort.upload(directory, file);
        log.info("[이미지 업로드 성공] url={}", originalUrl);
        return originalUrl;
    }

    @Override
    public void deleteFile(String fileUrl) {
        fileStoragePort.delete(fileUrl);
        log.info("[이미지 삭제 완료] {}", fileUrl);
    }

    public void createThumbnail(MultipartFile file, String directory, String fileName, int width, int height) {
        ByteArrayOutputStream thumbnailOutput = thumbnailGenerator.createThumbnail(file, width, height);
        MultipartFile thumbnailFile = convertToMultipartFile(thumbnailOutput, fileName, file.getContentType());
        String thumbUrl = fileStoragePort.upload(directory, thumbnailFile);
        log.info("[썸네일 생성 및 업로드] thumbUrl={}", thumbUrl);
    }

    private MultipartFile convertToMultipartFile(ByteArrayOutputStream baos, String filename, String contentType) {
        return new MultipartFile() {
            @Override public String getName() { return filename; }
            @Override public String getOriginalFilename() { return filename; }
            @Override public String getContentType() { return contentType; }
            @Override public boolean isEmpty() { return baos.size() == 0; }
            @Override public long getSize() { return baos.size(); }
            @Override public byte[] getBytes() throws IOException { return baos.toByteArray(); }
            @Override public InputStream getInputStream() { return new ByteArrayInputStream(baos.toByteArray()); }
            @Override public void transferTo(File dest) throws IOException, IllegalStateException {
                try (FileOutputStream fos = new FileOutputStream(dest)) {
                    baos.writeTo(fos);
                }
            }
        };
    }
}
