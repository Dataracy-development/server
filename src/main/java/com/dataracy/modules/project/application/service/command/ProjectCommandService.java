package com.dataracy.modules.project.application.service.command;

import com.dataracy.modules.common.util.FileUtil;
import com.dataracy.modules.dataset.application.port.in.ValidateDataUseCase;
import com.dataracy.modules.filestorage.application.port.in.FileUploadUseCase;
import com.dataracy.modules.filestorage.support.util.S3KeyGeneratorUtil;
import com.dataracy.modules.project.adapter.elasticsearch.document.ProjectSearchDocument;
import com.dataracy.modules.project.application.dto.request.ProjectModifyRequest;
import com.dataracy.modules.project.application.dto.request.ProjectUploadRequest;
import com.dataracy.modules.project.application.port.elasticsearch.ProjectCommentUpdatePort;
import com.dataracy.modules.project.application.port.elasticsearch.ProjectDeletePort;
import com.dataracy.modules.project.application.port.elasticsearch.ProjectIndexingPort;
import com.dataracy.modules.project.application.port.elasticsearch.ProjectLikeUpdatePort;
import com.dataracy.modules.project.application.port.in.*;
import com.dataracy.modules.project.application.port.out.ProjectRepositoryPort;
import com.dataracy.modules.project.application.port.query.ProjectQueryRepositoryPort;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import com.dataracy.modules.reference.application.port.in.analysispurpose.GetAnalysisPurposeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.profile.FindUsernameUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectCommandService implements
        ProjectUploadUseCase,
        ProjectModifyUseCase,
        ProjectDeleteUseCase,
        ProjectRestoreUseCase,
        IncreaseCommentCountUseCase,
        DecreaseCommentCountUseCase,
        IncreaseLikeCountUseCase,
        DecreaseLikeCountUseCase
{
    private final ProjectRepositoryPort projectRepositoryPort;
    private final ProjectIndexingPort projectIndexingPort;
    private final ProjectDeletePort projectDeletePort;
    private final ProjectQueryRepositoryPort projectQueryRepositoryPort;
    private final ProjectCommentUpdatePort projectCommentUpdatePort;
    private final ProjectLikeUpdatePort projectLikeUpdatePort;

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
     * 사용자의 프로젝트 업로드 요청을 처리하여 새 프로젝트를 생성하고, 필요 시 썸네일 이미지를 업로드한 뒤 프로젝트를 검색 시스템에 색인합니다.
     *
     * 프로젝트 생성 시 주요 라벨(주제, 분석 목적, 데이터 소스, 저자 레벨) 값을 검증 및 조회하고, 데이터셋 ID의 존재 여부와 부모 프로젝트의 존재를 확인합니다. 이미지 파일이 제공되면 외부 저장소에 업로드 후 프로젝트 정보에 반영하며, 모든 정보가 저장된 후 검색 시스템에 색인하여 검색이 가능하도록 처리합니다.
     *
     * @throws ProjectException 부모 프로젝트가 존재하지 않을 경우 발생합니다.
     * @throws RuntimeException 파일 업로드 실패 시 트랜잭션 롤백을 위해 발생합니다.
     */
    @Override
    @Transactional
    public void upload(Long userId, MultipartFile file, ProjectUploadRequest requestDto) {
        log.info("프로젝트 업로드 시작 - userId: {}, title: {}", userId, requestDto.title());

        // 해당 id가 존재하는지 내부 유효성 검사 및 라벨 값 반환 (elasticsearch 저장을 위해 유효성 검사 뿐만 아니라 label도 반환한다.)
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
        Long parentProjectId = null;
        if (requestDto.parentProjectId() != null) {
            if (!projectRepositoryPort.existsProjectById(requestDto.parentProjectId())) {
                throw new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT);
            }
            parentProjectId = requestDto.parentProjectId();
        }

        // 프로젝트 업로드 DB 저장
        Project project = Project.of(
                null,
                requestDto.title(),
                requestDto.topicId(),
                userId,
                requestDto.analysisPurposeId(),
                requestDto.dataSourceId(),
                requestDto.authorLevelId(),
                requestDto.isContinue(),
                parentProjectId,
                requestDto.content(),
                defaultImageUrl,
                requestDto.dataIds(),
                null,
                0L,
                0L,
                0L,
                false,
                List.of()
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

    /**
     * 프로젝트의 정보를 수정하고, 필요 시 새로운 이미지 파일을 업로드한 뒤, 변경된 내용을 Elasticsearch에 반영합니다.
     *
     * @param projectId 수정할 프로젝트의 ID
     * @param file      새로 업로드할 이미지 파일 (선택적)
     * @param requestDto 프로젝트 수정 요청 데이터
     * @throws ProjectException 프로젝트가 존재하지 않을 경우 발생
     * @throws RuntimeException 파일 업로드에 실패할 경우 트랜잭션 롤백을 위해 발생
     */
    @Override
    @Transactional
    public void modify(Long projectId, MultipartFile file, ProjectModifyRequest requestDto) {
        log.info("프로젝트 수정 시작 - projectId: {}, title: {}", projectId, requestDto.title());

        // 해당 id가 존재하는지 내부 유효성 검사 및 라벨 값 반환 (elasticsearch 저장을 위해 유효성 검사 뿐만 아니라 label도 반환한다.)
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

        // 프로젝트 수정
        projectRepositoryPort.modify(projectId, requestDto);

        // DB 저장 성공 후 파일 업로드 시도, 외부 서비스로 트랜잭션의 영향을 받지 않는다.
        if (file != null && !file.isEmpty()) {
            try {
                String key = S3KeyGeneratorUtil.generateKey("project", projectId, file.getOriginalFilename());
                String fileUrl = fileUploadUseCase.uploadFile(key, file);
                log.info("새로운 프로젝트 파일 업로드 성공 - url={}", fileUrl);

                projectRepositoryPort.updateFile(projectId, fileUrl);
            } catch (Exception e) {
                log.error("새 프로젝트 파일 업로드 실패. 프로젝트 ID={}, 에러={}", projectId, e.getMessage());
                throw new RuntimeException("파일 업로드 실패", e); // rollback 유도
            }
        }

        // 수정된 프로젝트 도메인 다시 조회
        Project updatedProject = projectQueryRepositoryPort.findProjectById(projectId)
                .orElseThrow(() -> new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT));

        String username = findUsernameUseCase.findUsernameById(updatedProject.getUserId());

        // Elasticsearch 업데이트
        projectIndexingPort.index(ProjectSearchDocument.from(
                updatedProject,
                topicLabel,
                analysisPurposeLabel,
                dataSourceLabel,
                authorLevelLabel,
                username
        ));

        log.info("프로젝트 수정 완료 - projectId: {}, title: {}", projectId, updatedProject.getTitle());
    }

    /**
     * 프로젝트를 삭제 상태로 변경합니다.
     *
     * 데이터베이스에서 프로젝트를 삭제 처리하고, Elasticsearch 인덱스에서도 삭제 상태로 동기화합니다.
     *
     * @param projectId 삭제할 프로젝트의 ID
     */
    @Override
    @Transactional
    public void markAsDelete(Long projectId) {
        projectRepositoryPort.delete(projectId);
        projectDeletePort.markAsDeleted(projectId);
    }

    /**
     * 프로젝트를 복원 상태로 변경합니다.
     *
     * 데이터베이스에서 삭제된 프로젝트를 복원하고, Elasticsearch 인덱스에서도 복원 상태로 반영합니다.
     *
     * @param projectId 복원할 프로젝트의 ID
     */
    @Override
    @Transactional
    public void markAsRestore(Long projectId) {
        projectRepositoryPort.restore(projectId);
        projectDeletePort.markAsRestore(projectId);
    }

    /**
     * 프로젝트의 댓글 수를 1 증가시킵니다.
     *
     * 데이터베이스와 Elasticsearch 인덱스의 댓글 수를 동기화하여 모두 증가시킵니다.
     *
     * @param projectId 댓글 수를 증가시킬 프로젝트의 ID
     */
    @Override
    @Transactional
    public void increase(Long projectId) {
        projectRepositoryPort.increaseCommentCount(projectId);
        projectCommentUpdatePort.increaseCommentCount(projectId);
    }

    /**
     * 프로젝트의 댓글 수를 1 감소시키고, 변경된 댓글 수를 Elasticsearch 인덱스에 반영합니다.
     *
     * @param projectId 댓글 수를 감소시킬 프로젝트의 ID
     */
    @Override
    @Transactional
    public void decrease(Long projectId) {
        projectRepositoryPort.decreaseCommentCount(projectId);
        projectCommentUpdatePort.decreaseCommentCount(projectId);
    }

    /**
     * 프로젝트의 좋아요 수를 1 증가시킵니다.
     *
     * 데이터베이스와 Elasticsearch 인덱스의 좋아요 수를 모두 동기화합니다.
     *
     * @param projectId 좋아요 수를 증가시킬 프로젝트의 ID
     */
    @Override
    @Transactional
    public void increaseLike(Long projectId) {
        projectRepositoryPort.increaseLikeCount(projectId);
        projectLikeUpdatePort.increaseLikeCount(projectId);
    }

    /**
     * 프로젝트의 좋아요 수를 1 감소시킵니다.
     *
     * 데이터베이스와 Elasticsearch 인덱스의 좋아요 수를 모두 동기화합니다.
     *
     * @param projectId 좋아요 수를 감소시킬 프로젝트의 ID
     */
    @Override
    @Transactional
    public void decreaseLike(Long projectId) {
        projectRepositoryPort.decreaseLikeCount(projectId);
        projectLikeUpdatePort.decreaseLikeCount(projectId);
    }
}
