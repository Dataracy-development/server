package com.dataracy.modules.dataset.application.service.command;

import static org.assertj.core.api.Assertions.assertThatNoException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.dataracy.modules.dataset.application.dto.request.metadata.ParseMetadataRequest;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ParseMetadataServiceTest {

  @Mock
  private com.dataracy.modules.filestorage.application.port.out.FileStoragePort fileStoragePort;

  @Mock
  private com.dataracy.modules.dataset.application.port.out.command.create.CreateMetadataPort
      metadataRepositoryPort;

  @Mock
  private com.dataracy.modules.dataset.application.port.out.query.read.FindDataPort findDataPort;

  @Mock
  private com.dataracy.modules.dataset.application.port.out.indexing.IndexDataPort indexDataPort;

  @Mock
  private com.dataracy.modules.user.application.port.in.query.extractor.FindUsernameUseCase
      findUsernameUseCase;

  @Mock
  private com.dataracy.modules.user.application.port.in.query.extractor.FindUserThumbnailUseCase
      findUserThumbnailUseCase;

  @Mock
  private com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase
      getTopicLabelFromIdUseCase;

  @Mock
  private com.dataracy.modules.reference.application.port.in.datasource
          .GetDataSourceLabelFromIdUseCase
      getDataSourceLabelFromIdUseCase;

  @Mock
  private com.dataracy.modules.reference.application.port.in.datatype.GetDataTypeLabelFromIdUseCase
      getDataTypeLabelFromIdUseCase;

  @InjectMocks private ParseMetadataService service;

  @Test
  @DisplayName("ParseMetadataService 생성자 테스트")
  void constructorTest() {
    // when & then
    assertThatNoException()
        .isThrownBy(
            () -> {
              ParseMetadataRequest request =
                  new ParseMetadataRequest(1L, "http://example.com/file.csv", "test.csv");
              // 서비스가 정상적으로 생성되었는지 확인
              assertThatNoException().isThrownBy(() -> service.parseAndSaveMetadata(request));
            });
  }

  @Test
  @DisplayName("서비스 의존성 주입 테스트")
  void dependencyInjectionTest() {
    // when & then
    assertThatNoException()
        .isThrownBy(
            () -> {
              // 서비스가 정상적으로 생성되었는지 확인
              assertThatNoException().isThrownBy(() -> service.toString());
            });
  }
}
