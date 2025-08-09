package com.dataracy.modules.email.adapter.web.api.validate;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.email.adapter.web.request.validate.VerifyCodeWebRequest;
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

@Tag(name = "Email", description = "이메일 관련 API")
@RequestMapping("/api/v1/email")
public interface EmailVerifyApi {
    /**
     * 이메일과 인증코드를 받아 인증코드의 일치 여부를 검증하는 API 엔드포인트입니다.
     *
     * @param webRequest 이메일과 인증코드 정보를 포함한 요청 객체
     * @return 인증 성공 시 성공 메시지를 포함한 응답
     */
    @Operation(
            summary = "이메일 인증코드 일치여부 확인",
            description = "이메일 인증코드 일치여부를 확인한다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 인증코드가 같은지 확인한다.", useReturnTypeSchema = true)
    })
    @PostMapping(value = "/verify", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse<String>> verifyCode(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "이메일 인증 코드를 확인한다.",
                    content = @Content(schema = @Schema(implementation =  VerifyCodeWebRequest.class))
            )
            @Validated @RequestBody
            VerifyCodeWebRequest webRequest
    );
}
