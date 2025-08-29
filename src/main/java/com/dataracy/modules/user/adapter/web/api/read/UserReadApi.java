package com.dataracy.modules.user.adapter.web.api.read;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.support.annotation.CurrentUserId;
import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserInfoWebResponse;
import com.dataracy.modules.user.adapter.web.response.read.GetUserInfoWebResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "User - Read", description = "사용자 관련 API - 조회")
@RequestMapping("/api/v1")
public interface UserReadApi {
    /**
     * 유저의 회원 정보를 조회한다.
     *
     * @return 유저의 회원 정보
     */
    @Operation(
            summary = "유저의 회원 정보를 조회한다.",
            description = "유저의 회원 정보를 조회한다."
    )
    @Parameter(
            in = ParameterIn.HEADER,
            name = "Authorization", required = true,
            schema = @Schema(type = "string"),
            description = "Bearer [Access 토큰]")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 조회에 성공했습니다.", useReturnTypeSchema = true)
    })
    @GetMapping("/user")
    ResponseEntity<SuccessResponse<GetUserInfoWebResponse>> getUserInfo(
            @Parameter(hidden = true)
            @CurrentUserId
            Long userId
    );

    @Operation(
            summary = "타인 유저의 회원 정보를 조회한다.",
            description = "타인 유저의 회원 정보를 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "타인의 회원 정보 조회에 성공했습니다.", useReturnTypeSchema = true)
    })
    @GetMapping("/users/{userId}")
    ResponseEntity<SuccessResponse<GetOtherUserInfoWebResponse>> getOtherUserInfo(
            @PathVariable
            Long userId
    );
}
