package com.dataracy.modules.project.application.service.query;

import com.dataracy.modules.project.application.dto.response.support.ProjectLabelMapResponse;
import com.dataracy.modules.project.application.port.in.query.extractor.FindProjectLabelMapUseCase;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.reference.application.port.in.analysispurpose.GetAnalysisPurposeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.profile.FindUsernameUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectLabelMapService implements FindProjectLabelMapUseCase {

    private final FindUsernameUseCase findUsernameUseCase;
    private final GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;
    private final GetAnalysisPurposeLabelFromIdUseCase getAnalysisPurposeLabelFromIdUseCase;
    private final GetDataSourceLabelFromIdUseCase getDataSourceLabelFromIdUseCase;
    private final GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;

    /**
     * 프로젝트 컬렉션에서 사용자, 토픽, 분석 목적, 데이터 소스, 저자 레벨의 ID를 추출하여 각 ID에 해당하는 사용자명과 레이블 정보를 매핑하여 반환합니다.
     *
     * @param savedProjects 매핑할 프로젝트 객체들의 컬렉션
     * @return 각 ID에 대한 사용자명과 레이블 매핑 정보를 포함하는 ProjectLabelMapResponse 객체
     */
    public ProjectLabelMapResponse labelMapping(Collection<Project> savedProjects) {
        List<Long> userIds = savedProjects.stream().map(Project::getUserId).toList();
        List<Long> topicIds = savedProjects.stream().map(Project::getTopicId).toList();
        List<Long> analysisPurposeIds = savedProjects.stream().map(Project::getAnalysisPurposeId).toList();
        List<Long> dataSourceIds = savedProjects.stream().map(Project::getDataSourceId).toList();
        List<Long> authorLevelIds = savedProjects.stream().map(Project::getAuthorLevelId).toList();

        return new ProjectLabelMapResponse(
                findUsernameUseCase.findUsernamesByIds(userIds),
                getTopicLabelFromIdUseCase.getLabelsByIds(topicIds),
                getAnalysisPurposeLabelFromIdUseCase.getLabelsByIds(analysisPurposeIds),
                getDataSourceLabelFromIdUseCase.getLabelsByIds(dataSourceIds),
                getAuthorLevelLabelFromIdUseCase.getLabelsByIds(authorLevelIds)
        );
    }
}
