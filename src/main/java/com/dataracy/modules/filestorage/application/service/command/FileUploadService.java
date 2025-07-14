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
public class FileUploadService implements FileUploadUseCase {

    private final FileStoragePort fileStoragePort;
    private final ThumbnailGenerator thumbnailGenerator;

    /**
     * 지정된 디렉터리에 MultipartFile을 업로드하고 업로드된 파일의 URL을 반환합니다.
     *
     * @param directory 파일을 업로드할 디렉터리 경로
     * @param file 업로드할 파일
     * @return 업로드된 파일의 URL
     */
    @Override
    public String uploadFile(String directory, MultipartFile file) {
        String url = fileStoragePort.upload(directory, file);
        log.info("[파일 업로드 성공] url={}", url);
        return url;
    }

    /**
     * 지정된 파일 URL에 해당하는 파일을 삭제합니다.
     *
     * @param fileUrl 삭제할 파일의 URL
     */
    @Override
    public void deleteFile(String fileUrl) {
        fileStoragePort.delete(fileUrl);
        log.info("[파일 삭제 성공] {}", fileUrl);
    }

    /**
     * 기존 파일을 새 파일로 교체하고 새 파일의 URL을 반환합니다.
     *
     * @param directory 새 파일을 업로드할 디렉터리 경로
     * @param newFile 업로드할 새 파일
     * @param oldFileUrl 교체할 기존 파일의 URL
     * @return 새로 업로드된 파일의 URL
     */
    @Override
    public String replaceFile(String directory, MultipartFile newFile, String oldFileUrl) {
        String newUrl = uploadFile(directory, newFile);
        deleteFile(oldFileUrl);
        return newUrl;
    }

    /**
     * 원본 이미지 파일로부터 지정된 크기의 썸네일을 생성하여 해당 디렉토리에 업로드하고, 업로드된 썸네일의 URL을 반환합니다.
     *
     * @param original  썸네일을 생성할 원본 이미지 파일
     * @param directory 썸네일을 업로드할 디렉토리 경로
     * @param fileName  업로드할 썸네일 파일명
     * @param width     생성할 썸네일의 너비
     * @param height    생성할 썸네일의 높이
     * @return 업로드된 썸네일 파일의 URL
     */
    public String createThumbnail(MultipartFile original, String directory, String fileName, int width, int height) {
        ByteArrayOutputStream baos = thumbnailGenerator.createThumbnail(original, width, height);
        MultipartFile thumb = convertToMultipartFile(baos, fileName, original.getContentType());
        String thumbUrl = fileStoragePort.upload(directory, thumb);
        log.info("[썸네일 업로드 성공] {}", thumbUrl);
        return thumbUrl;
    }

    /**
     * 주어진 바이트 배열 출력 스트림을 파일 이름과 콘텐츠 타입과 함께 MultipartFile 객체로 변환합니다.
     *
     * @param baos 변환할 바이트 배열 출력 스트림
     * @param filename MultipartFile에 설정할 파일 이름
     * @param contentType MultipartFile에 설정할 콘텐츠 타입
     * @return 변환된 MultipartFile 객체
     */
    private MultipartFile convertToMultipartFile(ByteArrayOutputStream baos, String filename, String contentType) {
        return new CustomMultipartFile(
                baos.toByteArray(),
                "thumbnail",
                filename,
                contentType
        );
    }
}
