package com.dataracy.modules.user.adapter.web.mapper.command;

import com.dataracy.modules.user.adapter.web.request.command.ModifyUserInfoWebRequest;
import com.dataracy.modules.user.application.dto.request.command.ModifyUserInfoRequest;
import org.springframework.stereotype.Component;

/**
 * 유저 command 웹 DTO와 애플리케이션 DTO를 변환하는 매퍼
 */
@Component
public class UserCommandWebMapper {
    public ModifyUserInfoRequest toApplicationDto(ModifyUserInfoWebRequest webRequest) {
        return new ModifyUserInfoRequest(
                webRequest.nickname(),
                webRequest.authorLevelId(),
                webRequest.occupationId(),
                webRequest.topicIds(),
                webRequest.visitSourceId(),
                webRequest.introductionText()
        );
    }
}
