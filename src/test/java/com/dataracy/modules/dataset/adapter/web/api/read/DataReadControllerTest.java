package com.dataracy.modules.dataset.adapter.web.api.read;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver;
import com.dataracy.modules.common.util.CookieUtil;
import com.dataracy.modules.dataset.adapter.web.api.read.DataReadController;
import com.dataracy.modules.dataset.adapter.web.mapper.read.DataReadWebMapper;
import com.dataracy.modules.dataset.adapter.web.response.read.*;
import com.dataracy.modules.dataset.application.dto.response.read.*;
import com.dataracy.modules.dataset.application.port.in.query.read.*;
import com.dataracy.modules.dataset.domain.status.DataSuccessStatus;
import com.dataracy.modules.security.config.SecurityPathConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class DataReadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataReadWebMapper mapper;

    @MockBean
    private GetPopularDataSetsUseCase getPopularDataSetsUseCase;

    @MockBean
    private GetDataDetailUseCase getDataDetailUseCase;

    @MockBean
    private GetRecentMinimalDataSetsUseCase getRecentMinimalDataSetsUseCase;

    @MockBean
    private GetDataGroupCountUseCase getDataGroupCountUseCase;

    @MockBean
    private FindConnectedDataSetsUseCase findConnectedDataSetsUseCase;

    @MockBean
    private FindUserDataSetsUseCase findUserDataSetsUseCase;

    @MockBean
    private BehaviorLogSendProducerPort behaviorLogSendProducerPort;

    @MockBean
    private JwtValidateUseCase jwtValidateUseCase;

    @MockBean
    private SecurityPathConfig securityPathConfig;

    @MockBean
    private CurrentUserIdArgumentResolver currentUserIdArgumentResolver;

    @BeforeEach
    void setupResolver() throws Exception {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new DataReadController(
                        mapper,
                        getPopularDataSetsUseCase,
                        getDataDetailUseCase,
                        getRecentMinimalDataSetsUseCase,
                        getDataGroupCountUseCase,
                        findConnectedDataSetsUseCase,
                        findUserDataSetsUseCase
                ))
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver(),
                        currentUserIdArgumentResolver
                )
                .build();

        // 모든 @CurrentUserId → userId=1L
        given(currentUserIdArgumentResolver.supportsParameter(any()))
                .willReturn(true);
        given(currentUserIdArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);

        // Jwt도 항상 userId=1L 반환
        given(jwtValidateUseCase.getUserIdFromToken(any()))
                .willReturn(1L);
    }

    @Test
    @DisplayName("인기 데이터셋 조회 성공 시 200 반환")
    void getPopularDataSetsShouldReturnOk() throws Exception {
        PopularDataResponse resDto = new PopularDataResponse(
                1L, "데이터1", 1L, "userA", "https://~~", "경제", "통계청", "CSV",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31),
                "설명입니다", "thumb.png",
                100, 2048L, 500, 20,
                LocalDateTime.of(2023, 2, 1, 10, 0),
                5L
        );
        PopularDataWebResponse webRes = new PopularDataWebResponse(
                1L, "데이터1", 1L, "userA", "https://~~", "경제", "통계청", "CSV",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31),
                "설명입니다", "thumb.png",
                100, 2048L, 500, 20,
                LocalDateTime.of(2023, 2, 1, 10, 0),
                5L
        );

        given(getPopularDataSetsUseCase.getPopularDataSets(3)).willReturn(List.of(resDto));
        given(mapper.toWebDto(resDto)).willReturn(webRes);

        mockMvc.perform(get("/api/v1/datasets/popular")
                        .param("size", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(DataSuccessStatus.FIND_POPULAR_DATASETS.getCode()))
                .andExpect(jsonPath("$.message").value(DataSuccessStatus.FIND_POPULAR_DATASETS.getMessage()))
                .andExpect(jsonPath("$.data[0].title").value("데이터1"))
                .andExpect(jsonPath("$.data[0].creatorName").value("userA"));
    }

    @Test
    @DisplayName("데이터 상세 조회 성공 시 200 반환")
    void getDataDetailShouldReturnOk() throws Exception {
        DataDetailResponse resDto = new DataDetailResponse(
                2L, "데이터2", 1L, "userA",
                "profile.png", "자기소개",
                "작성자", "연구원", "의료", "보건복지부", "XLSX",
                LocalDate.of(2022, 5, 1),
                LocalDate.of(2022, 12, 31),
                "상세 설명", "가이드 문서",
                "thumb2.png", 50, 1024L,
                300, 15, "{\"preview\":true}",
                LocalDateTime.of(2022, 6, 1, 14, 30)
        );
        DataDetailWebResponse webRes = new DataDetailWebResponse(
                2L, "데이터2", 1L, "userA",
                "profile.png", "자기소개",
                "작성자", "연구원", "의료", "보건복지부", "XLSX",
                LocalDate.of(2022, 5, 1),
                LocalDate.of(2022, 12, 31),
                "상세 설명", "가이드 문서",
                "thumb2.png", 50, 1024L,
                300, 15, "{\"preview\":true}",
                LocalDateTime.of(2022, 6, 1, 14, 30)
        );

        given(getDataDetailUseCase.getDataDetail(2L)).willReturn(resDto);
        given(mapper.toWebDto(resDto)).willReturn(webRes);

        mockMvc.perform(get("/api/v1/datasets/{dataId}", 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(DataSuccessStatus.GET_DATA_DETAIL.getCode()))
                .andExpect(jsonPath("$.message").value(DataSuccessStatus.GET_DATA_DETAIL.getMessage()))
                .andExpect(jsonPath("$.data.title").value("데이터2"))
                .andExpect(jsonPath("$.data.userProfileImageUrl").value("profile.png"))
                .andExpect(jsonPath("$.data.analysisGuide").value("가이드 문서"));
    }

    @Test
    @DisplayName("최근 데이터셋 조회 성공 시 200 반환")
    void getRecentDataSetsShouldReturnOk() throws Exception {
        RecentMinimalDataResponse resDto = new RecentMinimalDataResponse(10L, "데이터10", 1L, "userA", "https://~~", "thumb.png", LocalDateTime.now());
        RecentMinimalDataWebResponse webRes = new RecentMinimalDataWebResponse(10L, "데이터10", 1L, "userA", "https://~~", "thumb.png", LocalDateTime.now());

        given(getRecentMinimalDataSetsUseCase.getRecentDataSets(2)).willReturn(List.of(resDto));
        given(mapper.toWebDto(resDto)).willReturn(webRes);

        mockMvc.perform(get("/api/v1/datasets/recent")
                        .param("size", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(DataSuccessStatus.GET_RECENT_DATASETS.getCode()))
                .andExpect(jsonPath("$.message").value(DataSuccessStatus.GET_RECENT_DATASETS.getMessage()))
                .andExpect(jsonPath("$.data[0].title").value("데이터10"));
    }

    @Test
    @DisplayName("주제 라벨별 데이터셋 개수 조회 성공 시 200 반환")
    void getDataCountByTopicLabelShouldReturnOk() throws Exception {
        DataGroupCountResponse resDto = new DataGroupCountResponse(3L, "경제", 5L);
        DataGroupCountWebResponse webRes = new DataGroupCountWebResponse(3L, "경제", 5L);

        given(getDataGroupCountUseCase.getDataGroupCountByTopicLabel()).willReturn(List.of(resDto));
        given(mapper.toWebDto(resDto)).willReturn(webRes);

        mockMvc.perform(get("/api/v1/datasets/group-by/topic")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(DataSuccessStatus.COUNT_DATASETS_GROUP_BY_TOPIC.getCode()))
                .andExpect(jsonPath("$.message").value(DataSuccessStatus.COUNT_DATASETS_GROUP_BY_TOPIC.getMessage()))
                .andExpect(jsonPath("$.data[0].topicLabel").value("경제"))
                .andExpect(jsonPath("$.data[0].count").value(5));
    }

    @Test
    @DisplayName("프로젝트에 연결된 데이터셋 조회 성공 시 200 반환")
    void findConnectedDataSetsShouldReturnOk() throws Exception {
        // given
        ConnectedDataResponse resDto = new ConnectedDataResponse(
                7L, "연결된데이터", 1L, "userA", "https://~~", "경제", "CSV",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31),
                "thumb7.png", 123, 2048L,
                300, 10,
                LocalDateTime.of(2023, 3, 1, 10, 0),
                4L
        );
        ConnectedDataWebResponse webRes = new ConnectedDataWebResponse(
                7L, "연결된데이터", 1L, "userA", "https://~~", "경제", "CSV",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31),
                "thumb7.png", 123, 2048L,
                300, 10,
                LocalDateTime.of(2023, 3, 1, 10, 0),
                4L
        );

        // PageImpl에 pageable, total 명시
        Page<ConnectedDataResponse> pageRes =
                new PageImpl<>(new ArrayList<>(List.of(resDto)), PageRequest.of(0, 1), 1);

        given(findConnectedDataSetsUseCase.findConnectedDataSetsAssociatedWithProject(any(), any()))
                .willReturn(pageRes);
        given(mapper.toWebDto(resDto)).willReturn(webRes);

        // when & then
        mockMvc.perform(get("/api/v1/datasets/connected-to-project")
                        .param("projectId", "99")
                        .param("page", "0")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(DataSuccessStatus.GET_CONNECTED_DATASETS_ASSOCIATED_PROJECT.getCode()))
                .andExpect(jsonPath("$.message").value(DataSuccessStatus.GET_CONNECTED_DATASETS_ASSOCIATED_PROJECT.getMessage()))
                .andExpect(jsonPath("$.data.content[0].title").value("연결된데이터"))
                .andExpect(jsonPath("$.data.content[0].topicLabel").value("경제"));
    }

    @Test
    @DisplayName("로그인한 회원이 업로드한 데이터셋 목록 조회 성공 시 200 반환")
    void findUserDataSetsShouldReturnOk() throws Exception {
        UserDataResponse resDto = new UserDataResponse(
                7L, "연결된데이터", "경제", "CSV",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31),
                "thumb7.png", 123, 2048L,
                300, 10,
                LocalDateTime.of(2023, 3, 1, 10, 0),
                4L
        );
        UserDataWebResponse webRes = new UserDataWebResponse(
                7L, "연결된데이터", "경제", "CSV",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31),
                "thumb7.png", 123, 2048L,
                300, 10,
                LocalDateTime.of(2023, 3, 1, 10, 0),
                4L
        );

        Page<UserDataResponse> pageRes = new PageImpl<>(new ArrayList<>(List.of(resDto)), PageRequest.of(0, 1), 1);
        given(findUserDataSetsUseCase.findUserDataSets(any(), any()))
                .willReturn(pageRes);
        given(mapper.toWebDto(resDto)).willReturn(webRes);

        mockMvc.perform(get("/api/v1/datasets/me")
                        .param("page", "0")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(DataSuccessStatus.GET_USER_DATASETS.getCode()))
                .andExpect(jsonPath("$.message").value(DataSuccessStatus.GET_USER_DATASETS.getMessage()))
                .andExpect(jsonPath("$.data.content[0].title").value("연결된데이터"))
                .andExpect(jsonPath("$.data.content[0].topicLabel").value("경제"));
    }
}
