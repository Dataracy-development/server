package com.dataracy.modules.user.adapter.web.api.command;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.support.annotation.CurrentUserId;
import com.dataracy.modules.user.adapter.web.request.command.ModifyUserInfoWebRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User - Command", description = "사용자 관련 API - 수정, 삭제")
@RequestMapping("/api/v1/user")
public interface UserCommandApi {
    /**
     * 회원 정보를 수정합니다.
     *
     * 요청 본문으로 회원 수정 정보(검증된 ModifyUserInfoWebRequest)와 선택적 프로필 이미지 파일을 받아
     * 회원 정보를 업데이트하고 성공 시 200 OK와 SuccessResponse<Void>를 반환합니다.
     *
     * @param profileImageFile 선택적 프로필 이미지 파일(Multipart). 없을 수 있습니다.
     * @param webRequest 회원 수정에 필요한 검증된 요청 데이터(ModifyUserInfoWebRequest)
     * @return 수정 성공 시 200 OK와 빈 SuccessResponse<Void>
     */
    @Operation(
            summary = "회원 정보 수정",
            description = "회원 정보 수정을 진행합니다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공", useReturnTypeSchema = true)
    })
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<SuccessResponse<Void>> modifyUserInfo(
            @Parameter(hidden = true)
            @CurrentUserId
            Long userId,

            @RequestPart(value = "profileImageFile", required = false)
            MultipartFile profileImageFile,

            @RequestPart @Validated
            ModifyUserInfoWebRequest webRequest
    );

    /**
     * 회원을 소프트 탈퇴 처리합니다.
     *
     * 현재 인증된 사용자의 계정을 소프트 삭제(탈퇴)하고 성공 시 200 OK와 빈 SuccessResponse를 반환합니다.
     *
     * @param userId 현재 인증된 사용자의 식별자
     * @return 200 OK 및 빈 SuccessResponse<Void>
     */
    @Operation(
            summary = "회원 소프트 탈퇴",
            description = "회원 탈퇴를 진행합니다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공", useReturnTypeSchema = true)
    })
    @DeleteMapping
    ResponseEntity<SuccessResponse<Void>> withdrawUser(
            @Parameter(hidden = true)
            @CurrentUserId
            Long userId
    );

    /**
     * 현재 로그인한 사용자의 세션과 리프레시 토큰을 무효화하여 로그아웃을 수행합니다.
     * 모든 인증 관련 쿠키(accessToken, refreshToken, accessTokenExpiration, resetToken)를 삭제합니다.
     *
     * @param refreshToken 쿠키에 담긴 리프레시 토큰 — 해당 토큰을 무효화하여 로그아웃을 완료합니다.
     * @param request HTTP 요청 객체 (쿠키 삭제용)
     * @param response HTTP 응답 객체 (쿠키 삭제용)
     * @return 성공 응답을 담은 ResponseEntity(성공 시 본문은 비어있음)
     */
    @Operation(
            summary = "로그아웃",
            description = "현재 로그인한 사용자의 세션 또는 RefreshToken을 무효화하고 모든 인증 관련 쿠키를 삭제하여 로그아웃합니다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공", useReturnTypeSchema = true)
    })
    @PostMapping("/logout")
    ResponseEntity<SuccessResponse<Void>> logout(
            @Parameter(hidden = true)
            @CurrentUserId
            Long userId,

            @Parameter(
                    in = ParameterIn.COOKIE,
                    name = "refreshToken",
                    required = true,
                    description = "리프레시 토큰으로 토큰 재발급을 진행한다.",
                    schema = @Schema(type = "string")
            )
            @CookieValue(value = "refreshToken")
            String refreshToken,

            @Parameter(hidden = true)
            HttpServletRequest request,

            @Parameter(hidden = true)
            HttpServletResponse response
    );
}
