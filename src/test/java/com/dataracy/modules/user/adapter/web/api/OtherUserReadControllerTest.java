package com.dataracy.modules.user.adapter.web.api.read;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.user.adapter.web.mapper.read.OtherUserReadWebMapper;
import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserDataWebResponse;
import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserInfoWebResponse;
import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserProjectWebResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserDataResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserInfoResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserProjectResponse;
import com.dataracy.modules.user.application.port.in.query.extractor.GetOtherUserInfoUseCase;
import com.dataracy.modules.user.domain.status.UserSuccessStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OtherUserReadController.class)
@AutoConfigureMockMvc(addFilters = false)
class OtherUserReadControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    GetOtherUserInfoUseCase getOtherUserInfoUseCase;

    @MockBean
    OtherUserReadWebMapper mapper;

    // 공통 모킹
    @MockBean
    private BehaviorLogSendProducerPort behaviorLogSendProducerPort;
    @MockBean
    private JwtValidateUseCase jwtValidateUseCase;

    @Test
    @DisplayName("타인 유저 기본 정보 조회 성공 시 200 응답")
    void getOtherUserInfo_success() throws Exception {
        // given
        Long userId = 1L;
        Page<GetOtherUserProjectResponse> projects = Page.empty();
        Page<GetOtherUserDataResponse> datasets = Page.empty();

        GetOtherUserInfoResponse dto = new GetOtherUserInfoResponse(
                userId, "다른유저", "중급", "데이터분석가",
                "profile.png", "소개합니다",
                projects, datasets
        );

        GetOtherUserInfoWebResponse webRes = new GetOtherUserInfoWebResponse(
                dto.id(), dto.nickname(), dto.authorLevelLabel(),
                dto.occupationLabel(), dto.profileImageUrl(),
                dto.introductionText(), Page.empty(), Page.empty()
        );

        given(getOtherUserInfoUseCase.getOtherUserInfo(userId)).willReturn(dto);
        given(mapper.toWebDto(dto)).willReturn(webRes);

        // when & then
        mockMvc.perform(get("/api/v1/users/{userId}", userId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(UserSuccessStatus.OK_GET_OTHER_USER_INFO.getCode()))
                .andExpect(jsonPath("$.message").value(UserSuccessStatus.OK_GET_OTHER_USER_INFO.getMessage()))
                .andExpect(jsonPath("$.data.nickname").value("다른유저"))
                .andExpect(jsonPath("$.data.authorLevelLabel").value("중급"));
    }

    @Test
    @DisplayName("타인 유저 추가 프로젝트 조회 성공 시 200 응답")
    void getOtherProjects_success() throws Exception {
        // given
        Long userId = 1L;

        GetOtherUserProjectResponse dto = new GetOtherUserProjectResponse(
                10L, "프로젝트제목", "내용", "thumb.png",
                "주제", "레벨", 3L, 5L, 100L, LocalDateTime.now()
        );
        Page<GetOtherUserProjectResponse> dtoPage = new PageImpl<>(List.of(dto), PageRequest.of(0, 5), 1);

        GetOtherUserProjectWebResponse webRes = new GetOtherUserProjectWebResponse(
                dto.id(), dto.title(), dto.content(), dto.projectThumbnailUrl(),
                dto.topicLabel(), dto.authorLevelLabel(), dto.commentCount(),
                dto.likeCount(), dto.viewCount(), dto.createdAt()
        );
        Page<GetOtherUserProjectWebResponse> webPage = new PageImpl<>(List.of(webRes), PageRequest.of(0, 5), 1);

        given(getOtherUserInfoUseCase.getOtherExtraProjects(eq(userId), any())).willReturn(dtoPage);
        given(mapper.toWebDto(dto)).willReturn(webRes);

        // when & then
        mockMvc.perform(get("/api/v1/users/{userId}/projects", userId)
                        .param("page", "0")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(UserSuccessStatus.OK_GET_OTHER_EXTRA_PROJECTS.getCode()))
                .andExpect(jsonPath("$.message").value(UserSuccessStatus.OK_GET_OTHER_EXTRA_PROJECTS.getMessage()))
                .andExpect(jsonPath("$.data.content[0].title").value("프로젝트제목"))
                .andExpect(jsonPath("$.data.content[0].likeCount").value(5));
    }

    @Test
    @DisplayName("타인 유저 추가 데이터셋 조회 성공 시 200 응답")
    void getOtherDataSets_success() throws Exception {
        // given
        Long userId = 1L;

        GetOtherUserDataResponse dto = new GetOtherUserDataResponse(
                20L, "데이터셋제목", "주제", "타입",
                LocalDate.of(2023,1,1), LocalDate.of(2023,12,31),
                "data-thumb.png", 10, 2048L, 200, 20,
                LocalDateTime.now(), 7L
        );
        Page<GetOtherUserDataResponse> dtoPage = new PageImpl<>(List.of(dto), PageRequest.of(0, 5), 1);

        GetOtherUserDataWebResponse webRes = new GetOtherUserDataWebResponse(
                dto.id(), dto.title(), dto.topicLabel(), dto.dataTypeLabel(),
                dto.startDate(), dto.endDate(), dto.dataThumbnailUrl(),
                dto.downloadCount(), dto.sizeBytes(), dto.rowCount(), dto.columnCount(),
                dto.createdAt(), dto.countConnectedProjects()
        );
        Page<GetOtherUserDataWebResponse> webPage = new PageImpl<>(List.of(webRes), PageRequest.of(0, 5), 1);

        given(getOtherUserInfoUseCase.getOtherExtraDataSets(eq(userId), any())).willReturn(dtoPage);
        given(mapper.toWebDto(dto)).willReturn(webRes);

        // when & then
        mockMvc.perform(get("/api/v1/users/{userId}/datasets", userId)
                        .param("page", "0")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(UserSuccessStatus.OK_GET_OTHER_EXTRA_DATASETS.getCode()))
                .andExpect(jsonPath("$.message").value(UserSuccessStatus.OK_GET_OTHER_EXTRA_DATASETS.getMessage()))
                .andExpect(jsonPath("$.data.content[0].title").value("데이터셋제목"))
                .andExpect(jsonPath("$.data.content[0].downloadCount").value(10));
    }
}
