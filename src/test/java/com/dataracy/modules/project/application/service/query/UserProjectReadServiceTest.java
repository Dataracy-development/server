package com.dataracy.modules.project.application.service.query;

import com.dataracy.modules.project.application.dto.response.read.UserProjectResponse;
import com.dataracy.modules.project.application.mapper.read.UserProjectDtoMapper;
import com.dataracy.modules.project.application.port.out.query.read.FindUserProjectsPort;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserProjectReadServiceTest {

    @InjectMocks
    private UserProjectReadService service;

    @Mock
    private UserProjectDtoMapper mapper;

    @Mock
    private FindUserProjectsPort findUserProjectsPort;

    @Mock
    private GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;

    @Mock
    private GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;

    private Project sampleProject() {
        return Project.builder()
                .id(1L)
                .title("테스트 프로젝트")
                .content("내용입니다")
                .topicId(10L)
                .authorLevelId(20L)
                .thumbnailUrl("thumb.png")
                .commentCount(2L)
                .likeCount(5L)
                .viewCount(100L)
                .createdAt(LocalDateTime.of(2023, 8, 30, 12, 0))
                .build();
    }

    @Test
    @DisplayName("로그인한 회원이 작성한 프로젝트 목록 조회 성공 → UserProjectResponse 반환")
    void findUserProjectsSuccess() {
        // given
        Project project = sampleProject();
        Page<Project> page = new PageImpl<>(List.of(project), PageRequest.of(0, 5), 1);

        given(findUserProjectsPort.findUserProjects(1L, PageRequest.of(0, 5)))
                .willReturn(page);

        given(getTopicLabelFromIdUseCase.getLabelsByIds(List.of(10L)))
                .willReturn(Map.of(10L, "데이터 분석"));
        given(getAuthorLevelLabelFromIdUseCase.getLabelsByIds(List.of(20L)))
                .willReturn(Map.of(20L, "초급"));

        UserProjectResponse mockRes = new UserProjectResponse(
                1L, "테스트 프로젝트", "내용입니다", "thumb.png",
                "데이터 분석", "초급",
                2L, 5L, 100L,
                LocalDateTime.of(2023, 8, 30, 12, 0)
        );
        given(mapper.toResponseDto(any(), any(), any()))
                .willReturn(mockRes);

        // when
        Page<UserProjectResponse> result = service.findUserProjects(1L, PageRequest.of(0, 5));

        // then
        assertThat(result).hasSize(1);
        UserProjectResponse response = result.getContent().get(0);
        assertThat(response.title()).isEqualTo("테스트 프로젝트");
        assertThat(response.topicLabel()).isEqualTo("데이터 분석");
        assertThat(response.authorLevelLabel()).isEqualTo("초급");
        assertThat(response.likeCount()).isEqualTo(5L);
    }
}
