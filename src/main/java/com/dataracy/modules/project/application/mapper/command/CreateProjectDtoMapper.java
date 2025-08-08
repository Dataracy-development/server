package com.dataracy.modules.project.application.mapper.command;

import com.dataracy.modules.project.application.dto.request.command.UploadProjectRequest;
import com.dataracy.modules.project.domain.model.Project;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 도메인 요청 DTO -> 도메인 모델
 */
@Component
public class CreateProjectDtoMapper {
    /**
     * UploadProjectRequest DTO와 추가 정보를 기반으로 새로운 Project 도메인 객체를 생성합니다.
     *
     * @param requestDto 업로드할 프로젝트의 정보를 담은 DTO
     * @param userId 프로젝트를 생성하는 사용자 ID
     * @param parentProjectId 상위 프로젝트 ID (없을 경우 null)
     * @param defaultImageUrl 프로젝트의 기본 이미지 URL
     * @return 생성된 Project 도메인 객체
     */
    public Project toDomain(
            UploadProjectRequest requestDto,
            Long userId,
            Long parentProjectId,
            String defaultImageUrl
    ) {
        return Project.of(
                null, // id는 생성 시 null
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
                null, // thumbnailImageUrl은 기본값
                0L,   // commentCount 초기값
                0L,   // likeCount 초기값
                0L,   // viewCount 초기값
                false, // isDeleted 초기값
                List.of() // 자식 프로젝트 리스트 초기값
        );
    }
}
