package com.dataracy.modules.project.application.service.command;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.util.FileUtil;
import com.dataracy.modules.dataset.application.port.in.validate.ValidateDataUseCase;
import com.dataracy.modules.filestorage.application.port.in.FileUploadUseCase;
import com.dataracy.modules.filestorage.support.util.S3KeyGeneratorUtil;
import com.dataracy.modules.project.application.dto.document.ProjectSearchDocument;
import com.dataracy.modules.project.application.dto.request.command.ModifyProjectRequest;
import com.dataracy.modules.project.application.dto.request.command.UploadProjectRequest;
import com.dataracy.modules.project.application.mapper.command.UploadedProjectDtoMapper;
import com.dataracy.modules.project.application.port.in.command.content.ModifyProjectUseCase;
import com.dataracy.modules.project.application.port.in.command.content.UploadProjectUseCase;
import com.dataracy.modules.project.application.port.out.command.create.CreateProjectPort;
import com.dataracy.modules.project.application.port.out.command.delete.DeleteProjectDataPort;
import com.dataracy.modules.project.application.port.out.command.update.UpdateProjectFilePort;
import com.dataracy.modules.project.application.port.out.command.update.UpdateProjectPort;
import com.dataracy.modules.project.application.port.out.indexing.IndexProjectPort;
import com.dataracy.modules.project.application.port.out.query.extractor.ExtractProjectOwnerPort;
import com.dataracy.modules.project.application.port.out.query.read.FindProjectPort;
import com.dataracy.modules.project.application.port.out.query.validate.CheckProjectExistsByIdPort;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.project.domain.model.vo.ValidatedProjectInfo;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import com.dataracy.modules.reference.application.port.in.analysispurpose.GetAnalysisPurposeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUsernameUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectCommandService implements
        UploadProjectUseCase,
        ModifyProjectUseCase
{
    private final UploadedProjectDtoMapper uploadedProjectDtoMapper;

    private final IndexProjectPort indexProjectPort;

    private final CreateProjectPort createProjectPort;
    private final UpdateProjectFilePort updateProjectFilePort;
    private final UpdateProjectPort updateProjectPort;
    private final DeleteProjectDataPort deleteProjectDataPort;

    private final CheckProjectExistsByIdPort checkProjectExistsByIdPort;
    private final FindProjectPort findProjectPort;
    private final ExtractProjectOwnerPort extractProjectOwnerPort;

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
     * 사용자의 프로젝트 업로드 요청을 처리하여 새 프로젝트를 생성하고, 썸네일 이미지를 업로드한 뒤 프로젝트를 검색 시스템에 색인합니다.
     *
     * 프로젝트 생성 시 주제, 분석 목적, 데이터 소스, 저자 레벨 등 주요 라벨의 유효성을 검증하고, 데이터셋 및 부모 프로젝트의 존재 여부를 확인합니다. 이미지 파일이 제공되면 외부 저장소에 업로드 후 프로젝트 정보에 반영하며, 모든 정보가 저장된 후 검색 시스템에 색인하여 검색이 가능하도록 처리합니다.
     *
     * @throws ProjectException 부모 프로젝트가 존재하지 않을 경우 발생합니다.
     * @throws RuntimeException 파일 업로드 실패 시 트랜잭션 롤백을 위해 발생합니다.
     */
    @Override
    @Transactional
    public void uploadProject(Long userId, MultipartFile file, UploadProjectRequest requestDto) {
        Instant startTime = LoggerFactory.service().logStart("UploadProjectUseCase", "프로젝트 업로드 서비스 시작 title=" + requestDto.title());

        // 요청 DTO의 유효성을 검사한다.
        ValidatedProjectInfo validatedProjectInfo = getValidatedProjectInfo(
                file,
                requestDto.topicId(),
                requestDto.analysisPurposeId(),
                requestDto.dataSourceId(),
                requestDto.authorLevelId(),
                requestDto.dataIds()
        );

        // 부모 프로젝트 유효성 체크
        if (requestDto.parentProjectId() != null && !checkProjectExistsByIdPort.checkProjectExistsById(requestDto.parentProjectId())) {
                LoggerFactory.service().logWarning("UploadProjectUseCase", "해당 부모 프로젝트가 존재하지 않습니다. parentProjectId=" + requestDto.parentProjectId());
                throw new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT);
        }

        // 프로젝트 도메인 변환 및 DB 저장
        Project project = uploadedProjectDtoMapper.toDomain(
                requestDto,
                userId,
                requestDto.parentProjectId(),
                defaultImageUrl
        );
        Project savedProject = createProjectPort.saveProject(project);

        // DB 저장 성공 후 파일 업로드 시도
        fileUpload(savedProject.getId(), file);

        // 검색을 위해 elasticSearch에 프로젝트를 등록한다 .
        String username = findUsernameUseCase.findUsernameById(userId);
        indexProjectPort.index(ProjectSearchDocument.from(
                savedProject,
                validatedProjectInfo.topicLabel(),
                validatedProjectInfo.analysisPurposeLabel(),
                validatedProjectInfo.dataSourceLabel(),
                validatedProjectInfo.authorLevelLabel(),
                username
        ));

        LoggerFactory.service().logSuccess("UploadProjectUseCase", "프로젝트 업로드 서비스 종료 title=" + requestDto.title(), startTime);
    }

    /**
     * 프로젝트 정보를 수정하고, 필요 시 새로운 이미지 파일을 업로드한 뒤, 변경된 내용을 검색 인덱스에 반영합니다.
     *
     * @param projectId 수정할 프로젝트의 ID
     * @param file 새로 업로드할 이미지 파일 (선택 사항)
     * @param requestDto 프로젝트 수정 요청 데이터
     * @throws ProjectException 프로젝트 또는 부모 프로젝트가 존재하지 않을 경우 발생
     * @throws RuntimeException 파일 업로드 실패 시 트랜잭션 롤백을 위해 발생
     */
    @Override
    @Transactional
    public void modifyProject(Long projectId, MultipartFile file, ModifyProjectRequest requestDto) {
        Instant startTime = LoggerFactory.service().logStart("ModifyProjectUseCase", "프로젝트 수정 서비스 시작 projectId=" + projectId);

        // 해당 id가 존재하는지 내부 유효성 검사 및 라벨 값 반환 (elasticsearch 저장을 위해 유효성 검사 뿐만 아니라 label도 반환한다.)
        ValidatedProjectInfo validatedProjectInfo = getValidatedProjectInfo(
                file,
                requestDto.topicId(),
                requestDto.analysisPurposeId(),
                requestDto.dataSourceId(),
                requestDto.authorLevelId(),
                requestDto.dataIds()
        );
        if (requestDto.parentProjectId() != null && !checkProjectExistsByIdPort.checkProjectExistsById(requestDto.parentProjectId())) {
            LoggerFactory.service().logWarning("ModifyProjectUseCase", "해당 부모 프로젝트가 존재하지 않습니다. parentProjectId=" + requestDto.parentProjectId());
            throw new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT);
        }

        // 기존 연결과 새로운 연결 비교 후 필요한 것만 추가/삭제
        Set<Long> existingDataIds = extractProjectOwnerPort.findDataIdsByProjectId(projectId);
        Set<Long> newDataIds = new HashSet<>(requestDto.dataIds());

        // 삭제할 연결
        Set<Long> toDelete = existingDataIds.stream()
                .filter(id -> !newDataIds.contains(id))
                .collect(Collectors.toSet());
        if (!toDelete.isEmpty()) {
            deleteProjectDataPort.deleteByProjectIdAndDataIdIn(projectId, toDelete);
        }

        // 추가할 연결
        Set<Long> toAdd =  newDataIds.stream()
                .filter(id -> !existingDataIds.contains(id))
                .collect(Collectors.toSet());

        // 프로젝트 수정
        updateProjectPort.modifyProject(projectId, requestDto, toAdd);

        // DB 저장 성공 후 파일 업로드 시도, 외부 서비스로 트랜잭션의 영향을 받지 않는다.
        fileUpload(projectId, file);

        // 수정된 프로젝트 도메인 다시 조회
        Project updatedProject = findProjectPort.findProjectById(projectId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning("ModifyProjectUseCase", "해당 프로젝트가 존재하지 않습니다. projectId=" + projectId);
                    return new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT);
                });

        String username = findUsernameUseCase.findUsernameById(updatedProject.getUserId());
        // Elasticsearch 업데이트
        indexProjectPort.index(ProjectSearchDocument.from(
                updatedProject,
                validatedProjectInfo.topicLabel(),
                validatedProjectInfo.analysisPurposeLabel(),
                validatedProjectInfo.dataSourceLabel(),
                validatedProjectInfo.authorLevelLabel(),
                username
        ));
        LoggerFactory.service().logSuccess("ModifyProjectUseCase", "프로젝트 수정 서비스 종료 projectId=" + projectId, startTime);
    }

    /**
     * 프로젝트 이미지 파일을 외부 스토리지에 업로드하고, 해당 파일 URL로 프로젝트 정보를 업데이트합니다.
     *
     * 파일이 null이 아니고 비어 있지 않은 경우에만 동작하며, 업로드 실패 시 RuntimeException을 발생시켜 트랜잭션 롤백을 유도합니다.
     *
     * @param projectId 파일을 업로드할 프로젝트의 ID
     * @param file 업로드할 이미지 파일
     */
    private void fileUpload(Long projectId, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                String key = S3KeyGeneratorUtil.generateKey("project", projectId, file.getOriginalFilename());
                String fileUrl = fileUploadUseCase.uploadFile(key, file);
                updateProjectFilePort.updateFile(projectId, fileUrl);
            } catch (Exception e) {
                LoggerFactory.service().logException("UploadProjectRequest", "프로젝트 파일 업로드 실패. projectId=" + projectId, e);
                throw new RuntimeException("파일 업로드 실패", e);
            }
        }
    }

    /**
     * 프로젝트 관련 ID와 이미지 파일의 유효성을 검사하고, 각 항목의 라벨 정보를 포함한 ValidatedProjectInfo 객체를 반환합니다.
     *
     * @param file 프로젝트 이미지 파일
     * @param topicId 주제 ID
     * @param analysisPurposeId 분석 목적 ID
     * @param datasourceId 데이터 소스 ID
     * @param authorLevelId 작성자 레벨 ID
     * @param dataIds 데이터셋 ID 목록
     * @return 각 항목의 라벨 정보를 포함한 ValidatedProjectInfo 객체
     */
    private ValidatedProjectInfo getValidatedProjectInfo(
            MultipartFile file,
            Long topicId,
            Long analysisPurposeId,
            Long datasourceId,
            Long authorLevelId,
            List<Long> dataIds
    ) {
        // 해당 id가 존재하는지 내부 유효성 검사 및 라벨 값 반환 (elasticsearch 저장 또는 업데이트를 위해 유효성 검사 뿐만 아니라 label도 반환한다.)
        String topicLabel = getTopicLabelFromIdUseCase.getLabelById(topicId);
        String analysisPurposeLabel = getAnalysisPurposeLabelFromIdUseCase.getLabelById(analysisPurposeId);
        String dataSourceLabel = getDataSourceLabelFromIdUseCase.getLabelById(datasourceId);
        String authorLevelLabel = getAuthorLevelLabelFromIdUseCase.getLabelById(authorLevelId);

        // 데이터셋 id를 통해 데이터셋 존재 유효성 검사를 시행한다.
        if (dataIds != null) {
            dataIds.forEach(validateDataUseCase::validateData);
        }

        // 파일 유효성 검사
        FileUtil.validateImageFile(file);

        ValidatedProjectInfo validateProjectInfo = new ValidatedProjectInfo(
                topicLabel,
                analysisPurposeLabel,
                dataSourceLabel,
                authorLevelLabel
        );
        return validateProjectInfo;
    }
}
