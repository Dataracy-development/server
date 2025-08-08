package com.dataracy.modules.auth.adapter.web.api;

import com.dataracy.modules.auth.adapter.web.request.RefreshTokenWebRequest;
import com.dataracy.modules.auth.adapter.web.request.SelfLoginWebRequest;
import com.dataracy.modules.auth.adapter.web.response.ReIssueTokenWebResponse;
import com.dataracy.modules.auth.adapter.web.response.RefreshTokenWebResponse;
import com.dataracy.modules.common.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Auth - Dev", description = "인증 관련 개발용 API")
@RequestMapping("/api/v1/auth")
public interface AuthDevApi {
    /**
     * 개발 환경에서 이메일과 비밀번호를 사용하여 자체 로그인을 수행합니다.
     *
     * @param webRequest 로그인에 필요한 이메일과 비밀번호 정보를 포함한 요청 객체
     * @return 로그인 성공 시 리프레시 토큰 정보를 포함한 성공 응답
     */
    @Operation(
            summary = "개발용 자체 로그인",
            description = "개발용 자체 로그인(email, password)을 통해 로그인합니다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "자체로그인 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "잘못된 아이디 또는 비밀번호", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "500", description = "리프레시 토큰 발급 실패", useReturnTypeSchema = true)
    })
    @PostMapping(value = "/dev/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse<RefreshTokenWebResponse>> loginDev(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "로그인 정보 (이메일, 비밀번호)",
                    content = @Content(schema = @Schema(implementation = SelfLoginWebRequest.class))
            )
            @Validated @RequestBody
            SelfLoginWebRequest webRequest
    );

    @Operation(
            summary = "개발용 토큰 재발급",
            description = "개발용 쿠키의 리프레시 토큰을 통해 토큰 재발급을 시행한다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "리프레시 토큰 검증 실패", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "500", description = "토큰 발급 실패", useReturnTypeSchema = true)
    })
    @PostMapping(value = "/dev/token/re-issue")
    ResponseEntity<SuccessResponse<ReIssueTokenWebResponse>> reIssueTokenDev(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "리프레시 토큰",
                    content = @Content(schema = @Schema(implementation = RefreshTokenWebRequest.class))
            )
            @Validated @RequestBody
            RefreshTokenWebRequest webRequest
    );
}
