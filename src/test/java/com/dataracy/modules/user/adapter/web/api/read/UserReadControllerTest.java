
package com.dataracy.modules.user.adapter.web.api.read;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.user.adapter.web.mapper.read.UserReadWebMapper;
import com.dataracy.modules.user.adapter.web.response.read.GetUserInfoWebResponse;
import com.dataracy.modules.user.application.dto.response.read.GetUserInfoResponse;
import com.dataracy.modules.user.application.port.in.query.extractor.GetUserInfoUseCase;
import com.dataracy.modules.user.domain.enums.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserReadControllerTest {

    @Mock GetUserInfoUseCase getUserInfoUseCase;
    @Mock UserReadWebMapper userReadWebMapper;

    @InjectMocks UserReadController controller;

    @Test
    @DisplayName("getUserInfo: 정상 플로우 → 200 OK + 매핑 검증")
    void getUserInfo_success() {
        // given
        Long userId = 5L;
        GetUserInfoResponse dto = new GetUserInfoResponse(
                5L, RoleType.ROLE_USER, "u@test.com", "nick",
                "author", "job", List.of("topicA", "topicB"),
                "visit", "img.png", "intro"
        );
        GetUserInfoWebResponse webDto = new GetUserInfoWebResponse(
                dto.id(), dto.role(), dto.email(), dto.nickname(),
                dto.authorLevelLabel(), dto.occupationLabel(), dto.topicLabels(),
                dto.visitSourceLabel(), dto.profileImageUrl(), dto.introductionText()
        );
        given(getUserInfoUseCase.getUserInfo(userId)).willReturn(dto);
        given(userReadWebMapper.toWebDto(dto)).willReturn(webDto);

        // when
        ResponseEntity<SuccessResponse<GetUserInfoWebResponse>> res = controller.getUserInfo(userId);

        // then
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        then(getUserInfoUseCase).should().getUserInfo(userId);
        then(userReadWebMapper).should().toWebDto(dto);
    }
}
