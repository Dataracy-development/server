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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User - Command", description = "사용자 관련 API - 수정, 삭제")
@RequestMapping("/api/v1/user")
public interface UserCommandApi {
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

    @Operation(
            summary = "로그아웃",
            description = "현재 로그인한 사용자의 세션 또는 RefreshToken을 무효화하고 로그아웃합니다.",
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
            String refreshToken
    );
}
