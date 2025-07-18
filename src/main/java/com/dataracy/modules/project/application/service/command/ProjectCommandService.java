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
import com.dataracy.modules.reference.application.port.in.analysispurpose.ValidateAnalysisPurposeUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.ValidateAuthorLevelUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.ValidateDataSourceUseCase;
import com.dataracy.modules.reference.application.port.in.topic.ValidateTopicUseCase;
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
    private final ValidateTopicUseCase validateTopicUseCase;
    private final ValidateAnalysisPurposeUseCase validateAnalysisPurposeUseCase;
    private final ValidateDataSourceUseCase validateDataSourceUseCase;;
    private final ValidateAuthorLevelUseCase validateAuthorLevelUseCase;
    private final ValidateDataUseCase validateDataUseCase;

    @Value("${default.image.url:}")
    private String defaultImageUrl;

    /**
     * 사용자 ID와 프로젝트 요청 정보를 기반으로 새 프로젝트를 생성하고, 썸네일 이미지가 제공된 경우 외부 저장소에 업로드합니다.
     *
     * 프로젝트 정보는 데이터베이스에 저장되며, 이미지 파일 업로드에 실패할 경우 전체 트랜잭션이 롤백됩니다. 프로젝트 저장 후, 검색 시스템에 프로젝트가 색인되어 외부 검색이 가능해집니다.
     *
     * @param userId 프로젝트를 업로드하는 사용자의 ID
     * @param file 프로젝트 썸네일로 사용할 이미지 파일 (선택 사항)
     * @param requestDto 프로젝트 생성에 필요한 정보가 담긴 요청 객체
     */
    @Override
    @Transactional
    public void upload(Long userId, MultipartFile file, ProjectUploadRequest requestDto) {
        log.info("프로젝트 업로드 시작 - userId: {}, title: {}", userId, requestDto.title());

        // 유효성 검사
        validateTopicUseCase.validateTopic(requestDto.topicId());
        validateAnalysisPurposeUseCase.validateAnalysisPurpose(requestDto.analysisPurposeId());
        validateDataSourceUseCase.validateDataSource(requestDto.dataSourceId());
        validateAuthorLevelUseCase.validateAuthorLevel(requestDto.authorLevelId());

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
                requestDto.dataIds()
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
        projectIndexingPort.index(ProjectSearchDocument.from(saveProject, username));

        log.info("프로젝트 업로드 완료 - userId: {}, title: {}", userId, requestDto.title());
    }
}
