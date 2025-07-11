package com.dataracy.modules.project.application.service.command;

import com.dataracy.modules.project.application.dto.request.ProjectUploadRequest;
import com.dataracy.modules.project.application.port.in.ProjectUploadUseCase;
import com.dataracy.modules.project.application.port.out.ProjectRepositoryPort;
import com.dataracy.modules.project.domain.model.Project;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectCommandService implements ProjectUploadUseCase {

    private final ProjectRepositoryPort projectRepositoryPort;

    @Override
    @Transactional
    public void upload(Long userId, ProjectUploadRequest requestDto) {
        log.info("프로젝트 업로드 시작 - userId: {}, title: {}", userId, requestDto.title());
                // 프로젝트 아이디로 부모 프로젝트 조회
        Project parentProject= projectRepositoryPort.findProjectById(requestDto.parentProjectId());

        Project project = Project.builder()
                .id(null)
                .title(requestDto.title())
                .topicId(requestDto.topicId())
                .userId(userId)
                .analysisPurposeId(requestDto.analysisPurposeId())
                .dataSourceId(requestDto.dataSourceId())
                .authorLevelId(requestDto.authorLevelId())
                .isContinue(requestDto.isContinue())
                .parentProject(parentProject)
                .content(requestDto.content())
                .build();

        projectRepositoryPort.saveProject(project);
        log.info("프로젝트 업로드 완료 - userId: {}, title: {}", userId, requestDto.title());
    }
}
