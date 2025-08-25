package com.dataracy.modules.filestorage.application.service.command;

import com.dataracy.modules.filestorage.adapter.thumbnail.ThumbnailGenerator;
import com.dataracy.modules.filestorage.application.port.out.FileStoragePort;
import org.junit.jupiter.api.DisplayName;
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

    @Mock FileStoragePort fileStoragePort;
    @Mock ThumbnailGenerator thumbnailGenerator;
    @Mock MultipartFile file;
    @Mock MultipartFile newFile;

    @InjectMocks FileCommandService service;

    @Captor ArgumentCaptor<MultipartFile> multipartCaptor;

    @Test
    @DisplayName("uploadFile_should_delegate_to_port_and_return_url")
    void uploadFile_should_delegate_to_port_and_return_url() {
        // given
        given(fileStoragePort.upload("data/1", file))
                .willReturn("https://bucket/data/1/uuid.jpg");

        // when
        String url = service.uploadFile("data/1", file);

        // then
        assertThat(url).isEqualTo("https://bucket/data/1/uuid.jpg");
        then(fileStoragePort).should().upload("data/1", file);
    }

    @Test
    @DisplayName("deleteFile_should_delegate_to_port")
    void deleteFile_should_delegate_to_port() {
        // given
        String url = "https://bucket/k";

        // when
        service.deleteFile(url);

        // then
        then(fileStoragePort).should().delete(url);
    }

    @Test
    @DisplayName("replaceFile_should_upload_new_and_delete_old")
    void replaceFile_should_upload_new_and_delete_old() {
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

    @Test
    @DisplayName("createThumbnail_should_generate_and_upload_thumbnail")
    void createThumbnail_should_generate_and_upload_thumbnail() {
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
