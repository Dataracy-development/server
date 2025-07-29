package com.dataracy.modules.auth.adapter.web.api;

import com.dataracy.modules.auth.adapter.web.mapper.AuthDevWebMapper;
import com.dataracy.modules.auth.adapter.web.request.RefreshTokenWebRequest;
import com.dataracy.modules.auth.adapter.web.request.SelfLoginWebRequest;
import com.dataracy.modules.auth.adapter.web.response.ReIssueTokenWebResponse;
import com.dataracy.modules.auth.adapter.web.response.RefreshTokenWebResponse;
import com.dataracy.modules.auth.application.dto.request.RefreshTokenRequest;
import com.dataracy.modules.auth.application.dto.request.SelfLoginRequest;
import com.dataracy.modules.auth.application.dto.response.ReIssueTokenResponse;
import com.dataracy.modules.auth.application.dto.response.RefreshTokenResponse;
import com.dataracy.modules.auth.application.port.in.auth.ReIssueTokenUseCase;
import com.dataracy.modules.auth.application.port.in.auth.SelfLoginUseCase;
import com.dataracy.modules.auth.domain.status.AuthSuccessStatus;
import com.dataracy.modules.common.dto.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthDevController implements AuthDevApi {
    private final AuthDevWebMapper authDevWebMapper;

    private final SelfLoginUseCase selfLoginUseCase;
    private final ReIssueTokenUseCase reIssueTokenUseCase;

    @Override
    public ResponseEntity<SuccessResponse<RefreshTokenWebResponse>> loginDev(SelfLoginWebRequest webRequest) {
        SelfLoginRequest requestDto = authDevWebMapper.toApplicationDto(webRequest);
        // 자체 로그인 진행
        RefreshTokenResponse responseDto = selfLoginUseCase.login(requestDto);
        RefreshTokenWebResponse webResponse = authDevWebMapper.toWebDto(responseDto);
        return ResponseEntity.ok(SuccessResponse.of(AuthSuccessStatus.OK_SELF_LOGIN, webResponse));
    }

    @Override
    public ResponseEntity<SuccessResponse<ReIssueTokenWebResponse>> reIssueTokenDev(RefreshTokenWebRequest webRequest) {
        RefreshTokenRequest requestDto = authDevWebMapper.toApplicationDto(webRequest);

        // 토큰 재발급 진행
        ReIssueTokenResponse responseDto = reIssueTokenUseCase.reIssueToken(requestDto.refreshToken());
        ReIssueTokenWebResponse webResponse = authDevWebMapper.toWebDto(responseDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(AuthSuccessStatus.OK_RE_ISSUE_TOKEN, webResponse));
    }
}
