package com.dataracy.modules.user.application.service.query.profile;

import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.GetOccupationLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.visitsource.GetVisitSourceLabelFromIdUseCase;
import com.dataracy.modules.user.application.dto.response.read.GetUserInfoResponse;
import com.dataracy.modules.user.application.port.out.query.UserMultiQueryPort;
import com.dataracy.modules.user.application.port.out.query.UserQueryPort;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.model.vo.UserInfo;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock UserQueryPort userQueryPort;
    @Mock UserMultiQueryPort userMultiQueryPort;
    @Mock GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;
    @Mock GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;
    @Mock GetOccupationLabelFromIdUseCase getOccupationLabelFromIdUseCase;
    @Mock GetVisitSourceLabelFromIdUseCase getVisitSourceLabelFromIdUseCase;

    @InjectMocks UserProfileService service;

    private User localUser() {
        return User.builder()
                .id(10L)
                .provider(ProviderType.LOCAL)
                .providerId("local-10")
                .role(RoleType.ROLE_USER)
                .email("u@test.com")
                .password("encoded")
                .nickname("nick")
                .authorLevelId(1L)
                .occupationId(2L)
                .topicIds(List.of(100L, 200L))
                .visitSourceId(3L)
                .profileImageUrl("img.png")
                .introductionText("intro")
                .isDeleted(false)
                .build();
    }

    // ---------------- findUsernameById ----------------
    @Test
    @DisplayName("findUsernameById: 유저 존재 → 닉네임 반환")
    void findUsernameById_success() {
        given(userQueryPort.findUserById(10L)).willReturn(Optional.of(localUser()));

        String nickname = service.findUsernameById(10L);

        assertThat(nickname).isEqualTo("nick");
    }

    @Test
    @DisplayName("findUsernameById: 유저 없음 → NOT_FOUND_USER 예외")
    void findUsernameById_notFound() {
        given(userQueryPort.findUserById(10L)).willReturn(Optional.empty());

        UserException ex = catchThrowableOfType(() -> service.findUsernameById(10L), UserException.class);

        assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.NOT_FOUND_USER);
    }

    // ---------------- findUsernamesByIds ----------------
    @Test
    @DisplayName("findUsernamesByIds: null/빈 리스트 → 빈 Map 반환")
    void findUsernamesByIds_empty() {
        assertThat(service.findUsernamesByIds(null)).isEmpty();
        assertThat(service.findUsernamesByIds(List.of())).isEmpty();
    }

    @Test
    @DisplayName("findUsernamesByIds: 유저들 존재 → Map 반환")
    void findUsernamesByIds_success() {
        Map<Long, String> expected = Map.of(1L,"a",2L,"b");
        given(userMultiQueryPort.findUsernamesByIds(List.of(1L,2L))).willReturn(expected);

        Map<Long, String> res = service.findUsernamesByIds(List.of(1L,2L));

        assertThat(res).containsEntry(1L,"a").containsEntry(2L,"b");
    }

    // ---------------- findUserThumbnailsByIds ----------------
    @Test
    @DisplayName("findUserThumbnailsByIds: null/빈 리스트 → 빈 Map 반환")
    void findUserThumbnailsByIds_empty() {
        assertThat(service.findUserThumbnailsByIds(null)).isEmpty();
        assertThat(service.findUserThumbnailsByIds(List.of())).isEmpty();
    }

    @Test
    @DisplayName("findUserThumbnailsByIds: 유저 썸네일 반환")
    void findUserThumbnailsByIds_success() {
        Map<Long, String> expected = Map.of(1L,"img1",2L,"img2");
        given(userMultiQueryPort.findUserThumbnailsByIds(List.of(1L,2L))).willReturn(expected);

        Map<Long,String> res = service.findUserThumbnailsByIds(List.of(1L,2L));

        assertThat(res).containsEntry(1L,"img1").containsEntry(2L,"img2");
    }

    // ---------------- findUserAuthorLevelIds ----------------
    @Test
    @DisplayName("findUserAuthorLevelIds: null/빈 리스트 → 빈 Map 반환")
    void findUserAuthorLevelIds_empty() {
        assertThat(service.findUserAuthorLevelIds(null)).isEmpty();
        assertThat(service.findUserAuthorLevelIds(List.of())).isEmpty();
    }

    @Test
    @DisplayName("findUserAuthorLevelIds: 유저 레벨 반환")
    void findUserAuthorLevelIds_success() {
        Map<Long, String> expected = Map.of(1L,"lv1",2L,"lv2");
        given(userMultiQueryPort.findUserAuthorLevelIds(List.of(1L,2L))).willReturn(expected);

        Map<Long,String> res = service.findUserAuthorLevelIds(List.of(1L,2L));

        assertThat(res).containsEntry(1L,"lv1").containsEntry(2L,"lv2");
    }

    // ---------------- extractUserInfo ----------------
    @Test
    @DisplayName("extractUserInfo: 유저 존재 → UserInfo 반환")
    void extractUserInfo_success() {
        given(userQueryPort.findUserById(10L)).willReturn(Optional.of(localUser()));

        UserInfo res = service.extractUserInfo(10L);

        assertThat(res.email()).isEqualTo("u@test.com");
        assertThat(res.nickname()).isEqualTo("nick");
    }

    @Test
    @DisplayName("extractUserInfo: 유저 없음 → NOT_FOUND_USER 예외")
    void extractUserInfo_notFound() {
        given(userQueryPort.findUserById(10L)).willReturn(Optional.empty());

        UserException ex = catchThrowableOfType(() -> service.extractUserInfo(10L), UserException.class);

        assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.NOT_FOUND_USER);
    }

    // ---------------- getUserInfo ----------------
    @Test
    @DisplayName("getUserInfo: 유저 존재 → 상세정보 반환")
    void getUserInfo_success() {
        User user = localUser();
        given(userQueryPort.findUserById(10L)).willReturn(Optional.of(user));
        given(getAuthorLevelLabelFromIdUseCase.getLabelById(1L)).willReturn("Author");
        given(getOccupationLabelFromIdUseCase.getLabelById(2L)).willReturn("Dev");
        given(getTopicLabelFromIdUseCase.getLabelsByIds(user.getTopicIds()))
                .willReturn(Map.of(100L,"TopicA",200L,"TopicB"));
        given(getVisitSourceLabelFromIdUseCase.getLabelById(3L)).willReturn("SNS");

        GetUserInfoResponse res = service.getUserInfo(10L);

        assertThat(res.nickname()).isEqualTo("nick");
        assertThat(res.authorLevelLabel()).isEqualTo("Author");
        assertThat(res.occupationLabel()).isEqualTo("Dev");
        assertThat(res.topicLabels()).containsExactly("TopicA","TopicB");
        assertThat(res.visitSourceLabel()).isEqualTo("SNS");
    }

    @Test
    @DisplayName("getUserInfo: 유저 없음 → NOT_FOUND_USER 예외")
    void getUserInfo_notFound() {
        given(userQueryPort.findUserById(10L)).willReturn(Optional.empty());

        UserException ex = catchThrowableOfType(() -> service.getUserInfo(10L), UserException.class);

        assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.NOT_FOUND_USER);
    }
}
