package com.dataracy.modules.project.application.mapper.command;

import com.dataracy.modules.project.application.dto.request.command.UploadProjectRequest;
import com.dataracy.modules.project.domain.model.Project;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 도메인 요청 DTO -> 도메인 모델
 */
@Component
public class UploadedProjectDtoMapper {
    public Project toDomain(
            UploadProjectRequest requestDto,
            Long userId,
            Long parentProjectId,
            String defaultImageUrl
    ) {
        return Project.of(
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
    }
}
