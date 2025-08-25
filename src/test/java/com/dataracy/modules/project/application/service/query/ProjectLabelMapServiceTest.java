package com.dataracy.modules.project.application.service.query;

import com.dataracy.modules.project.application.dto.response.support.ProjectLabelMapResponse;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.reference.application.port.in.analysispurpose.GetAnalysisPurposeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUsernameUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProjectLabelMapServiceTest {

    @Mock FindUsernameUseCase findUsernameUseCase;
    @Mock GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;
    @Mock GetAnalysisPurposeLabelFromIdUseCase getAnalysisPurposeLabelFromIdUseCase;
    @Mock GetDataSourceLabelFromIdUseCase getDataSourceLabelFromIdUseCase;
    @Mock GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;

    @InjectMocks ProjectLabelMapService service;

    @Test
    @DisplayName("labelMapping_should_collect_and_map_labels_for_projects")
    void labelMapping_should_collect_and_map_labels_for_projects() {
        // given
        List<Project> projects = List.of(
                Project.builder()
                        .id(1L).userId(100L)
                        .topicId(10L).analysisPurposeId(20L)
                        .dataSourceId(30L).authorLevelId(40L)
                        .build()
        );

        // username 은 findUsernamesByIds 호출됨
        given(findUsernameUseCase.findUsernamesByIds(List.of(100L)))
                .willReturn(Map.of(100L, "u"));

        // label 들은 getLabelsByIds 호출됨
        given(getTopicLabelFromIdUseCase.getLabelsByIds(List.of(10L)))
                .willReturn(Map.of(10L, "t"));
        given(getAnalysisPurposeLabelFromIdUseCase.getLabelsByIds(List.of(20L)))
                .willReturn(Map.of(20L, "ap"));
        given(getDataSourceLabelFromIdUseCase.getLabelsByIds(List.of(30L)))
                .willReturn(Map.of(30L, "ds"));
        given(getAuthorLevelLabelFromIdUseCase.getLabelsByIds(List.of(40L)))
                .willReturn(Map.of(40L, "al"));

        // when
        ProjectLabelMapResponse res = service.labelMapping(projects);

        // then
        assertThat(res.usernameMap()).isEqualTo(Map.of(100L, "u"));
        assertThat(res.topicLabelMap()).isEqualTo(Map.of(10L, "t"));
        assertThat(res.analysisPurposeLabelMap()).isEqualTo(Map.of(20L, "ap"));
        assertThat(res.dataSourceLabelMap()).isEqualTo(Map.of(30L, "ds"));
        assertThat(res.authorLevelLabelMap()).isEqualTo(Map.of(40L, "al"));
    }
}
