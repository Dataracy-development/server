package com.dataracy.modules.user.adapter.web.api.read;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.adapter.web.mapper.read.UserReadWebMapper;
import com.dataracy.modules.user.adapter.web.response.read.GetUserInfoWebResponse;
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
     * 유저의 회원 정보를 조회한다.
     *
     * @return 회원 정보 조회 성공 시 200 OK 상태와 유저 정보 반환
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
}
