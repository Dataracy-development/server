package com.dataracy.modules.dataset.adapter.web.api.command;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver;
import com.dataracy.modules.dataset.adapter.web.mapper.command.DataCommandWebMapper;
import com.dataracy.modules.dataset.adapter.web.mapper.download.DataDownloadWebMapper;
import com.dataracy.modules.dataset.adapter.web.request.command.ModifyDataWebRequest;
import com.dataracy.modules.dataset.adapter.web.request.command.UploadDataWebRequest;
import com.dataracy.modules.dataset.adapter.web.response.command.UploadDataWebResponse;
import com.dataracy.modules.dataset.adapter.web.response.download.GetDataPreSignedUrlWebResponse;
import com.dataracy.modules.dataset.application.dto.request.command.ModifyDataRequest;
import com.dataracy.modules.dataset.application.dto.request.command.UploadDataRequest;
import com.dataracy.modules.dataset.application.dto.response.command.UploadDataResponse;
import com.dataracy.modules.dataset.application.dto.response.download.GetDataPreSignedUrlResponse;
import com.dataracy.modules.dataset.application.port.in.command.content.*;
import com.dataracy.modules.dataset.domain.status.DataSuccessStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(
    controllers = DataCommandController.class,
    includeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {
              com.dataracy.modules.common.util.CookieUtil.class,
              com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver.class
            }))
class DataCommandControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private DataCommandWebMapper dataCommandWebMapper;

  @MockBean private DataDownloadWebMapper dataDownloadWebMapper;

  @MockBean private UploadDataUseCase uploadDataUseCase;

  @MockBean private ModifyDataUseCase modifyDataUseCase;

  @MockBean private DeleteDataUseCase deleteDataUseCase;

  @MockBean private RestoreDataUseCase restoreDataUseCase;

  @MockBean private DownloadDataFileUseCase downloadDataFileUseCase;

  @MockBean private CurrentUserIdArgumentResolver currentUserIdArgumentResolver;

  @MockBean private JwtValidateUseCase jwtValidateUseCase;

  @MockBean private BehaviorLogSendProducerPort behaviorLogSendProducerPort;

  @MockBean private com.dataracy.modules.security.config.SecurityPathConfig securityPathConfig;

  @BeforeEach
  void setupResolver() {
    // 모든 @CurrentUserId → userId=1L
    given(currentUserIdArgumentResolver.supportsParameter(any())).willReturn(true);
    given(currentUserIdArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(1L);

    // Jwt도 항상 userId=1L 반환
    given(jwtValidateUseCase.getUserIdFromToken(any())).willReturn(1L);
  }

  @Test
  @DisplayName("데이터 업로드 성공 시 201 반환")
  void uploadDataShouldReturnCreated() throws Exception {
    // given
    UploadDataWebRequest webReq =
        new UploadDataWebRequest("title", 1L, 2L, 3L, null, null, "desc", "guide");
    UploadDataRequest appReq =
        new UploadDataRequest("title", 1L, 2L, 3L, null, null, "desc", "guide");
    UploadDataResponse appRes = new UploadDataResponse(100L);
    UploadDataWebResponse webRes = new UploadDataWebResponse(100L);

    given(dataCommandWebMapper.toApplicationDto(any(UploadDataWebRequest.class)))
        .willReturn(appReq);
    given(uploadDataUseCase.uploadData(anyLong(), any(), any(), any(UploadDataRequest.class)))
        .willReturn(appRes);
    given(dataCommandWebMapper.toWebDto(appRes)).willReturn(webRes);

    // MockMultipartFile 준비
    MockMultipartFile dataFile =
        new MockMultipartFile(
            "dataFile", "data.csv", "text/csv", "dummy".getBytes(StandardCharsets.UTF_8));
    MockMultipartFile thumbnailFile =
        new MockMultipartFile(
            "thumbnailFile",
            "thumb.png",
            "image/png",
            "dummy-thumb".getBytes(StandardCharsets.UTF_8));
    MockMultipartFile webRequestPart =
        new MockMultipartFile(
            "webRequest", "", "application/json", objectMapper.writeValueAsBytes(webReq));

    // when & then
    mockMvc
        .perform(
            multipart("/api/v1/datasets")
                .file(dataFile)
                .file(thumbnailFile)
                .file(webRequestPart) // JSON DTO를 multipart part로 추가
                .param("userId", "1")
                .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.code").value(DataSuccessStatus.CREATED_DATASET.getCode()))
        .andExpect(jsonPath("$.message").value(DataSuccessStatus.CREATED_DATASET.getMessage()))
        .andExpect(jsonPath("$.data.id").value(100L));
  }

  @Test
  @DisplayName("데이터 수정 성공 시 200 반환")
  void modifyDataShouldReturnOk() throws Exception {
    // given
    ModifyDataWebRequest webReq =
        new ModifyDataWebRequest("new title", 1L, 2L, 3L, null, null, "desc", "guide");
    ModifyDataRequest appReq =
        new ModifyDataRequest("new title", 1L, 2L, 3L, null, null, "desc", "guide");

    given(dataCommandWebMapper.toApplicationDto(any(ModifyDataWebRequest.class)))
        .willReturn(appReq);
    willDoNothing()
        .given(modifyDataUseCase)
        .modifyData(anyLong(), any(), any(), any(ModifyDataRequest.class));

    MockMultipartFile dataFile =
        new MockMultipartFile(
            "dataFile", "data.csv", "text/csv", "dummy".getBytes(StandardCharsets.UTF_8));
    MockMultipartFile webRequestPart =
        new MockMultipartFile(
            "webRequest", "", "application/json", objectMapper.writeValueAsBytes(webReq));

    // when & then
    mockMvc
        .perform(
            multipart("/api/v1/datasets/{dataId}", 100L)
                .file(dataFile)
                .file(webRequestPart) // webRequest DTO JSON 파트 추가
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(
                    request -> {
                      request.setMethod("PUT"); // multipart는 기본 POST → PUT으로 덮어쓰기
                      return request;
                    }))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(DataSuccessStatus.MODIFY_DATASET.getCode()))
        .andExpect(jsonPath("$.message").value(DataSuccessStatus.MODIFY_DATASET.getMessage()));
  }

  @Test
  @DisplayName("데이터 삭제 성공 시 200 반환")
  void deleteDataShouldReturnOk() throws Exception {
    // given
    willDoNothing().given(deleteDataUseCase).deleteData(100L);

    // when & then
    mockMvc
        .perform(delete("/api/v1/datasets/{dataId}", 100L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(DataSuccessStatus.DELETE_DATASET.getCode()))
        .andExpect(jsonPath("$.message").value(DataSuccessStatus.DELETE_DATASET.getMessage()));
  }

  @Test
  @DisplayName("데이터 복원 성공 시 200 반환")
  void restoreDataShouldReturnOk() throws Exception {
    // given
    willDoNothing().given(restoreDataUseCase).restoreData(100L);

    // when & then
    mockMvc
        .perform(patch("/api/v1/datasets/{dataId}/restore", 100L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(DataSuccessStatus.RESTORE_DATASET.getCode()))
        .andExpect(jsonPath("$.message").value(DataSuccessStatus.RESTORE_DATASET.getMessage()));
  }

  @Test
  @DisplayName("프리사인드 다운로드 URL 조회 성공 시 200 반환")
  void getPreSignedDataUrlShouldReturnOk() throws Exception {
    // given
    GetDataPreSignedUrlResponse appRes = new GetDataPreSignedUrlResponse("http://signed-url");

    given(downloadDataFileUseCase.downloadDataFile(anyLong(), anyInt())).willReturn(appRes);

    given(dataDownloadWebMapper.toWebDto(appRes))
        .willReturn(new GetDataPreSignedUrlWebResponse("http://signed-url"));

    // when & then
    mockMvc
        .perform(get("/api/v1/datasets/{dataId}/download", 100L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(DataSuccessStatus.DOWNLOAD_DATASET.getCode()))
        .andExpect(jsonPath("$.message").value(DataSuccessStatus.DOWNLOAD_DATASET.getMessage()))
        .andExpect(jsonPath("$.data.preSignedUrl").value("http://signed-url"));
  }
}
