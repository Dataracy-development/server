package com.dataracy.modules.user.application.mapper.external;

import com.dataracy.modules.dataset.application.dto.response.read.UserDataResponse;
import com.dataracy.modules.project.application.dto.response.read.UserProjectResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserDataResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserProjectResponse;
import org.springframework.stereotype.Component;

@Component
public class OtherUserInfoMapper {

    public GetOtherUserProjectResponse toOtherUserProject(UserProjectResponse source) {
        return new GetOtherUserProjectResponse(
                source.id(),
                source.title(),
                source.content(),
                source.projectThumbnailUrl(),
                source.topicLabel(),
                source.authorLevelLabel(),
                source.commentCount(),
                source.likeCount(),
                source.viewCount(),
                source.createdAt()
        );
    }

    public GetOtherUserDataResponse toOtherUserData(UserDataResponse source) {
        return new GetOtherUserDataResponse(
                source.id(),
                source.title(),
                source.topicLabel(),
                source.dataTypeLabel(),
                source.startDate(),
                source.endDate(),
                source.dataThumbnailUrl(),
                source.downloadCount(),
                source.sizeBytes(),
                source.rowCount(),
                source.columnCount(),
                source.createdAt(),
                source.countConnectedProjects()
        );
    }
}
