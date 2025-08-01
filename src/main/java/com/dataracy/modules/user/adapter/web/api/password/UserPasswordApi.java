package com.dataracy.modules.user.adapter.web.api.password;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.support.annotation.CurrentUserId;
import com.dataracy.modules.user.adapter.web.request.password.ChangePasswordWebRequest;
import com.dataracy.modules.user.adapter.web.request.password.ConfirmPasswordWebRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "User - password", description = "사용자 관련 API")
@RequestMapping("/api/v1")
public interface UserPasswordApi {
    /**
     * 비밀번호를 변경한다.
     *
     * @param webRequest 웹 요청 DTO (비밀번호)
     * @return 비밀번호 변경 성공
     */
    @Operation(
            summary = "비밀번호를 변경한다.",
            description = "비밀번호를 변경한다."
    )
    @Parameter(
            in = ParameterIn.HEADER,
            name = "Authorization", required = true,
            schema = @Schema(type = "string"),
            description = "Bearer [Access 토큰]")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 변겅에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class))),
    })
    @PatchMapping(value = "/user/password/change")
    ResponseEntity<SuccessResponse<Void>> changePassword(
            @Parameter(hidden = true)
            @CurrentUserId
            Long userId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "변경할 비밀번호",
                    content = @Content(schema = @Schema(implementation = ChangePasswordWebRequest.class))
            )
            @Validated @RequestBody
            ChangePasswordWebRequest webRequest
    );

    /**
     * 비밀번호 확인한다.
     *
     * @param webRequest 웹 요청 DTO (비밀번호)
     * @return 비밀번호 확인 성공
     */
    @Operation(
            summary = "비밀번호를 확인한다.",
            description = "비밀번호 일치 여부를 확인한다."
    )
    @Parameter(
            in = ParameterIn.HEADER,
            name = "Authorization", required = true,
            schema = @Schema(type = "string"),
            description = "Bearer [Access 토큰]")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 확인에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class))),
    })
    @PostMapping(value = "/user/password/confirm")
    ResponseEntity<SuccessResponse<Void>> confirmPassword(
            @Parameter(hidden = true)
            @CurrentUserId
            Long userId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "확인할 비밀번호",
                    content = @Content(schema = @Schema(implementation = ConfirmPasswordWebRequest.class))
            )
            @Validated @RequestBody
            ConfirmPasswordWebRequest webRequest
    );
}
