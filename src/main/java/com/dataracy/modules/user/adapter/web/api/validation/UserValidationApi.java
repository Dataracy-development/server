package com.dataracy.modules.user.adapter.web.api.validation;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.user.adapter.web.request.validation.DuplicateNicknameWebRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "사용자 관련 API")
@RequestMapping("/api/v1")
public interface UserValidationApi {
    /**
     * 닉네임 중복유무를 확인한다.
     *
     * @param webRequest 웹 요청 DTO (닉네임)
     * @return 200 응답 : 닉네임 중복되지 않음, 409 응답 : 닉네임 중복
     */
    @Operation(
            summary = "닉네임 중복체크를 진행한다.",
            description = "제공받은 웹 요청 DTO의 닉네임이 이미 존재하는 닉네임인지 유무를 판단한다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임이 중복되지 않습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "409", description = "닉네임이 중복됩니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
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
