package com.dataracy.modules.filestorage.application.service.command;

import com.dataracy.modules.filestorage.adapter.thumbnail.ThumbnailGenerator;
import com.dataracy.modules.filestorage.application.port.out.FileStoragePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class FileCommandServiceTest {

    @Mock
    private FileStoragePort fileStoragePort;

    @Mock
    private ThumbnailGenerator thumbnailGenerator;

    @Mock
    private MultipartFile file;

    @Mock
    private MultipartFile newFile;

    @InjectMocks
    private FileCommandService service;

    @Captor
    private ArgumentCaptor<MultipartFile> multipartCaptor;

    @Nested
    @DisplayName("파일 업로드")
    class UploadFile {

        @Test
        @DisplayName("성공: FileStoragePort.upload 호출 후 URL 반환")
        void shouldDelegateAndReturnUrl() {
            // given
            given(fileStoragePort.upload("data/1", file))
                    .willReturn("https://bucket/data/1/uuid.jpg");

            // when
            String url = service.uploadFile("data/1", file);

            // then
            assertThat(url).isEqualTo("https://bucket/data/1/uuid.jpg");
            then(fileStoragePort).should().upload("data/1", file);
        }
    }

    @Nested
    @DisplayName("파일 삭제")
    class DeleteFile {

        @Test
        @DisplayName("성공: FileStoragePort.delete 호출")
        void shouldDelegateDelete() {
            // given
            String url = "https://bucket/k";

            // when
            service.deleteFile(url);

            // then
            then(fileStoragePort).should().delete(url);
        }
    }

    @Nested
    @DisplayName("새 파일 업로드")
    class ReplaceFile {

        @Test
        @DisplayName("성공: 새 파일 업로드 후 기존 파일 삭제")
        void shouldUploadNewAndDeleteOld() {
            // given
            String directory = "project/99";
            String oldUrl = "https://bucket/old";
            given(fileStoragePort.upload(directory, newFile))
                    .willReturn("https://bucket/new");

            // when
            String newUrl = service.replaceFile(directory, newFile, oldUrl);

            // then
            assertThat(newUrl).isEqualTo("https://bucket/new");
            then(fileStoragePort).should().upload(directory, newFile);
            then(fileStoragePort).should().delete(oldUrl);
        }
    }

    @Nested
    @DisplayName("썸네일 이미지 업로드")
    class CreateThumbnail {

        @Test
        @DisplayName("성공: 썸네일 생성 후 FileStoragePort.upload 호출")
        void shouldGenerateAndUploadThumbnail() {
            // given
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.writeBytes(new byte[]{1, 2, 3});

            given(thumbnailGenerator.createThumbnail(file, 200, 100)).willReturn(baos);
            given(file.getContentType()).willReturn("image/jpeg");
            given(fileStoragePort.upload(eq("thumb-dir"), any(MultipartFile.class)))
                    .willReturn("https://bucket/thumb.jpg");

            // when
            String url = service.createThumbnail(file, "thumb-dir", "thumb.jpg", 200, 100);

            // then
            assertThat(url).isEqualTo("https://bucket/thumb.jpg");
            then(thumbnailGenerator).should().createThumbnail(file, 200, 100);
            then(fileStoragePort).should().upload(eq("thumb-dir"), multipartCaptor.capture());

            MultipartFile captured = multipartCaptor.getValue();
            assertThat(captured.getOriginalFilename()).isEqualTo("thumb.jpg");
            assertThat(captured.getContentType()).isEqualTo("image/jpeg");
        }
    }
}
