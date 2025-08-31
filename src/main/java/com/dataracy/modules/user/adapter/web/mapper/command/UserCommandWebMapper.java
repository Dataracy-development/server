package com.dataracy.modules.user.adapter.web.mapper.command;

import com.dataracy.modules.user.adapter.web.request.command.ModifyUserInfoWebRequest;
import com.dataracy.modules.user.application.dto.request.command.ModifyUserInfoRequest;
import org.springframework.stereotype.Component;

/**
 * 유저 command 웹 DTO와 애플리케이션 DTO를 변환하는 매퍼
 */
@Component
public class UserCommandWebMapper {
    /**
     * 웹 계층의 ModifyUserInfoWebRequest를 애플리케이션 계층의 ModifyUserInfoRequest로 변환한다.
     *
     * <p>다음 필드를 일대일로 매핑한다: nickname, authorLevelId, occupationId, topicIds, visitSourceId, introductionText.</p>
     *
     * @param webRequest 변환할 웹용 요청 객체
     * @return 애플리케이션 계층에서 사용하는 ModifyUserInfoRequest 인스턴스
     */
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
