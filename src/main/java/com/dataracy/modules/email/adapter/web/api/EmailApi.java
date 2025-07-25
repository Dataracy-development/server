package com.dataracy.modules.email.adapter.web.api;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.email.adapter.web.request.SendEmailWebRequest;
import com.dataracy.modules.email.adapter.web.request.VerifyCodeWebRequest;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Email", description = "이메일 관련 API")
@RequestMapping("/api/v1/email")
public interface EmailApi {
    /**
     * 이메일 인증을 위한 코드를 전송한다.
     * @param webRequest 이메일
     * @return 인증 코드 전송 성공
     */
    @Operation(
            summary = "이메일 인증코드를 전송",
            description = "이메일 인증코드를 전송한다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 인증코드 전송 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "500", description = "인증번호 발송에 실패했습니다. 다시 시도해주세요",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/send", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse<Void>> send(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "이메일",
                    content = @Content(schema = @Schema(implementation =  SendEmailWebRequest.class))
            )
            @Validated @RequestBody
            SendEmailWebRequest webRequest
    );

    /**
     * 이메일 인증코드의 일치여부를 검증한다.
     * @param webRequest 이메일, 인증코드
     * @return 검증 성공
     */
    @Operation(
            summary = "이메일 인증코드 일치여부 확인",
            description = "이메일 인증코드 일치여부를 확인한다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 인증코드가 같은지 확인한다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "이메일 인증코드가 일치하지 않습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/verify", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse<Void>> verify(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "이메일 인증 코드를 확인한다.",
                    content = @Content(schema = @Schema(implementation =  VerifyCodeWebRequest.class))
            )
            @Validated @RequestBody
            VerifyCodeWebRequest webRequest
    );
}
