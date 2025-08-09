package com.dataracy.modules.project.application.mapper.command;

import com.dataracy.modules.project.application.dto.request.command.UploadProjectRequest;
import com.dataracy.modules.project.domain.model.Project;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 도메인 요청 DTO -> 도메인 모델
 */
@Component
public class CreateProjectDtoMapper {

    @Value("${default.project.image-url}")
    private String defaultProjectImageUrl;

    /**
     * UploadProjectRequest DTO와 추가 정보를 바탕으로 새로운 Project 도메인 객체를 생성합니다.
     *
     * 프로젝트 ID는 null로 설정되어 신규 엔티티임을 나타내며, 프로젝트 이미지 URL은 기본값이 사용됩니다. 댓글, 좋아요, 조회수는 0으로 초기화되고, 삭제 여부는 false, 자식 프로젝트 리스트는 비어 있습니다.
     *
     * @param requestDto 업로드할 프로젝트의 정보를 담은 DTO
     * @param userId 프로젝트를 생성하는 사용자 ID
     * @param parentProjectId 상위 프로젝트 ID (없을 경우 null)
     * @return 생성된 Project 도메인 객체
     */
    public Project toDomain(
            UploadProjectRequest requestDto,
            Long userId,
            Long parentProjectId
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
                defaultProjectImageUrl,
                requestDto.dataIds(),
                null,
                0L,   // commentCount 초기값
                0L,   // likeCount 초기값
                0L,   // viewCount 초기값
                false, // isDeleted 초기값
                List.of() // 자식 프로젝트 리스트 초기값
        );
    }
}
