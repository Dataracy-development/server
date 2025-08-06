package com.dataracy.modules.project.adapter.web.api.command;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.support.annotation.AuthorizationProjectEdit;
import com.dataracy.modules.project.adapter.web.mapper.command.ProjectCommandWebMapper;
import com.dataracy.modules.project.adapter.web.request.command.ModifyProjectWebRequest;
import com.dataracy.modules.project.adapter.web.request.command.UploadProjectWebRequest;
import com.dataracy.modules.project.application.dto.request.command.ModifyProjectRequest;
import com.dataracy.modules.project.application.dto.request.command.UploadProjectRequest;
import com.dataracy.modules.project.application.port.in.command.content.DeleteProjectUseCase;
import com.dataracy.modules.project.application.port.in.command.content.ModifyProjectUseCase;
import com.dataracy.modules.project.application.port.in.command.content.RestoreProjectUseCase;
import com.dataracy.modules.project.application.port.in.command.content.UploadProjectUseCase;
import com.dataracy.modules.project.domain.status.ProjectSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
public class ProjectCommandController implements ProjectCommandApi {
    private final ProjectCommandWebMapper projectCommandWebMapper;

    private final UploadProjectUseCase uploadProjectUseCase;
    private final ModifyProjectUseCase modifyProjectUseCase;
    private final DeleteProjectUseCase deleteProjectUseCase;
    private final RestoreProjectUseCase restoreProjectUseCase;

    /**
     * 새로운 프로젝트를 생성한다.
     *
     * 사용자의 ID, 프로젝트 썸네일 이미지 파일, 프로젝트 정보가 포함된 요청 객체를 받아 프로젝트를 업로드한다.
     *
     * @param userId 프로젝트를 업로드하는 사용자의 ID
     * @param file 프로젝트 썸네일 이미지 파일
     * @param webRequest 업로드할 프로젝트 정보가 담긴 요청 객체
     * @return 프로젝트 생성 성공 시 201 Created와 성공 상태를 포함한 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<Void>> uploadProject(
            Long userId,
            MultipartFile file,
            UploadProjectWebRequest webRequest
    ) {
        Instant startTime = LoggerFactory.api().logRequest("[UploadProject] 프로젝트 업로드 API 요청 시작");

        try {
            UploadProjectRequest requestDto = projectCommandWebMapper.toApplicationDto(webRequest);
            uploadProjectUseCase.uploadProject(userId, file, requestDto);
        } finally {
            LoggerFactory.api().logResponse("[UploadProject] 프로젝트 업로드 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of(ProjectSuccessStatus.CREATED_PROJECT));
    }

    /**
     * 프로젝트 ID에 해당하는 프로젝트 정보를 수정한다.
     *
     * @param projectId 수정할 프로젝트의 ID
     * @param file 프로젝트 썸네일 파일 (선택 사항)
     * @param webRequest 프로젝트 수정 요청 데이터
     * @return 프로젝트 수정 성공 시 성공 상태를 포함한 응답
     */
    @Override
    @AuthorizationProjectEdit
    public ResponseEntity<SuccessResponse<Void>> modifyProject(Long projectId, MultipartFile file, ModifyProjectWebRequest webRequest) {
        Instant startTime = LoggerFactory.api().logRequest("[ModifyProject] 프로젝트 수정 API 요청 시작");

        try {
            ModifyProjectRequest requestDto = projectCommandWebMapper.toApplicationDto(webRequest);
            modifyProjectUseCase.modifyProject(projectId, file, requestDto);
        } finally {
            LoggerFactory.api().logResponse("[ModifyProject] 프로젝트 수정 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ProjectSuccessStatus.MODIFY_PROJECT));
    }

    /**
     * 지정한 프로젝트를 삭제 상태로 변경합니다.
     *
     * @param projectId 삭제할 프로젝트의 ID
     * @return 프로젝트 삭제 성공 시 성공 상태를 포함한 응답을 반환합니다.
     */
    @Override
    @AuthorizationProjectEdit
    public ResponseEntity<SuccessResponse<Void>> deleteProject(Long projectId) {
        Instant startTime = LoggerFactory.api().logRequest("[DeleteProject] 프로젝트 삭제 API 요청 시작");

        try {
            deleteProjectUseCase.deleteProject(projectId);
        } finally {
            LoggerFactory.api().logResponse("[DeleteProject] 프로젝트 삭제 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ProjectSuccessStatus.DELETE_PROJECT));
    }

    /**
     * 삭제된 프로젝트를 복구하는 REST API 엔드포인트입니다.
     *
     * @param projectId 복구할 프로젝트의 고유 식별자
     * @return 프로젝트 복구 성공 상태가 포함된 HTTP 200 OK 응답
     */
    @Override
    @AuthorizationProjectEdit(restore = true)
    public ResponseEntity<SuccessResponse<Void>> restoreProject(Long projectId) {
        Instant startTime = LoggerFactory.api().logRequest("[RestoreProject] 삭제된 프로젝트 복원 API 요청 시작");

        try {
            restoreProjectUseCase.restoreProject(projectId);
        } finally {
            LoggerFactory.api().logResponse("[RestoreProject] 삭제된 프로젝트 복원 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ProjectSuccessStatus.RESTORE_PROJECT));
    }
}
