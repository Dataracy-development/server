package com.dataracy.modules.user.adapter.web.api.read;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.adapter.web.mapper.read.UserReadWebMapper;
import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserInfoWebResponse;
import com.dataracy.modules.user.adapter.web.response.read.GetUserInfoWebResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserInfoResponse;
import com.dataracy.modules.user.application.dto.response.read.GetUserInfoResponse;
import com.dataracy.modules.user.application.port.in.query.extractor.GetUserInfoUseCase;
import com.dataracy.modules.user.domain.status.UserSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
public class UserReadController implements UserReadApi {
    private final UserReadWebMapper userReadWebMapper;

    private final GetUserInfoUseCase getUserInfoUseCase;

    /**
         * 유저의 회원 정보를 조회하여 웹 응답 DTO로 반환한다.
         *
         * <p>내부적으로 GetUserInfoUseCase를 호출해 도메인 응답을 얻고, UserReadWebMapper로 웹 DTO로 변환한 후
         * HTTP 200 응답의 SuccessResponse에 담아 반환한다.</p>
         *
         * @param userId 조회할 유저의 식별자
         * @return HTTP 200 OK와 UserSuccessStatus.OK_GET_USER_INFO 상태 및 조회된 GetUserInfoWebResponse를 포함한 SuccessResponse
         */
    @Override
    public ResponseEntity<SuccessResponse<GetUserInfoWebResponse>> getUserInfo(
            Long userId
    ) {
        Instant startTime = LoggerFactory.api().logRequest("[GetUserInfo] 유저 회원정보 조회 API 요청 시작");
        GetUserInfoWebResponse webResponse;

        try {
            GetUserInfoResponse responseDto = getUserInfoUseCase.getUserInfo(userId);
            webResponse = userReadWebMapper.toWebDto(responseDto);
        } finally {
            LoggerFactory.api().logResponse("[GetUserInfo] 유저 회원정보 조회 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(UserSuccessStatus.OK_GET_USER_INFO, webResponse));
    }

    @Override
    public ResponseEntity<SuccessResponse<GetOtherUserInfoWebResponse>> getOtherUserInfo(Long userId) {
        Instant startTime = LoggerFactory.api().logRequest("[GetOtherUserInfo] 타인 회원정보 조회 API 요청 시작");
        GetOtherUserInfoWebResponse webResponse;

        try {
            GetOtherUserInfoResponse responseDto = getUserInfoUseCase.getOtherUserInfo(userId);
            webResponse = userReadWebMapper.toWebDto(responseDto);
        } finally {
            LoggerFactory.api().logResponse("[GetOtherUserInfo] 타인 회원정보 조회 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(UserSuccessStatus.OK_GET_OTHER_USER_INFO, webResponse));
    }
}
