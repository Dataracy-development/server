package com.dataracy.modules.auth.adapter.web.api;

import com.dataracy.modules.auth.adapter.web.request.SelfLoginWebRequest;
import com.dataracy.modules.common.dto.response.ErrorResponse;
import com.dataracy.modules.common.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 관련 API")
@RequestMapping("/api/v1/auth")
public interface AuthApi {
    /**
     * 자체로그인을 통해 로그인을 진행한다.
     *
     * @param webRequest 자체로그인 정보(email, password)
     * @param response HttpServletResponse
     * @return refreshToken 쿠키 저장
     */
    @Operation(
            summary = "자체 로그인",
            description = "자체 로그인(email, password)을 통해 로그인합니다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "자체로그인 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 아이디 또는 비밀번호",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "리프레시 토큰 발급 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse<Void>> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "로그인 정보 (이메일, 비밀번호)",
                    content = @Content(schema = @Schema(implementation = SelfLoginWebRequest.class))
            )
            @Validated @RequestBody
            SelfLoginWebRequest webRequest,

            HttpServletResponse response
    );

    /**
     * 리프레시 토큰을 통해 새로운 액세스 토큰과 리프레시 토큰을 발급받아 쿠키에 저장합니다.
     *
     * @param refreshToken 클라이언트로부터 전달받은 리프레시 토큰 (쿠키에서 추출)
     * @param response HttpServletResponse
     * @return 쿠키에 accessToken, accessTokenExpiration, refreshToken을 저장한다.
     */
    @Operation(
            summary = "토큰 재발급",
            description = "쿠키의 리프레시 토큰을 통해 토큰 재발급을 시행한다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "401", description = "리프레시 토큰 검증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "토큰 발급 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/token/re-issue")
    ResponseEntity<SuccessResponse<Void>> reIssueToken(
            @Parameter(
                    in = ParameterIn.COOKIE,
                    name = "refreshToken",
                    required = true,
                    description = "리프레시 토큰으로 토큰 재발급을 진행한다.",
                    schema = @Schema(type = "string")
            )
            @CookieValue(value = "refreshToken")
            String refreshToken,

            HttpServletResponse response
    );
}
