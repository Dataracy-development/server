package com.dataracy.modules.user.application.mapper.external;

import com.dataracy.modules.dataset.application.dto.response.read.UserDataResponse;
import com.dataracy.modules.project.application.dto.response.read.UserProjectResponse;
import com.dataracy.modules.user.application.dto.response.support.OtherUserDataResponse;
import com.dataracy.modules.user.application.dto.response.support.OtherUserProjectResponse;
import org.springframework.stereotype.Component;

@Component
public class OtherUserInfoMapper {

    public OtherUserProjectResponse toOtherUserProject(UserProjectResponse source) {
        return new OtherUserProjectResponse(
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

    public OtherUserDataResponse toOtherUserData(UserDataResponse source) {
        return new OtherUserDataResponse(
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
