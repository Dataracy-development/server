package com.dataracy.modules.user.adapter.web.api.command;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.support.annotation.CurrentUserId;
import com.dataracy.modules.user.adapter.web.request.command.ModifyUserInfoWebRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
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
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
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
}
