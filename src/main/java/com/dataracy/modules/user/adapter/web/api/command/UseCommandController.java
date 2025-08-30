package com.dataracy.modules.user.adapter.web.api.command;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.adapter.web.mapper.command.UserCommandWebMapper;
import com.dataracy.modules.user.adapter.web.request.command.ModifyUserInfoWebRequest;
import com.dataracy.modules.user.application.dto.request.command.ModifyUserInfoRequest;
import com.dataracy.modules.user.application.port.in.command.command.ModifyUserInfoUseCase;
import com.dataracy.modules.user.domain.status.UserSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
public class UseCommandController implements UserCommandApi {
    private final UserCommandWebMapper userCommandWebMapper;

    private final ModifyUserInfoUseCase modifyUserInfoUseCase;

    @Override
    public ResponseEntity<SuccessResponse<Void>> modifyUserInfo(
            Long userId,
            MultipartFile profileImageFile,
            ModifyUserInfoWebRequest webRequest
    ) {
        Instant startTime = LoggerFactory.api().logRequest("[ModifyUserInfo] 회원정보 수정 API 요청 시작");

        try {
            ModifyUserInfoRequest requestDto = userCommandWebMapper.toApplicationDto(webRequest);
            modifyUserInfoUseCase.modifyUserInfo(userId, profileImageFile, requestDto);
        } finally {
            LoggerFactory.api().logResponse("[ModifyUserInfo] 회원정보 수정 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(UserSuccessStatus.OK_MODIFY_USER_INFO));
    }
}
