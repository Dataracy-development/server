package com.dataracy.modules.project.application.service.query;

import com.dataracy.modules.project.application.dto.response.support.ProjectLabelMapResponse;
import com.dataracy.modules.project.application.port.in.query.extractor.FindProjectLabelMapUseCase;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.reference.application.port.in.analysispurpose.GetAnalysisPurposeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUserThumbnailUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUsernameUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectLabelMapService implements FindProjectLabelMapUseCase {

    private final FindUsernameUseCase findUsernameUseCase;
    private final FindUserThumbnailUseCase findUserThumbnailUseCase;

    private final GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;
    private final GetAnalysisPurposeLabelFromIdUseCase getAnalysisPurposeLabelFromIdUseCase;
    private final GetDataSourceLabelFromIdUseCase getDataSourceLabelFromIdUseCase;
    private final GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;

    /**
     * 프로젝트 컬렉션에서 관련 ID들을 추출해 사용자명, 사용자 썸네일 및 각 레이블로 매핑한 결과를 반환합니다.
     *
     * <p>각 Project에서 userId, topicId, analysisPurposeId, dataSourceId, authorLevelId를 수집한 뒤,
     * 해당 ID들을 관련 UseCase에 위임하여 사용자명, 사용자 썸네일, 토픽 레이블, 분석 목적 레이블,
     * 데이터 소스 레이블 및 저자 레벨 레이블을 조회해 ProjectLabelMapResponse로 합칩니다.</p>
     *
     * @param savedProjects 매핑 대상인 Project 객체들의 컬렉션
     * @return 사용자명, 사용자 썸네일, 토픽/분석 목적/데이터 소스/저자 레벨에 대한 ID→레이블 매핑을 포함한 ProjectLabelMapResponse
     */
    public ProjectLabelMapResponse labelMapping(Collection<Project> savedProjects) {
        List<Long> userIds = savedProjects.stream().map(Project::getUserId).toList();
        List<Long> topicIds = savedProjects.stream().map(Project::getTopicId).toList();
        List<Long> analysisPurposeIds = savedProjects.stream().map(Project::getAnalysisPurposeId).toList();
        List<Long> dataSourceIds = savedProjects.stream().map(Project::getDataSourceId).toList();
        List<Long> authorLevelIds = savedProjects.stream().map(Project::getAuthorLevelId).toList();

        return new ProjectLabelMapResponse(
                findUsernameUseCase.findUsernamesByIds(userIds),
                findUserThumbnailUseCase.findUserThumbnailsByIds(userIds),
                getTopicLabelFromIdUseCase.getLabelsByIds(topicIds),
                getAnalysisPurposeLabelFromIdUseCase.getLabelsByIds(analysisPurposeIds),
                getDataSourceLabelFromIdUseCase.getLabelsByIds(dataSourceIds),
                getAuthorLevelLabelFromIdUseCase.getLabelsByIds(authorLevelIds)
        );
    }
}
