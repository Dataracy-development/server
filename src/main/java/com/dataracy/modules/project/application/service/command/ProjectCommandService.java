package com.dataracy.modules.project.application.service.command;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.status.CommonErrorStatus;
import com.dataracy.modules.common.util.FileUtil;
import com.dataracy.modules.dataset.application.port.in.validate.ValidateDataUseCase;
import com.dataracy.modules.filestorage.application.port.in.FileCommandUseCase;
import com.dataracy.modules.filestorage.support.util.S3KeyGeneratorUtil;
import com.dataracy.modules.project.application.dto.document.ProjectSearchDocument;
import com.dataracy.modules.project.application.dto.request.command.ModifyProjectRequest;
import com.dataracy.modules.project.application.dto.request.command.UploadProjectRequest;
import com.dataracy.modules.project.application.dto.response.command.UploadProjectResponse;
import com.dataracy.modules.project.application.mapper.command.CreateProjectDtoMapper;
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
import com.dataracy.modules.user.application.port.in.query.extractor.FindUserThumbnailUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUsernameUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectCommandService implements UploadProjectUseCase, ModifyProjectUseCase {
  private final CreateProjectDtoMapper createProjectDtoMapper;

  private final IndexProjectPort indexProjectPort;

  private final CreateProjectPort createProjectPort;
  private final UpdateProjectFilePort updateProjectFilePort;
  private final UpdateProjectPort updateProjectPort;
  private final DeleteProjectDataPort deleteProjectDataPort;

  private final CheckProjectExistsByIdPort checkProjectExistsByIdPort;
  private final FindProjectPort findProjectPort;
  private final ExtractProjectOwnerPort extractProjectOwnerPort;

  private final FindUsernameUseCase findUsernameUseCase;
  private final FindUserThumbnailUseCase findUserThumbnailUseCase;
  private final FileCommandUseCase fileCommandUseCase;

  // Use Case 상수 정의
  private static final String UPLOAD_PROJECT_USE_CASE = "UploadProjectUseCase";
  private static final String MODIFY_PROJECT_USE_CASE = "ModifyProjectUseCase";
  private static final String UPLOAD_PROJECT_REQUEST_USE_CASE = "UploadProjectRequest";

  // 메시지 상수 정의
  private static final String PARENT_PROJECT_NOT_FOUND_MESSAGE =
      "해당 부모 프로젝트가 존재하지 않습니다. parentProjectId=";
  private static final String PROJECT_NOT_FOUND_MESSAGE = "해당 프로젝트가 존재하지 않습니다. projectId=";

  private final GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;
  private final GetAnalysisPurposeLabelFromIdUseCase getAnalysisPurposeLabelFromIdUseCase;
  private final GetDataSourceLabelFromIdUseCase getDataSourceLabelFromIdUseCase;
  private final GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;
  private final ValidateDataUseCase validateDataUseCase;

  /**
   * 새 프로젝트를 생성하고(검증 포함) 썸네일을 업로드한 뒤 검색 색인까지 처리합니다.
   *
   * <p>요청된 라벨(topic, analysis purpose, data source, author level)과 데이터 ID를 검증하고, 부모 프로젝트 존재 여부를 확인한
   * 뒤 프로젝트를 저장합니다. 저장 완료 후 제공된 썸네일을 외부 저장소에 업로드하고 업로드된 썸네일 URL을 프로젝트에 반영한 다음, 사용자명 및 사용자 썸네일 URL과
   * 함께 검색 시스템에 색인합니다.
   *
   * @return 생성된 프로젝트 ID를 담은 UploadProjectResponse
   * @throws ProjectException 부모 프로젝트가 존재하지 않을 경우 발생합니다.
   * @throws RuntimeException 썸네일 업로드 등 파일 처리 중 오류가 발생하여 트랜잭션 롤백이 필요할 때 발생합니다.
   */
  @Override
  @Transactional
  public UploadProjectResponse uploadProject(
      Long userId, MultipartFile thumbnailFile, UploadProjectRequest requestDto) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(UPLOAD_PROJECT_USE_CASE, "프로젝트 업로드 서비스 시작 title=" + requestDto.title());

    // 요청 DTO의 유효성을 검사한다.
    ValidatedProjectInfo validatedProjectInfo =
        getValidatedProjectInfo(
            thumbnailFile,
            requestDto.topicId(),
            requestDto.analysisPurposeId(),
            requestDto.dataSourceId(),
            requestDto.authorLevelId(),
            requestDto.dataIds());

    // 부모 프로젝트 유효성 체크
    if (requestDto.parentProjectId() != null
        && !checkProjectExistsByIdPort.checkProjectExistsById(requestDto.parentProjectId())) {
      LoggerFactory.service()
          .logWarning(
              UPLOAD_PROJECT_USE_CASE,
              PARENT_PROJECT_NOT_FOUND_MESSAGE + requestDto.parentProjectId());
      throw new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT);
    }

    // 프로젝트 도메인 변환 및 DB 저장
    Project project =
        createProjectDtoMapper.toDomain(requestDto, userId, requestDto.parentProjectId());
    Project savedProject = createProjectPort.saveProject(project);

    // DB 저장 성공 후 파일 업로드 시도
    fileUpload(savedProject.getId(), thumbnailFile);

    // 검색을 위해 elasticSearch에 프로젝트를 등록한다 .
    String username = findUsernameUseCase.findUsernameById(userId);
    String userProfileImageUrl = findUserThumbnailUseCase.findUserThumbnailById(userId);
    indexProjectPort.index(
        ProjectSearchDocument.from(
            savedProject,
            validatedProjectInfo.topicLabel(),
            validatedProjectInfo.analysisPurposeLabel(),
            validatedProjectInfo.dataSourceLabel(),
            validatedProjectInfo.authorLevelLabel(),
            username,
            userProfileImageUrl));

    LoggerFactory.service()
        .logSuccess(
            UPLOAD_PROJECT_USE_CASE, "프로젝트 업로드 서비스 종료 title=" + requestDto.title(), startTime);
    return new UploadProjectResponse(savedProject.getId());
  }

  /**
   * 기존 프로젝트를 수정하고 변경 내용을 검색 인덱스에 반영합니다.
   *
   * <p>프로젝트의 메타정보와 연관 데이터 연결을 업데이트하고, 필요 시 새로운 썸네일을 업로드한 뒤 변경된 프로젝트를 검색 시스템에 재색인합니다. 썸네일 업로드는 DB 저장
   * 이후 외부 파일 스토리지로 수행되며, 업로드 실패 시 트랜잭션 롤백을 위해 RuntimeException이 발생합니다.
   *
   * @param projectId 수정할 프로젝트의 식별자
   * @param thumbnailFile 새로 업로드할 썸네일 이미지 파일 (null 또는 비어있을 수 있음)
   * @param requestDto 프로젝트 수정에 필요한 입력 데이터 (메타정보와 연관 데이터 ID 목록 포함)
   * @throws ProjectException 대상 프로젝트 또는 지정된 부모 프로젝트가 존재하지 않는 경우 발생
   * @throws RuntimeException 썸네일 파일 업로드 실패 시 트랜잭션 롤백을 위해 발생
   */
  @Override
  @Transactional
  public void modifyProject(
      Long projectId, MultipartFile thumbnailFile, ModifyProjectRequest requestDto) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(MODIFY_PROJECT_USE_CASE, "프로젝트 수정 서비스 시작 projectId=" + projectId);

    // 해당 id가 존재하는지 내부 유효성 검사 및 라벨 값 반환 (elasticsearch 저장을 위해 유효성 검사 뿐만 아니라 label도 반환한다.)
    ValidatedProjectInfo validatedProjectInfo =
        getValidatedProjectInfo(
            thumbnailFile,
            requestDto.topicId(),
            requestDto.analysisPurposeId(),
            requestDto.dataSourceId(),
            requestDto.authorLevelId(),
            requestDto.dataIds());
    if (requestDto.parentProjectId() != null
        && !checkProjectExistsByIdPort.checkProjectExistsById(requestDto.parentProjectId())) {
      LoggerFactory.service()
          .logWarning(
              MODIFY_PROJECT_USE_CASE,
              PARENT_PROJECT_NOT_FOUND_MESSAGE + requestDto.parentProjectId());
      throw new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT);
    }

    // 기존 연결과 새로운 연결 비교 후 필요한 것만 추가/삭제
    Set<Long> existingDataIds = extractProjectOwnerPort.findDataIdsByProjectId(projectId);
    Set<Long> newDataIds = new HashSet<>(requestDto.dataIds());

    // 삭제할 연결
    Set<Long> toDelete =
        existingDataIds.stream().filter(id -> !newDataIds.contains(id)).collect(Collectors.toSet());
    if (!toDelete.isEmpty()) {
      deleteProjectDataPort.deleteByProjectIdAndDataIdIn(projectId, toDelete);
    }

    // 추가할 연결
    Set<Long> toAdd =
        newDataIds.stream().filter(id -> !existingDataIds.contains(id)).collect(Collectors.toSet());

    // 프로젝트 수정
    updateProjectPort.modifyProject(projectId, requestDto, toAdd);

    // DB 저장 성공 후 파일 업로드 시도, 외부 서비스로 트랜잭션의 영향을 받지 않는다.
    fileUpload(projectId, thumbnailFile);

    // 수정된 프로젝트 도메인 다시 조회
    Project updatedProject =
        findProjectPort
            .findProjectById(projectId)
            .orElseThrow(
                () -> {
                  LoggerFactory.service()
                      .logWarning(MODIFY_PROJECT_USE_CASE, PROJECT_NOT_FOUND_MESSAGE + projectId);
                  return new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT);
                });

    String username = findUsernameUseCase.findUsernameById(updatedProject.getUserId());
    String userProfileImageUrl =
        findUserThumbnailUseCase.findUserThumbnailById(updatedProject.getUserId());
    // Elasticsearch 업데이트
    indexProjectPort.index(
        ProjectSearchDocument.from(
            updatedProject,
            validatedProjectInfo.topicLabel(),
            validatedProjectInfo.analysisPurposeLabel(),
            validatedProjectInfo.dataSourceLabel(),
            validatedProjectInfo.authorLevelLabel(),
            username,
            userProfileImageUrl));
    LoggerFactory.service()
        .logSuccess(MODIFY_PROJECT_USE_CASE, "프로젝트 수정 서비스 종료 projectId=" + projectId, startTime);
  }

  /**
   * 프로젝트 썸네일 이미지를 외부 스토리지에 업로드하고, 해당 파일의 URL로 프로젝트의 썸네일 정보를 갱신합니다.
   *
   * <p>파일이 null이 아니고 비어 있지 않은 경우에만 동작하며, 업로드 중 예외가 발생하면 트랜잭션 롤백을 위해 RuntimeException을 발생시킵니다.
   *
   * @param projectId 썸네일을 업로드할 프로젝트의 ID
   * @param file 업로드할 썸네일 이미지 파일
   */
  private void fileUpload(Long projectId, MultipartFile file) {
    if (file != null && !file.isEmpty()) {
      try {
        String key =
            S3KeyGeneratorUtil.generateKey("project", projectId, file.getOriginalFilename());
        String fileUrl = fileCommandUseCase.uploadFile(key, file);
        updateProjectFilePort.updateThumbnailFile(projectId, fileUrl);
      } catch (Exception e) {
        LoggerFactory.service()
            .logException(
                UPLOAD_PROJECT_REQUEST_USE_CASE, "프로젝트 파일 업로드 실패. projectId=" + projectId, e);
        throw new CommonException(CommonErrorStatus.FILE_UPLOAD_FAILURE);
      }
    }
  }

  /**
   * 프로젝트 관련 ID와 썸네일 이미지 파일의 유효성을 검사하고, 각 항목의 라벨 정보를 포함한 ValidatedProjectInfo 객체를 반환합니다.
   *
   * @param thumbnailFile 프로젝트 썸네일 이미지 파일
   * @param topicId 주제 ID
   * @param analysisPurposeId 분석 목적 ID
   * @param datasourceId 데이터 소스 ID
   * @param authorLevelId 작성자 레벨 ID
   * @param dataIds 데이터셋 ID 목록
   * @return 각 항목의 라벨 정보를 포함한 ValidatedProjectInfo 객체
   * @throws IllegalArgumentException 유효하지 않은 ID나 이미지 파일이 전달된 경우 발생합니다.
   */
  private ValidatedProjectInfo getValidatedProjectInfo(
      MultipartFile thumbnailFile,
      Long topicId,
      Long analysisPurposeId,
      Long datasourceId,
      Long authorLevelId,
      List<Long> dataIds) {
    // 해당 id가 존재하는지 내부 유효성 검사 및 라벨 값 반환 (elasticsearch 저장 또는 업데이트를 위해 유효성 검사 뿐만 아니라 label도 반환한다.)
    String topicLabel = getTopicLabelFromIdUseCase.getLabelById(topicId);
    String analysisPurposeLabel =
        getAnalysisPurposeLabelFromIdUseCase.getLabelById(analysisPurposeId);
    String dataSourceLabel = getDataSourceLabelFromIdUseCase.getLabelById(datasourceId);
    String authorLevelLabel = getAuthorLevelLabelFromIdUseCase.getLabelById(authorLevelId);

    // 데이터셋 id를 통해 데이터셋 존재 유효성 검사를 시행한다.
    if (dataIds != null) {
      dataIds.forEach(validateDataUseCase::validateData);
    }

    // 파일 유효성 검사
    FileUtil.validateImageFile(thumbnailFile);

    return new ValidatedProjectInfo(
        topicLabel, analysisPurposeLabel, dataSourceLabel, authorLevelLabel);
  }
}
