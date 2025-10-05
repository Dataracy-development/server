package com.dataracy.modules.filestorage.application.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.multipart.MultipartFile;

import com.dataracy.modules.filestorage.adapter.thumbnail.ThumbnailGenerator;
import com.dataracy.modules.filestorage.application.port.out.FileStoragePort;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FileCommandServiceTest {

  @InjectMocks private FileCommandService service;

  @Mock private FileStoragePort fileStoragePort;

  @Mock private ThumbnailGenerator thumbnailGenerator;

  @Mock private MultipartFile multipartFile;

  @Nested
  @DisplayName("uploadFile 메서드 테스트")
  class UploadFileTest {

    @Test
    @DisplayName("파일 업로드 성공")
    void uploadFileSuccess() {
      // given
      String directory = "test-directory";
      String expectedUrl = "https://example.com/uploaded-file.jpg";

      given(fileStoragePort.upload(directory, multipartFile)).willReturn(expectedUrl);

      // when
      String result = service.uploadFile(directory, multipartFile);

      // then
      assertThat(result).isEqualTo(expectedUrl);
      then(fileStoragePort).should().upload(directory, multipartFile);
    }
  }

  @Nested
  @DisplayName("deleteFile 메서드 테스트")
  class DeleteFileTest {

    @Test
    @DisplayName("파일 삭제 성공")
    void deleteFileSuccess() {
      // given
      String fileUrl = "https://example.com/file-to-delete.jpg";

      // when
      service.deleteFile(fileUrl);

      // then
      then(fileStoragePort).should().delete(fileUrl);
    }
  }

  @Nested
  @DisplayName("replaceFile 메서드 테스트")
  class ReplaceFileTest {

    @Test
    @DisplayName("파일 교체 성공")
    void replaceFileSuccess() {
      // given
      String directory = "test-directory";
      String oldFileUrl = "https://example.com/old-file.jpg";
      String newFileUrl = "https://example.com/new-file.jpg";

      given(fileStoragePort.upload(directory, multipartFile)).willReturn(newFileUrl);

      // when
      String result = service.replaceFile(directory, multipartFile, oldFileUrl);

      // then
      assertThat(result).isEqualTo(newFileUrl);
      then(fileStoragePort).should().upload(directory, multipartFile);
      then(fileStoragePort).should().delete(oldFileUrl);
    }

    @Test
    @DisplayName("파일 교체 실패 - 새 파일 업로드 실패 시 기존 파일 삭제 안함")
    void replaceFileFailOnNewFileUpload() {
      // given
      String directory = "test-directory";
      String oldFileUrl = "https://example.com/old-file.jpg";
      MultipartFile newFile = mock(MultipartFile.class);

      given(fileStoragePort.upload(directory, newFile))
          .willThrow(new RuntimeException("Upload failed"));

      // when & then
      RuntimeException ex =
          catchThrowableOfType(
              () -> service.replaceFile(directory, newFile, oldFileUrl), RuntimeException.class);
      assertThat(ex.getMessage()).isEqualTo("Upload failed");

      then(fileStoragePort).should().upload(directory, newFile);
      then(fileStoragePort).should(never()).delete(oldFileUrl); // 업로드 실패 시 기존 파일 삭제 안함
    }
  }
}
