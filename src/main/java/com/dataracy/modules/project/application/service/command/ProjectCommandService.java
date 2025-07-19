package com.dataracy.modules.project.application.service.command;

import com.dataracy.modules.common.util.FileUtil;
import com.dataracy.modules.data.application.port.in.ValidateDataUseCase;
import com.dataracy.modules.filestorage.application.port.in.FileUploadUseCase;
import com.dataracy.modules.filestorage.support.util.S3KeyGeneratorUtil;
import com.dataracy.modules.project.adapter.index.document.ProjectSearchDocument;
import com.dataracy.modules.project.application.dto.request.ProjectUploadRequest;
import com.dataracy.modules.project.application.port.in.ProjectUploadUseCase;
import com.dataracy.modules.project.application.port.out.ProjectIndexingPort;
import com.dataracy.modules.project.application.port.out.ProjectRepositoryPort;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import com.dataracy.modules.reference.application.port.in.analysispurpose.GetAnalysisPurposeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.user.FindUsernameUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectCommandService implements ProjectUploadUseCase {
    private final ProjectRepositoryPort projectRepositoryPort;
    private final ProjectIndexingPort projectIndexingPort;

    private final FindUsernameUseCase findUsernameUseCase;
    private final FileUploadUseCase fileUploadUseCase;

    private final GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;
    private final GetAnalysisPurposeLabelFromIdUseCase getAnalysisPurposeLabelFromIdUseCase;
    private final GetDataSourceLabelFromIdUseCase getDataSourceLabelFromIdUseCase;
    private final GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;
    private final ValidateDataUseCase validateDataUseCase;

    @Value("${default.image.url:}")
    private String defaultImageUrl;

    /**
     * 주어진 사용자 ID와 프로젝트 요청 정보를 기반으로 새 프로젝트를 생성하고, 필요 시 썸네일 이미지를 외부 저장소에 업로드합니다.
     *
     * 프로젝트의 주요 필드(주제, 분석 목적, 데이터 소스, 저자 레벨)는 ID 유효성 검사와 함께 라벨 값을 조회하여 저장 및 색인에 활용합니다. 데이터셋 ID 목록의 각 항목에 대해 존재 여부를 검증합니다. 부모 프로젝트가 지정된 경우 존재 여부를 확인하며, 존재하지 않으면 예외가 발생합니다. 이미지 파일이 제공되면 업로드 후 프로젝트 정보에 반영하며, 업로드 실패 시 전체 트랜잭션이 롤백됩니다. 프로젝트 저장 후 검색 시스템에 색인되어 외부 검색이 가능해집니다.
     */
    @Override
    @Transactional
    public void upload(Long userId, MultipartFile file, ProjectUploadRequest requestDto) {
        log.info("프로젝트 업로드 시작 - userId: {}, title: {}", userId, requestDto.title());

        // 해당 id가 존재하는지 내부 유효성 검사 및 라벨 값 반환
        String topicLabel = getTopicLabelFromIdUseCase.getLabelById(requestDto.topicId());
        String analysisPurposeLabel = getAnalysisPurposeLabelFromIdUseCase.getLabelById(requestDto.analysisPurposeId());
        String dataSourceLabel = getDataSourceLabelFromIdUseCase.getLabelById(requestDto.dataSourceId());
        String authorLevelLabel = getAuthorLevelLabelFromIdUseCase.getLabelById(requestDto.authorLevelId());

        // 데이터셋 id를 통해 데이터셋 존재 유효성 검사를 시행한다.
        if (requestDto.dataIds() != null) {
            requestDto.dataIds()
                    .forEach(validateDataUseCase::validateData);
        }

        // 파일 유효성 검사
        FileUtil.validateImageFile(file);

        // 부모 프로젝트 조회
        Project parentProject = null;
        if (requestDto.parentProjectId() != null) {
            parentProject = projectRepositoryPort.findProjectById(requestDto.parentProjectId())
                    .orElseThrow(() -> new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT));
        }

        // 프로젝트 업로드 DB 저장
        Project project = Project.toDomain(
                null,
                requestDto.title(),
                requestDto.topicId(),
                userId,
                requestDto.analysisPurposeId(),
                requestDto.dataSourceId(),
                requestDto.authorLevelId(),
                requestDto.isContinue(),
                parentProject,
                requestDto.content(),
                defaultImageUrl,
                requestDto.dataIds(),
                null,
                0L,
                0L,
                0L,
                false
                );
        Project saveProject = projectRepositoryPort.saveProject(project);

        // DB 저장 성공 후 파일 업로드 시도, 외부 서비스로 트랜잭션의 영향을 받지 않는다.
        if (file != null && !file.isEmpty()) {
            try {
                String key = S3KeyGeneratorUtil.generateKey("project", saveProject.getId(), file.getOriginalFilename());
                String fileUrl = fileUploadUseCase.uploadFile(key, file);
                log.info("프로젝트 파일 업로드 성공 - url={}", fileUrl);

                // 이미지 업로드 저장
                project.updateFile(fileUrl);
                projectRepositoryPort.updateFile(saveProject.getId(), fileUrl);
            } catch (Exception e) {
                log.error("프로젝트 파일 업로드 실패. 프로젝트 ID={}, 에러={}", saveProject.getId(), e.getMessage());
                throw new RuntimeException("파일 업로드 실패", e); // rollback 유도
            }
        }

        // 검색을 위해 elasticSearch에 프로젝트를 등록한다.
        String username = findUsernameUseCase.findUsernameById(userId);
        projectIndexingPort.index(ProjectSearchDocument.from(
                saveProject,
                topicLabel,
                analysisPurposeLabel,
                dataSourceLabel,
                authorLevelLabel,
                username
        ));

        log.info("프로젝트 업로드 완료 - userId: {}, title: {}", userId, requestDto.title());
    }
}
