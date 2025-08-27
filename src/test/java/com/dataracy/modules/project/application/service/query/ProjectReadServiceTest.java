package com.dataracy.modules.project.application.service.query;

import com.dataracy.modules.dataset.application.port.in.query.read.FindConnectedDataSetsUseCase;
import com.dataracy.modules.like.application.port.in.validate.ValidateTargetLikeUseCase;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.dataracy.modules.project.application.dto.response.read.ConnectedProjectResponse;
import com.dataracy.modules.project.application.dto.response.read.ContinuedProjectResponse;
import com.dataracy.modules.project.application.dto.response.read.PopularProjectResponse;
import com.dataracy.modules.project.application.dto.response.read.ProjectDetailResponse;
import com.dataracy.modules.project.application.dto.response.support.ProjectLabelMapResponse;
import com.dataracy.modules.project.application.dto.response.support.ProjectWithDataIdsResponse;
import com.dataracy.modules.project.application.mapper.read.ConnectedProjectDtoMapper;
import com.dataracy.modules.project.application.mapper.read.ContinuedProjectDtoMapper;
import com.dataracy.modules.project.application.mapper.read.PopularProjectDtoMapper;
import com.dataracy.modules.project.application.mapper.read.ProjectDetailDtoMapper;
import com.dataracy.modules.project.application.mapper.support.ParentProjectDtoMapper;
import com.dataracy.modules.project.application.port.in.query.extractor.FindProjectLabelMapUseCase;
import com.dataracy.modules.project.application.port.out.query.read.FindConnectedProjectsPort;
import com.dataracy.modules.project.application.port.out.query.read.FindContinuedProjectsPort;
import com.dataracy.modules.project.application.port.out.query.read.FindProjectPort;
import com.dataracy.modules.project.application.port.out.query.read.GetPopularProjectsPort;
import com.dataracy.modules.project.application.port.out.query.validate.CheckProjectExistsByParentPort;
import com.dataracy.modules.project.application.port.out.view.ManageProjectViewCountPort;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.reference.application.port.in.analysispurpose.GetAnalysisPurposeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.GetOccupationLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUserThumbnailUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUsernameUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.GetUserInfoUseCase;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.vo.UserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectReadServiceTest {

    @Mock
    private ContinuedProjectDtoMapper continuedProjectDtoMapper;

    @Mock
    private ConnectedProjectDtoMapper connectedProjectDtoMapper;

    @Mock
    private PopularProjectDtoMapper popularProjectDtoMapper;

    @Mock
    private ProjectDetailDtoMapper projectDetailDtoMapper;

    @Mock
    private ParentProjectDtoMapper parentProjectDtoMapper;

    @Mock
    private ManageProjectViewCountPort manageProjectViewCountPort;

    @Mock
    private CheckProjectExistsByParentPort checkProjectExistsByParentPort;

    @Mock
    private FindProjectPort findProjectPort;

    @Mock
    private FindContinuedProjectsPort findContinuedProjectsPort;

    @Mock
    private FindConnectedProjectsPort findConnectedProjectsPort;

    @Mock
    private GetPopularProjectsPort getPopularProjectsPort;

    @Mock
    private GetUserInfoUseCase getUserInfoUseCase;

    @Mock
    private FindUsernameUseCase findUsernameUseCase;

    @Mock
    private FindUserThumbnailUseCase findUserThumbnailUseCase;

    @Mock
    private GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;

    @Mock
    private GetAnalysisPurposeLabelFromIdUseCase getAnalysisPurposeLabelFromIdUseCase;

    @Mock
    private GetDataSourceLabelFromIdUseCase getDataSourceLabelFromIdUseCase;

    @Mock
    private GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;

    @Mock
    private GetOccupationLabelFromIdUseCase getOccupationLabelFromIdUseCase;

    @Mock
    private FindProjectLabelMapUseCase findProjectLabelMapUseCase;

    @Mock
    private FindConnectedDataSetsUseCase findConnectedDataSetsUseCase;

    @Mock
    private ValidateTargetLikeUseCase validateTargetLikeUseCase;

    @InjectMocks
    private ProjectReadService service;

    @Test
    @DisplayName("프로젝트 상세 조회 성공")
    void getProjectDetailSuccess() {
        // given
        Long projectId = 1L;
        Project project = Project.builder()
                .id(projectId).userId(100L)
                .topicId(10L).analysisPurposeId(20L).dataSourceId(30L).authorLevelId(40L)
                .title("테스트 프로젝트").content("내용").createdAt(LocalDateTime.now())
                .commentCount(5L).likeCount(10L).viewCount(15L)
                .build();

        ProjectWithDataIdsResponse wrapper = new ProjectWithDataIdsResponse(project, List.of(11L, 22L));
        given(findProjectPort.findProjectWithDataById(projectId)).willReturn(Optional.of(wrapper));
        given(findConnectedDataSetsUseCase.findDataSetsByIds(List.of(11L, 22L))).willReturn(List.of());
        given(checkProjectExistsByParentPort.checkParentProjectExistsById(projectId)).willReturn(true);

        UserInfo userInfo = new UserInfo(
                100L, RoleType.ROLE_USER, "user@test.com", "nick",
                40L, 50L, List.of(), 77L,
                "profile.png", "소개글"
        );
        given(getUserInfoUseCase.extractUserInfo(100L)).willReturn(userInfo);

        given(validateTargetLikeUseCase.hasUserLikedTarget(200L, projectId, TargetType.PROJECT)).willReturn(true);

        given(getTopicLabelFromIdUseCase.getLabelById(10L)).willReturn("TopicLabel");
        given(getAnalysisPurposeLabelFromIdUseCase.getLabelById(20L)).willReturn("PurposeLabel");
        given(getDataSourceLabelFromIdUseCase.getLabelById(30L)).willReturn("SourceLabel");
        given(getAuthorLevelLabelFromIdUseCase.getLabelById(40L)).willReturn("AuthorLabel");
        given(getOccupationLabelFromIdUseCase.getLabelById(50L)).willReturn("OccupationLabel");

        ProjectDetailResponse expected = new ProjectDetailResponse(
                1L, "테스트 프로젝트", "nick", "소개글", "profile.png",
                "AuthorLabel", "OccupationLabel", "TopicLabel", "PurposeLabel", "SourceLabel",
                false, null, "내용", "thumb.png", LocalDateTime.now(),
                5L, 10L, 15L,
                true, true, List.of(), null
        );
        given(projectDetailDtoMapper.toResponseDto(
                eq(project), eq("nick"), eq("소개글"), eq("profile.png"),
                eq("AuthorLabel"), eq("OccupationLabel"),
                eq("TopicLabel"), eq("PurposeLabel"), eq("SourceLabel"),
                eq(true), eq(true), anyList(), any()
        )).willReturn(expected);

        // when
        ProjectDetailResponse result = service.getProjectDetail(projectId, 200L, "viewer1");

        // then
        assertThat(result).isEqualTo(expected);
        then(manageProjectViewCountPort).should().increaseViewCount(projectId, "viewer1", "PROJECT");
    }

    @Test
    @DisplayName("프로젝트 상세 조회 실패 - 존재하지 않음")
    void getProjectDetailFailWhenNotFound() {
        // given
        given(findProjectPort.findProjectWithDataById(999L)).willReturn(Optional.empty());

        // when
        ProjectException ex = catchThrowableOfType(() -> service.getProjectDetail(999L, null, "viewerX"), ProjectException.class);

        // then
        assertThat(ex).isNotNull();
    }

    @Test
    @DisplayName("이어가기 프로젝트 조회 성공")
    void findContinuedProjectsSuccess() {
        // given
        Long projectId = 1L;
        Project child = Project.builder().id(2L).userId(200L).topicId(10L).authorLevelId(40L).build();
        Page<Project> page = new PageImpl<>(List.of(child));
        given(findContinuedProjectsPort.findContinuedProjects(eq(projectId), any())).willReturn(page);
        given(findUsernameUseCase.findUsernamesByIds(List.of(200L))).willReturn(Map.of(200L, "userA"));
        given(findUserThumbnailUseCase.findUserThumbnailsByIds(List.of(200L))).willReturn(Map.of(200L, "thumb.png"));
        given(getTopicLabelFromIdUseCase.getLabelsByIds(List.of(10L))).willReturn(Map.of(10L, "TopicLabel"));
        given(getAuthorLevelLabelFromIdUseCase.getLabelsByIds(List.of(40L))).willReturn(Map.of(40L, "AuthorLabel"));

        ContinuedProjectResponse expected = new ContinuedProjectResponse(
                2L, "child", "userA", "thumb.png", "proj-thumb.png",
                "TopicLabel", "AuthorLabel",
                1L, 2L, 3L, LocalDateTime.now()
        );
        given(continuedProjectDtoMapper.toResponseDto(any(), eq("userA"), eq("thumb.png"), eq("TopicLabel"), eq("AuthorLabel")))
                .willReturn(expected);

        // when
        Page<ContinuedProjectResponse> result = service.findContinuedProjects(projectId, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).contains(expected);
    }

    @Test
    @DisplayName("연결된 프로젝트 조회 성공")
    void findConnectedProjectsSuccess() {
        // given
        Long dataId = 5L;
        Project connected = Project.builder().id(10L).userId(300L).topicId(99L).build();
        Page<Project> page = new PageImpl<>(List.of(connected));
        given(findConnectedProjectsPort.findConnectedProjectsAssociatedWithDataset(eq(dataId), any())).willReturn(page);
        given(findUsernameUseCase.findUsernamesByIds(List.of(300L))).willReturn(Map.of(300L, "userB"));
        given(getTopicLabelFromIdUseCase.getLabelsByIds(List.of(99L))).willReturn(Map.of(99L, "TopicX"));

        ConnectedProjectResponse expected = new ConnectedProjectResponse(
                10L, "proj", "userB", "TopicX", 0L, 0L, 0L, LocalDateTime.now()
        );
        given(connectedProjectDtoMapper.toResponseDto(any(), eq("userB"), eq("TopicX"))).willReturn(expected);

        // when
        Page<ConnectedProjectResponse> result = service.findConnectedProjects(dataId, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).contains(expected);
    }

    @Test
    @DisplayName("인기 프로젝트 조회 성공")
    void getPopularProjectsSuccess() {
        // given
        Project p = Project.builder().id(1L).userId(100L).topicId(10L).analysisPurposeId(20L).dataSourceId(30L).authorLevelId(40L).build();
        given(getPopularProjectsPort.getPopularProjects(3)).willReturn(List.of(p));
        ProjectLabelMapResponse labelMap = new ProjectLabelMapResponse(
                Map.of(100L, "userC"),
                Map.of(10L, "TopicY"),
                Map.of(20L, "PurposeY"),
                Map.of(30L, "SourceY"),
                Map.of(40L, "AuthorY")
        );
        given(findProjectLabelMapUseCase.labelMapping(List.of(p))).willReturn(labelMap);

        PopularProjectResponse expected = new PopularProjectResponse(
                1L, "proj", "content", "userC", "thumb.png",
                "TopicY", "PurposeY", "SourceY", "AuthorY",
                1L, 2L, 3L
        );
        given(popularProjectDtoMapper.toResponseDto(p, "userC", "TopicY", "PurposeY", "SourceY", "AuthorY")).willReturn(expected);

        // when
        List<PopularProjectResponse> result = service.getPopularProjects(3);

        // then
        assertThat(result).contains(expected);
    }
}
