package com.dataracy.modules.project.application.service.command;

import com.dataracy.modules.common.util.FileUtil;
import com.dataracy.modules.filestorage.application.port.in.FileUploadUseCase;
import com.dataracy.modules.filestorage.support.util.S3KeyGeneratorUtil;
import com.dataracy.modules.project.application.dto.request.ProjectUploadRequest;
import com.dataracy.modules.project.application.port.in.ProjectUploadUseCase;
import com.dataracy.modules.project.application.port.out.ProjectRepositoryPort;
import com.dataracy.modules.project.domain.model.Project;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectCommandService implements ProjectUploadUseCase {

    private final ProjectRepositoryPort projectRepositoryPort;

    private final FileUploadUseCase fileUploadUseCase;

    /**
     * 주어진 사용자 ID와 프로젝트 업로드 요청 정보를 기반으로 새로운 프로젝트를 생성하여 저장합니다.
     *
     * @param userId 프로젝트를 업로드하는 사용자의 ID
     * @param imageFile 썸네일 이미지 파일
     * @param requestDto 프로젝트 생성에 필요한 정보가 담긴 요청 객체
     */
    @Override
    @Transactional
    public void upload(Long userId, MultipartFile imageFile, ProjectUploadRequest requestDto) {
        log.info("프로젝트 업로드 시작 - userId: {}, title: {}", userId, requestDto.title());

        // 유효성 검사
        FileUtil.validateImageFile(imageFile);

        // 부모 프로젝트 조회
        Project parentProject = projectRepositoryPort.findProjectById(requestDto.parentProjectId());

        // 프로젝트 업로드 DB 저장
        Project project = Project.builder()
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
        Long projectId = projectRepositoryPort.saveProject(project);

        // DB 저장 성공 후 파일 업로드 시도, 외부 서비스로 트랜잭션의 영향을 받지 않는다.
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String key = S3KeyGeneratorUtil.generateKey("project", projectId, imageFile.getOriginalFilename());
                String imageUrl = fileUploadUseCase.uploadFile(key, imageFile);
                log.info("프로젝트 썸네일 업로드 성공 - url={}", imageUrl);

                // 업데이트
                project.updateFile(imageUrl);
                projectRepositoryPort.updateFile(project.getId(), imageUrl);
            } catch (Exception e) {
                log.error("프로젝트 썸네일 업로드 실패. 프로젝트 ID={}, 에러={}", project.getId(), e.getMessage());
                throw new RuntimeException("파일 업로드 실패", e); // rollback 유도
            }
        }
        log.info("프로젝트 업로드 완료 - userId: {}, title: {}", userId, requestDto.title());

    }
}
