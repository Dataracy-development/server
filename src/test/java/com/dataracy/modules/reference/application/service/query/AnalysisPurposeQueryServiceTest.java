package com.dataracy.modules.reference.application.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.dataracy.modules.common.logging.ServiceLogger;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.reference.application.dto.response.allview.AllAnalysisPurposesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.AnalysisPurposeResponse;
import com.dataracy.modules.reference.application.mapper.AnalysisPurposeDtoMapper;
import com.dataracy.modules.reference.application.port.out.AnalysisPurposePort;
import com.dataracy.modules.reference.domain.exception.ReferenceException;
import com.dataracy.modules.reference.domain.model.AnalysisPurpose;
import com.dataracy.modules.reference.domain.status.ReferenceErrorStatus;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AnalysisPurposeQueryServiceTest {

  // Test constants
  private static final Integer CURRENT_YEAR = 2024;

  @InjectMocks private AnalysisPurposeQueryService service;

  @Mock private AnalysisPurposeDtoMapper analysisPurposeDtoMapper;

  @Mock private AnalysisPurposePort analysisPurposePort;

  private MockedStatic<LoggerFactory> loggerFactoryMock;
  private ServiceLogger loggerService;

  @BeforeEach
  void setUp() {
    loggerFactoryMock = mockStatic(LoggerFactory.class);
    loggerService = mock(ServiceLogger.class);
    loggerFactoryMock.when(LoggerFactory::service).thenReturn(loggerService);
    doReturn(Instant.now()).when(loggerService).logStart(anyString(), anyString());
    doNothing().when(loggerService).logSuccess(anyString(), anyString(), any(Instant.class));
    doNothing().when(loggerService).logWarning(anyString(), anyString());
  }

  @AfterEach
  void tearDown() {
    if (loggerFactoryMock != null) {
      loggerFactoryMock.close();
    }
  }

  @Nested
  @DisplayName("findAllAnalysisPurposes 메서드 테스트")
  class FindAllAnalysisPurposesTest {

    // Test constants
    private static final Integer CURRENT_YEAR = 2024;

    @Test
    @DisplayName("모든 분석 목적 조회 성공")
    void findAllAnalysisPurposesSuccess() {
      // given
      List<AnalysisPurpose> analysisPurposes =
          List.of(
              new AnalysisPurpose(1L, "value1", "분석 목적 1"),
              new AnalysisPurpose(2L, "value2", "분석 목적 2"));
      List<AnalysisPurposeResponse> responseList =
          List.of(
              new AnalysisPurposeResponse(1L, "value1", "분석 목적 1"),
              new AnalysisPurposeResponse(2L, "value2", "분석 목적 2"));
      AllAnalysisPurposesResponse expectedResponse = new AllAnalysisPurposesResponse(responseList);

      given(analysisPurposePort.findAllAnalysisPurposes()).willReturn(analysisPurposes);
      given(analysisPurposeDtoMapper.toResponseDto(analysisPurposes)).willReturn(expectedResponse);

      // when
      AllAnalysisPurposesResponse result = service.findAllAnalysisPurposes();

      // then
      assertThat(result).isEqualTo(expectedResponse);
      then(analysisPurposePort).should().findAllAnalysisPurposes();
      then(analysisPurposeDtoMapper).should().toResponseDto(analysisPurposes);
      then(loggerService)
          .should()
          .logStart(eq("FindAllAnalysisPurposesUseCase"), contains("모든 분석 목적 정보 조회 서비스 시작"));
      then(loggerService)
          .should()
          .logSuccess(
              eq("FindAllAnalysisPurposesUseCase"),
              contains("모든 분석 목적 정보 조회 서비스 종료"),
              any(Instant.class));
    }
  }

  @Nested
  @DisplayName("findAnalysisPurpose 메서드 테스트")
  class FindAnalysisPurposeTest {

    // Test constants
    private static final Integer CURRENT_YEAR = 2024;

    @Test
    @DisplayName("분석 목적 조회 성공")
    void findAnalysisPurposeSuccess() {
      // given
      Long analysisPurposeId = 1L;
      AnalysisPurpose analysisPurpose = new AnalysisPurpose(analysisPurposeId, "value", "분석 목적");
      AnalysisPurposeResponse expectedResponse =
          new AnalysisPurposeResponse(analysisPurposeId, "value", "분석 목적");

      given(analysisPurposePort.findAnalysisPurposeById(analysisPurposeId))
          .willReturn(Optional.of(analysisPurpose));
      given(analysisPurposeDtoMapper.toResponseDto(analysisPurpose)).willReturn(expectedResponse);

      // when
      AnalysisPurposeResponse result = service.findAnalysisPurpose(analysisPurposeId);

      // then
      assertThat(result).isEqualTo(expectedResponse);
      then(analysisPurposePort).should().findAnalysisPurposeById(analysisPurposeId);
      then(analysisPurposeDtoMapper).should().toResponseDto(analysisPurpose);
      then(loggerService)
          .should()
          .logStart(
              eq("FindAnalysisPurposeUseCase"),
              contains("주어진 ID로 분석 목적 조회 서비스 시작 analysisPurposeId=" + analysisPurposeId));
      then(loggerService)
          .should()
          .logSuccess(
              eq("FindAnalysisPurposeUseCase"),
              contains("주어진 ID로 분석 목적 조회 서비스 종료 analysisPurposeId=" + analysisPurposeId),
              any(Instant.class));
    }

    @Test
    @DisplayName("분석 목적이 존재하지 않을 때 예외 발생")
    void findAnalysisPurposeFailWhenNotFound() {
      // given
      Long analysisPurposeId = 999L;
      given(analysisPurposePort.findAnalysisPurposeById(analysisPurposeId))
          .willReturn(Optional.empty());

      // when & then
      ReferenceException exception =
          catchThrowableOfType(
              () -> service.findAnalysisPurpose(analysisPurposeId), ReferenceException.class);
      assertAll(
          () -> assertThat(exception).isNotNull(),
          () ->
              assertThat(exception.getErrorCode())
                  .isEqualTo(ReferenceErrorStatus.NOT_FOUND_ANALYSIS_PURPOSE));

      then(analysisPurposePort).should().findAnalysisPurposeById(analysisPurposeId);
      then(analysisPurposeDtoMapper).should(never()).toResponseDto(any(AnalysisPurpose.class));
      then(loggerService)
          .should()
          .logStart(
              eq("FindAnalysisPurposeUseCase"),
              contains("주어진 ID로 분석 목적 조회 서비스 시작 analysisPurposeId=" + analysisPurposeId));
      then(loggerService)
          .should()
          .logWarning(
              eq("FindAnalysisPurposeUseCase"),
              contains("해당 분석 목적이 존재하지 않습니다. analysisPurposeId=" + analysisPurposeId));
      then(loggerService).should(never()).logSuccess(anyString(), anyString(), any(Instant.class));
    }
  }

  @Nested
  @DisplayName("validateAnalysisPurpose 메서드 테스트")
  class ValidateAnalysisPurposeTest {

    // Test constants
    private static final Integer CURRENT_YEAR = 2024;

    @Test
    @DisplayName("분석 목적 검증 성공")
    void validateAnalysisPurposeSuccess() {
      // given
      Long analysisPurposeId = 1L;
      given(analysisPurposePort.existsAnalysisPurposeById(analysisPurposeId)).willReturn(true);

      // when
      service.validateAnalysisPurpose(analysisPurposeId);

      // then
      then(analysisPurposePort).should().existsAnalysisPurposeById(analysisPurposeId);
      then(loggerService)
          .should()
          .logStart(
              eq("ValidateAnalysisPurposeUseCase"),
              contains(
                  "주어진 ID에 해당하는 분석 목적이 존재하는지 확인 서비스 시작 analysisPurposeId=" + analysisPurposeId));
      then(loggerService)
          .should()
          .logSuccess(
              eq("ValidateAnalysisPurposeUseCase"),
              contains(
                  "주어진 ID에 해당하는 분석 목적이 존재하는지 확인 서비스 종료 analysisPurposeId=" + analysisPurposeId),
              any(Instant.class));
      then(loggerService).should(never()).logWarning(anyString(), anyString());
    }

    @Test
    @DisplayName("분석 목적이 존재하지 않을 때 예외 발생")
    void validateAnalysisPurposeFailWhenNotFound() {
      // given
      Long analysisPurposeId = 999L;
      given(analysisPurposePort.existsAnalysisPurposeById(analysisPurposeId)).willReturn(false);

      // when & then
      ReferenceException exception =
          catchThrowableOfType(
              () -> service.validateAnalysisPurpose(analysisPurposeId), ReferenceException.class);
      assertAll(
          () -> assertThat(exception).isNotNull(),
          () ->
              assertThat(exception.getErrorCode())
                  .isEqualTo(ReferenceErrorStatus.NOT_FOUND_ANALYSIS_PURPOSE));

      then(analysisPurposePort).should().existsAnalysisPurposeById(analysisPurposeId);
      then(loggerService)
          .should()
          .logStart(
              eq("ValidateAnalysisPurposeUseCase"),
              contains(
                  "주어진 ID에 해당하는 분석 목적이 존재하는지 확인 서비스 시작 analysisPurposeId=" + analysisPurposeId));
      then(loggerService)
          .should()
          .logWarning(
              eq("FindAnalysisPurposeUseCase"),
              contains("해당 분석 목적이 존재하지 않습니다. analysisPurposeId=" + analysisPurposeId));
      then(loggerService).should(never()).logSuccess(anyString(), anyString(), any(Instant.class));
    }
  }

  @Nested
  @DisplayName("getLabelById 메서드 테스트")
  class GetLabelByIdTest {

    // Test constants
    private static final Integer CURRENT_YEAR = 2024;

    @Test
    @DisplayName("라벨 조회 성공")
    void getLabelByIdSuccess() {
      // given
      Long analysisPurposeId = 1L;
      String expectedLabel = "분석 목적";
      given(analysisPurposePort.getLabelById(analysisPurposeId))
          .willReturn(Optional.of(expectedLabel));

      // when
      String result = service.getLabelById(analysisPurposeId);

      // then
      assertThat(result).isEqualTo(expectedLabel);
      then(analysisPurposePort).should().getLabelById(analysisPurposeId);
      then(loggerService)
          .should()
          .logStart(
              eq("GetAnalysisPurposeLabelFromIdUseCase"),
              contains("주어진 분석 목적 ID에 해당하는 라벨을 조회 서비스 시작 analysisPurposeId=" + analysisPurposeId));
      then(loggerService)
          .should()
          .logSuccess(
              eq("GetAnalysisPurposeLabelFromIdUseCase"),
              contains("주어진 분석 목적 ID에 해당하는 라벨을 조회 서비스 종료 analysisPurposeId=" + analysisPurposeId),
              any(Instant.class));
    }

    @Test
    @DisplayName("라벨이 존재하지 않을 때 예외 발생")
    void getLabelByIdFailWhenNotFound() {
      // given
      Long analysisPurposeId = 999L;
      given(analysisPurposePort.getLabelById(analysisPurposeId)).willReturn(Optional.empty());

      // when & then
      ReferenceException exception =
          catchThrowableOfType(
              () -> service.getLabelById(analysisPurposeId), ReferenceException.class);
      assertAll(
          () -> assertThat(exception).isNotNull(),
          () ->
              assertThat(exception.getErrorCode())
                  .isEqualTo(ReferenceErrorStatus.NOT_FOUND_ANALYSIS_PURPOSE));

      then(analysisPurposePort).should().getLabelById(analysisPurposeId);
      then(loggerService)
          .should()
          .logStart(
              eq("GetAnalysisPurposeLabelFromIdUseCase"),
              contains("주어진 분석 목적 ID에 해당하는 라벨을 조회 서비스 시작 analysisPurposeId=" + analysisPurposeId));
      then(loggerService)
          .should()
          .logWarning(
              eq("FindAnalysisPurposeUseCase"),
              contains("해당 분석 목적이 존재하지 않습니다. analysisPurposeId=" + analysisPurposeId));
      then(loggerService).should(never()).logSuccess(anyString(), anyString(), any(Instant.class));
    }
  }

  @Nested
  @DisplayName("getLabelsByIds 메서드 테스트")
  class GetLabelsByIdsTest {

    // Test constants
    private static final Integer CURRENT_YEAR = 2024;

    @Test
    @DisplayName("라벨 목록 조회 성공")
    void getLabelsByIdsSuccess() {
      // given
      List<Long> analysisPurposeIds = List.of(1L, 2L);
      Map<Long, String> expectedLabels =
          Map.of(
              1L, "분석 목적 1",
              2L, "분석 목적 2");
      given(analysisPurposePort.getLabelsByIds(analysisPurposeIds)).willReturn(expectedLabels);

      // when
      Map<Long, String> result = service.getLabelsByIds(analysisPurposeIds);

      // then
      assertThat(result).isEqualTo(expectedLabels);
      then(analysisPurposePort).should().getLabelsByIds(analysisPurposeIds);
      then(loggerService)
          .should()
          .logStart(
              eq("GetAnalysisPurposeLabelFromIdUseCase"),
              contains("분석 목적 ID 목록에 대해 각 ID에 해당하는 라벨을 반환 서비스 시작"));
      then(loggerService)
          .should()
          .logSuccess(
              eq("GetAnalysisPurposeLabelFromIdUseCase"),
              contains("분석 목적 ID 목록에 대해 각 ID에 해당하는 라벨을 반환 서비스 종료"),
              any(Instant.class));
    }

    @Test
    @DisplayName("null ID 목록일 때 빈 Map 반환")
    void getLabelsByIdsWithNullList() {
      // when
      Map<Long, String> result = service.getLabelsByIds(null);

      // then
      assertThat(result).isEmpty();
      then(analysisPurposePort).should(never()).getLabelsByIds(any());
      // null/빈 리스트일 때는 logSuccess가 호출되지 않을 수 있음
    }

    @Test
    @DisplayName("빈 ID 목록일 때 빈 Map 반환")
    void getLabelsByIdsWithEmptyList() {
      // when
      Map<Long, String> result = service.getLabelsByIds(List.of());

      // then
      assertThat(result).isEmpty();
      then(analysisPurposePort).should(never()).getLabelsByIds(any());
      // null/빈 리스트일 때는 logSuccess가 호출되지 않을 수 있음
    }
  }
}
