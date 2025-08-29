package com.dataracy.modules.user.application.service.query.profile;

import com.dataracy.modules.dataset.application.dto.response.read.UserDataResponse;
import com.dataracy.modules.dataset.application.port.in.query.read.FindUserDataSetsUseCase;
import com.dataracy.modules.project.application.dto.response.read.UserProjectResponse;
import com.dataracy.modules.project.application.port.in.query.read.FindUserProjectsUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.GetOccupationLabelFromIdUseCase;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserDataResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserInfoResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserProjectResponse;
import com.dataracy.modules.user.application.mapper.external.OtherUserInfoMapper;
import com.dataracy.modules.user.application.port.out.query.UserQueryPort;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class OtherUserProfileServiceTest {

    @Mock
    private OtherUserInfoMapper otherUserInfoMapper;

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;

    @Mock
    private GetOccupationLabelFromIdUseCase getOccupationLabelFromIdUseCase;

    @Mock
    private FindUserProjectsUseCase findUserProjectsUseCase;

    @Mock
    private FindUserDataSetsUseCase findUserDataSetsUseCase;

    @InjectMocks OtherUserProfileService service;

    @Test
    @DisplayName("유저 존재 시 타인 유저 정보 조회 성공")
    void getOtherUserInfoSuccess() {
        // given
        Long userId = 1L;
        User user = User.of(
                userId, null, "pid", null,
                "user@test.com", "pw", "닉네임", 2L,
                3L, List.of(), null,
                "profile.png", "자기소개", true, false
        );

        given(userQueryPort.findUserById(userId)).willReturn(Optional.of(user));
        given(getAuthorLevelLabelFromIdUseCase.getLabelById(2L)).willReturn("중급");
        given(getOccupationLabelFromIdUseCase.getLabelById(3L)).willReturn("데이터 분석가");

        // 프로젝트
        UserProjectResponse projectDto = new UserProjectResponse(
                10L, "프로젝트", "내용", "thumb.png",
                "주제", "레벨", 2L, 5L, 100L,
                LocalDateTime.of(2023, 8, 30, 12, 0)
        );
        Page<UserProjectResponse> projectPage = new PageImpl<>(List.of(projectDto));
        given(findUserProjectsUseCase.findUserProjects(eq(userId), isNull())).willReturn(projectPage);

        GetOtherUserProjectResponse mappedProject = new GetOtherUserProjectResponse(
                projectDto.id(), projectDto.title(), projectDto.content(),
                projectDto.projectThumbnailUrl(), projectDto.topicLabel(), projectDto.authorLevelLabel(),
                projectDto.commentCount(), projectDto.likeCount(), projectDto.viewCount(), projectDto.createdAt()
        );
        given(otherUserInfoMapper.toOtherUserProject(projectDto)).willReturn(mappedProject);

        // 데이터셋
        UserDataResponse dataDto = new UserDataResponse(
                20L, "데이터셋", "주제", "타입",
                LocalDate.of(2023,1,1), LocalDate.of(2023,12,31),
                "data-thumb.png", 10, 2048L, 200, 20,
                LocalDateTime.of(2023, 8, 30, 13, 0), 7L
        );
        Page<UserDataResponse> dataPage = new PageImpl<>(List.of(dataDto));
        given(findUserDataSetsUseCase.findUserDataSets(eq(userId), isNull())).willReturn(dataPage);

        GetOtherUserDataResponse mappedData = new GetOtherUserDataResponse(
                dataDto.id(), dataDto.title(), dataDto.topicLabel(), dataDto.dataTypeLabel(),
                dataDto.startDate(), dataDto.endDate(), dataDto.dataThumbnailUrl(),
                dataDto.downloadCount(), dataDto.sizeBytes(), dataDto.rowCount(), dataDto.columnCount(),
                dataDto.createdAt(), dataDto.countConnectedProjects()
        );
        given(otherUserInfoMapper.toOtherUserData(dataDto)).willReturn(mappedData);

        // when
        GetOtherUserInfoResponse result = service.getOtherUserInfo(userId);

        // then
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.nickname()).isEqualTo("닉네임");
        assertThat(result.authorLevelLabel()).isEqualTo("중급");
        assertThat(result.occupationLabel()).isEqualTo("데이터 분석가");
        assertThat(result.profileImageUrl()).isEqualTo("profile.png");
        assertThat(result.introductionText()).isEqualTo("자기소개");
        assertThat(result.projects().getContent()).containsExactly(mappedProject);
        assertThat(result.datasets().getContent()).containsExactly(mappedData);
    }

    @Test
    @DisplayName("유저 존재하지 않을 시 UserException 발생")
    void getOtherUserInfoUserNotFound() {
        // given
        Long userId = 99L;
        given(userQueryPort.findUserById(userId)).willReturn(Optional.empty());

        // when
        Throwable ex = catchThrowable(() -> service.getOtherUserInfo(userId));

        // then
        assertThat(ex).isInstanceOf(UserException.class)
                .hasMessage(UserErrorStatus.NOT_FOUND_USER.getMessage());
    }

    @Test
    @DisplayName("추가 프로젝트 조회 성공")
    void getOtherExtraProjectsSuccess() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 5);

        UserProjectResponse projectDto = new UserProjectResponse(
                11L, "Extra 프로젝트", "내용", "thumb.png",
                "주제", "레벨", 2L, 5L, 100L, LocalDateTime.now()
        );
        Page<UserProjectResponse> projectPage = new PageImpl<>(List.of(projectDto));
        given(findUserProjectsUseCase.findUserProjects(userId, pageable)).willReturn(projectPage);

        GetOtherUserProjectResponse mappedProject = new GetOtherUserProjectResponse(
                projectDto.id(), projectDto.title(), projectDto.content(),
                projectDto.projectThumbnailUrl(), projectDto.topicLabel(), projectDto.authorLevelLabel(),
                projectDto.commentCount(), projectDto.likeCount(), projectDto.viewCount(), projectDto.createdAt()
        );
        given(otherUserInfoMapper.toOtherUserProject(projectDto)).willReturn(mappedProject);

        // when
        Page<GetOtherUserProjectResponse> result = service.getOtherExtraProjects(userId, pageable);

        // then
        assertThat(result.getContent()).containsExactly(mappedProject);
    }

    @Test
    @DisplayName("추가 데이터셋 조회 성공")
    void getOtherExtraDataSetsSuccess() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 5);

        UserDataResponse dataDto = new UserDataResponse(
                21L, "Extra 데이터셋", "주제", "타입",
                LocalDate.of(2023,1,1), LocalDate.of(2023,12,31),
                "data-thumb.png", 10, 2048L, 200, 20,
                LocalDateTime.now(), 7L
        );
        Page<UserDataResponse> dataPage = new PageImpl<>(List.of(dataDto));
        given(findUserDataSetsUseCase.findUserDataSets(userId, pageable)).willReturn(dataPage);

        GetOtherUserDataResponse mappedData = new GetOtherUserDataResponse(
                dataDto.id(), dataDto.title(), dataDto.topicLabel(), dataDto.dataTypeLabel(),
                dataDto.startDate(), dataDto.endDate(), dataDto.dataThumbnailUrl(),
                dataDto.downloadCount(), dataDto.sizeBytes(), dataDto.rowCount(), dataDto.columnCount(),
                dataDto.createdAt(), dataDto.countConnectedProjects()
        );
        given(otherUserInfoMapper.toOtherUserData(dataDto)).willReturn(mappedData);

        // when
        Page<GetOtherUserDataResponse> result = service.getOtherExtraDataSets(userId, pageable);

        // then
        assertThat(result.getContent()).containsExactly(mappedData);
    }
}
