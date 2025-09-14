package com.dataracy.modules.user.adapter.web.api;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.common.support.resolver.CurrentUserIdArgumentResolver;
import com.dataracy.modules.user.adapter.web.api.read.UserReadController;
import com.dataracy.modules.user.adapter.web.mapper.read.UserReadWebMapper;
import com.dataracy.modules.user.adapter.web.response.read.GetUserInfoWebResponse;
import com.dataracy.modules.user.application.dto.response.read.GetUserInfoResponse;
import com.dataracy.modules.user.application.port.in.query.extractor.GetUserInfoUseCase;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.status.UserSuccessStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = UserReadController.class)
class UserReadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetUserInfoUseCase getUserInfoUseCase;

    @MockBean
    private UserReadWebMapper userReadWebMapper;

    // 공통 모킹
    @MockBean
    private BehaviorLogSendProducerPort behaviorLogSendProducerPort;
    @MockBean
    private JwtValidateUseCase jwtValidateUseCase;
    @MockBean
    private CurrentUserIdArgumentResolver currentUserIdArgumentResolver;

    private static final Long FIXED_USER_ID = 5L;

    @BeforeEach
    void setupResolver() throws Exception {
        given(currentUserIdArgumentResolver.supportsParameter(any()))
                .willReturn(true);
        given(currentUserIdArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(FIXED_USER_ID);
        given(jwtValidateUseCase.getUserIdFromToken(any()))
                .willReturn(FIXED_USER_ID);
    }

    @Test
    @DisplayName("getUserInfo API: 정상 플로우 → 200 OK + JSON 매핑 검증")
    void getUserInfoSuccess() throws Exception {
        // given
        GetUserInfoResponse dto = new GetUserInfoResponse(
                FIXED_USER_ID, RoleType.ROLE_USER, "u@test.com", "nick",
                1L, "author", 2L, "job", List.of(10L, 20L), List.of("topicA", "topicB"),
                3L, "visit", "img.png", "intro"
        );
        GetUserInfoWebResponse webDto = new GetUserInfoWebResponse(
                dto.id(), dto.role(), dto.email(), dto.nickname(),
                dto.authorLevelId(), dto.authorLevelLabel(), dto.occupationId(), dto.occupationLabel(),
                dto.topicIds(), dto.topicLabels(), dto.visitSourceId(), dto.visitSourceLabel(),
                dto.profileImageUrl(), dto.introductionText()
        );

        given(getUserInfoUseCase.getUserInfo(FIXED_USER_ID)).willReturn(dto);
        given(userReadWebMapper.toWebDto(dto)).willReturn(webDto);

        // when & then
        mockMvc.perform(get("/api/v1/user")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(UserSuccessStatus.OK_GET_USER_INFO.getCode()))
                .andExpect(jsonPath("$.data.id").value(FIXED_USER_ID))
                .andExpect(jsonPath("$.data.email").value("u@test.com"))
                .andExpect(jsonPath("$.data.nickname").value("nick"))
                .andExpect(jsonPath("$.data.profileImageUrl").value("img.png"));

        then(getUserInfoUseCase).should().getUserInfo(FIXED_USER_ID);
        then(userReadWebMapper).should().toWebDto(dto);
    }
}
