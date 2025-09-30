package com.dataracy.modules.project.application.service.batch;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.dto.response.read.PopularProjectResponse;
import com.dataracy.modules.project.application.dto.response.support.ProjectLabelMapResponse;
import com.dataracy.modules.project.application.mapper.read.PopularProjectDtoMapper;
import com.dataracy.modules.project.application.port.out.query.read.GetPopularProjectsPort;
import com.dataracy.modules.project.application.port.out.storage.PopularProjectsStoragePort;
import com.dataracy.modules.project.application.port.in.query.extractor.FindProjectLabelMapUseCase;
import com.dataracy.modules.project.application.port.in.storage.UpdatePopularProjectsStorageUseCase;
import com.dataracy.modules.project.domain.model.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 인기 프로젝트 목록을 주기적으로 계산하고 캐시에 저장하는 배치 서비스
 */
@Service
@RequiredArgsConstructor
public class PopularProjectsBatchService implements UpdatePopularProjectsStorageUseCase {
    
    private final PopularProjectDtoMapper popularProjectDtoMapper;
    private final FindProjectLabelMapUseCase findProjectLabelMapUseCase;
    private final PopularProjectsStoragePort popularProjectsStoragePort;
    private final GetPopularProjectsPort getPopularProjectsPort;

    // Service 상수 정의
    private static final String POPULAR_PROJECTS_BATCH_SERVICE = "PopularProjectsBatchService";
    
    /**
     * 매 5분마다 인기 프로젝트 목록을 계산하고 캐시에 저장합니다.
     * 
     * 실제 운영에서는 더 긴 주기(예: 30분)로 설정할 수 있습니다.
     */
    @Scheduled(fixedRate = 300000) // 5분 = 300,000ms
    public void updatePopularProjectsCache() {
        LoggerFactory.scheduler().logStart(POPULAR_PROJECTS_BATCH_SERVICE);
        
        try {
            // Port Out을 통해 데이터베이스에서 인기 프로젝트 조회 (최대 20개)
            List<Project> savedProjects = getPopularProjectsPort.getPopularProjects(20);
            
            // 라벨 매핑
            ProjectLabelMapResponse labelResponse = findProjectLabelMapUseCase.labelMapping(savedProjects);
            
            // PopularProjectResponse로 변환
            List<PopularProjectResponse> popularProjects = savedProjects.stream()
                    .map(project -> popularProjectDtoMapper.toResponseDto(
                            project,
                            labelResponse.usernameMap().get(project.getUserId()),
                            labelResponse.userProfileUrlMap().get(project.getUserId()),
                            labelResponse.topicLabelMap().get(project.getTopicId()),
                            labelResponse.analysisPurposeLabelMap().get(project.getAnalysisPurposeId()),
                            labelResponse.dataSourceLabelMap().get(project.getDataSourceId()),
                            labelResponse.authorLevelLabelMap().get(project.getAuthorLevelId())
                    ))
                    .toList();
            
            // 저장소에 저장
            popularProjectsStoragePort.setPopularProjects(popularProjects);
            
            LoggerFactory.scheduler().logComplete("PopularProjectsBatchService - count=" + popularProjects.size());
                
        } catch (Exception e) {
            LoggerFactory.scheduler().logError(POPULAR_PROJECTS_BATCH_SERVICE, e);
        }
    }
    
    /**
     * 수동으로 인기 프로젝트 캐시를 업데이트합니다.
     * 
     * @param size 조회할 프로젝트 개수
     */
    public void manualUpdatePopularProjectsCache(int size) {
        LoggerFactory.scheduler().logStart("PopularProjectsBatchService-Manual");
        
        try {
            // Port Out을 통해 데이터베이스에서 인기 프로젝트 조회
            List<Project> savedProjects = getPopularProjectsPort.getPopularProjects(size);
            
            // 라벨 매핑
            ProjectLabelMapResponse labelResponse = findProjectLabelMapUseCase.labelMapping(savedProjects);
            
            // PopularProjectResponse로 변환
            List<PopularProjectResponse> popularProjects = savedProjects.stream()
                    .map(project -> popularProjectDtoMapper.toResponseDto(
                            project,
                            labelResponse.usernameMap().get(project.getUserId()),
                            labelResponse.userProfileUrlMap().get(project.getUserId()),
                            labelResponse.topicLabelMap().get(project.getTopicId()),
                            labelResponse.analysisPurposeLabelMap().get(project.getAnalysisPurposeId()),
                            labelResponse.dataSourceLabelMap().get(project.getDataSourceId()),
                            labelResponse.authorLevelLabelMap().get(project.getAuthorLevelId())
                    ))
                    .toList();
            
            // 저장소에 저장
            popularProjectsStoragePort.setPopularProjects(popularProjects);
            
            LoggerFactory.scheduler().logComplete("PopularProjectsBatchService-Manual - count=" + popularProjects.size());
                
        } catch (Exception e) {
            LoggerFactory.scheduler().logError("PopularProjectsBatchService-Manual", e);
        }
    }
    
    /**
     * 캐시가 존재하지 않을 때 즉시 업데이트합니다.
     * 
     * @param size 조회할 프로젝트 개수
     */
    @Override
    public void warmUpCacheIfNeeded(int size) {
        LoggerFactory.scheduler().logStart("PopularProjectsBatchService-WarmUpCheck");
        boolean hasValidData = popularProjectsStoragePort.hasValidData();
        LoggerFactory.scheduler().logStart("PopularProjectsBatchService-WarmUpCheck - hasValidData=" + hasValidData);
        
        if (!hasValidData) {
            LoggerFactory.scheduler().logStart("PopularProjectsBatchService-WarmUp");
            manualUpdatePopularProjectsCache(size);
        } else {
            LoggerFactory.scheduler().logComplete("PopularProjectsBatchService-WarmUpCheck - 캐시가 이미 존재하여 워밍업 생략");
        }
    }
}
