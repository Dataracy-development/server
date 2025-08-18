package com.dataracy.modules.user.adapter.web.mapper.read;

import com.dataracy.modules.user.adapter.web.response.read.GetUserInfoWebResponse;
import com.dataracy.modules.user.application.dto.response.read.GetUserInfoResponse;
import org.springframework.stereotype.Component;

/**
 * 유저 조회 웹 DTO와 애플리케이션 DTO를 변환하는 매퍼
 */
@Component
public class UserReadWebMapper {
    /**
     * 유저 정보 조회 웹 응답 DTO를 애플리케이션 응답 DTO 변경
     *
     * @param responseDto 유저 정보 조회 애플리케이션 응답 DTO
     * @return 유저 정보 조회 웹 응답 DTO
     */
    public GetUserInfoWebResponse toWebDto(GetUserInfoResponse responseDto) {
        return new GetUserInfoWebResponse(
                responseDto.id(),
                responseDto.role(),
                responseDto.email(),
                responseDto.nickname(),
                responseDto.authorLevelLabel(),
                responseDto.occupationLabel(),
                responseDto.topicLabels(),
                responseDto.visitSourceLabel(),
                responseDto.profileImageUrl(),
                responseDto.introductionText()
        );
    }
}
