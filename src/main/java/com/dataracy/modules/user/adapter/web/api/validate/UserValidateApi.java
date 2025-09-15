package com.dataracy.modules.user.adapter.web.api.validate;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.user.adapter.web.request.validate.DuplicateNicknameWebRequest;
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

@Tag(name = "User - Validate", description = "사용자 관련 API - 유효성 검사")
@RequestMapping("/api/v1")
public interface UserValidateApi {
    /**
     * 닉네임의 중복 여부를 확인하는 API 엔드포인트입니다.
     *
     * 요청된 닉네임이 이미 존재하는지 검사하여, 중복이 없으면 200 OK를 반환합니다.
     * 닉네임이 중복된 경우 409 Conflict 상태 코드로 응답합니다.
     *
     * @param webRequest 중복 확인을 원하는 닉네임 정보를 담은 요청 객체
     * @return 닉네임이 중복되지 않으면 200 OK, 중복된 경우 409 Conflict 상태 코드의 응답
     */
    @Operation(
            summary = "닉네임 중복체크를 진행한다.",
            description = "제공받은 웹 요청 DTO의 닉네임이 이미 존재하는 닉네임인지 유무를 판단한다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임이 중복되지 않습니다.", useReturnTypeSchema = true)
    })
    @PostMapping(value = "/nickname/check", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse<Void>> duplicateNickname(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "확인하고자 하는 닉네임",
                    content = @Content(schema = @Schema(implementation = DuplicateNicknameWebRequest.class))
            )
            @Validated @RequestBody
            DuplicateNicknameWebRequest webRequest
    );
}
