package com.dataracy.modules.email.adapter.web.api.command;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.email.adapter.web.request.command.SendEmailWebRequest;
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
public interface EmailCommandApi {
    /**
     * 이메일 인증을 위한 인증 코드를 지정된 이메일 주소로 전송한다.
     *
     * @param webRequest 인증 코드를 받을 이메일 주소 정보가 포함된 요청 객체
     * @return 인증 코드 전송 성공 시 성공 응답을 반환한다.
     */
    @Operation(
            summary = "이메일 인증코드를 전송",
            description = "이메일 인증코드를 전송한다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 인증코드 전송 성공", useReturnTypeSchema = true)
    })
    @PostMapping(value = "/send", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse<Void>> sendCode(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "이메일",
                    content = @Content(schema = @Schema(implementation =  SendEmailWebRequest.class))
            )
            @Validated @RequestBody
            SendEmailWebRequest webRequest
    );
}
