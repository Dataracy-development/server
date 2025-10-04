package com.dataracy.modules.filestorage.adapter.s3;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.dataracy.modules.filestorage.config.FileStorageProperties;
import com.dataracy.modules.filestorage.domain.exception.S3UploadException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AwsS3FileStorageAdapterTest {

  @Mock private AmazonS3 amazonS3;

  @Mock private FileStorageProperties fileStorageProperties;

  @Mock private MultipartFile file;

  @InjectMocks private AwsS3FileStorageAdapter adapter;

  @Captor private ArgumentCaptor<PutObjectRequest> putCaptor;

  @BeforeEach
  void init() {
    ReflectionTestUtils.setField(adapter, "bucket", "my-bucket");

    // FileStorageProperties 모킹 설정 (기본값으로 설정)
    FileStorageProperties.FileSize fileSize = new FileStorageProperties.FileSize();
    fileSize.setMultipartThreshold(20 * 1024L * 1024L); // 20MB
    fileSize.setStreamingThreshold(5 * 1024L * 1024L); // 5MB

    FileStorageProperties.Multipart multipart = new FileStorageProperties.Multipart();
    multipart.setChunkSize(5 * 1024L * 1024L); // 5MB

    FileStorageProperties.Buffer buffer = new FileStorageProperties.Buffer();
    buffer.setDefaultSize(8192); // 8KB
    buffer.setStreamingSize(16384); // 16KB

    // lenient()를 사용하여 불필요한 스터빙 경고 방지
    doReturn(fileSize).when(fileStorageProperties).getFileSize();
    doReturn(multipart).when(fileStorageProperties).getMultipart();
    doReturn(buffer).when(fileStorageProperties).getBuffer();
  }

  @Nested
  @DisplayName("upload 메서드")
  class Upload {

    @Test
    @DisplayName("성공: putObject 호출 후 업로드 URL 반환")
    void shouldPutObjectAndReturnUrl() throws Exception {
      // given
      given(file.getSize()).willReturn(3L);
      given(file.getContentType()).willReturn("image/jpeg");
      given(file.getInputStream()).willReturn(new ByteArrayInputStream(new byte[] {1, 2, 3}));
      given(amazonS3.getUrl("my-bucket", "k")).willReturn(new URL("https://bucket/k"));

      // when
      String url = adapter.upload("k", file);

      // then
      assertThat(url).isEqualTo("https://bucket/k");
      then(amazonS3).should().putObject(putCaptor.capture());

      PutObjectRequest req = putCaptor.getValue();
      ObjectMetadata md = req.getMetadata();
      assertAll(
          () -> assertThat(req.getBucketName()).isEqualTo("my-bucket"),
          () -> assertThat(req.getKey()).isEqualTo("k"),
          () -> assertThat(md.getContentLength()).isEqualTo(3L),
          () -> assertThat(md.getContentType()).isEqualTo("image/jpeg"));
    }
  }

  @Nested
  @DisplayName("download 메서드")
  class Download {

    @Test
    @DisplayName("성공: S3ObjectInputStream 반환")
    void shouldReturnStreamFromS3() throws Exception {
      // given
      String url = "https://bucket/k";
      given(amazonS3.getUrl("my-bucket", "")).willReturn(new URL("https://bucket/"));

      S3Object s3Object = mock(S3Object.class);
      given(amazonS3.getObject("my-bucket", "k")).willReturn(s3Object);

      S3ObjectInputStream s3is =
          new S3ObjectInputStream(
              new ByteArrayInputStream("abc".getBytes(StandardCharsets.UTF_8)), null);
      given(s3Object.getObjectContent()).willReturn(s3is);

      // when
      InputStream is = adapter.download(url);

      // then
      assertThat(is.readAllBytes()).isEqualTo("abc".getBytes(StandardCharsets.UTF_8));
      then(amazonS3).should().getObject("my-bucket", "k");
    }
  }

  @Nested
  @DisplayName("delete 메서드")
  class Delete {

    @Test
    @DisplayName("성공: deleteObject 호출")
    void shouldCallDeleteObject() throws Exception {
      // given
      String url = "https://bucket/k";
      given(amazonS3.getUrl("my-bucket", "")).willReturn(new URL("https://bucket/"));

      // when
      adapter.delete(url);

      // then
      then(amazonS3).should().deleteObject("my-bucket", "k");
    }
  }

  @Nested
  @DisplayName("validateProperties 메서드")
  class ValidateProperties {

    @Test
    @DisplayName("실패: bucket 비어있으면 S3UploadException 발생")
    void shouldThrowWhenBucketBlank() {
      // given
      ReflectionTestUtils.setField(adapter, "bucket", "   ");

      // when
      S3UploadException ex =
          catchThrowableOfType(adapter::validateProperties, S3UploadException.class);

      // then
      assertThat(ex).isNotNull();
    }

    @Test
    @DisplayName("성공: bucket 세팅되어 있으면 통과")
    void shouldPassWhenBucketValid() {
      // when & then
      assertThatCode(() -> adapter.validateProperties()).doesNotThrowAnyException();
    }
  }

  @Nested
  @DisplayName("getPreSignedUrl 메서드")
  class GetPreSignedUrl {

    @Test
    @DisplayName("성공: S3 generatePresignedUrl 호출 후 URL 반환")
    void shouldGeneratePreSignedUrl() throws Exception {
      // given
      String fileUrl = "https://bucket/k";
      given(amazonS3.getUrl("my-bucket", "")).willReturn(new URL("https://bucket/"));
      given(amazonS3.generatePresignedUrl(any(GeneratePresignedUrlRequest.class)))
          .willReturn(new URL("https://signed"));

      // when
      String pre = adapter.getPreSignedUrl(fileUrl, 120);

      // then
      assertThat(pre).isEqualTo("https://signed");
      then(amazonS3).should().generatePresignedUrl(any(GeneratePresignedUrlRequest.class));
    }
  }
}
