package com.dataracy.modules.email.presentation.api;

import com.dataracy.modules.common.dto.SuccessResponse;
import com.dataracy.modules.email.application.dto.request.SendEmailRequestDto;
import com.dataracy.modules.email.application.dto.request.VerifyCodeRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Email", description = "이메일 관련 API")
@RequestMapping("/api/v1/public")
public interface EmailApi {

    /**
     * 이메일 인증을 위한 코드를 전송한다.
     * @param requestDto 이메일
     * @return Void
     */
    @Operation(
            summary = "이메일 인증코드를 전송",
            description = "이메일 인증코드를 전송한다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 인증코드 전송",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PostMapping(value = "/email/send", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse<Void>> send(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "이메일",
                    content = @Content(schema = @Schema(implementation =  SendEmailRequestDto.class))
            )
            @RequestBody
            SendEmailRequestDto requestDto
    );

    /**
     * 이메일 인증을 위한 코드를 전송한다.
     * @param requestDto 이메일
     * @return Void
     */
    @Operation(
            summary = "이메일 인증코드 일치여부 확인",
            description = "이메일 인증코드 일치여부를 확인한다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 인증코드가 같은지 확인한다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PostMapping(value = "/email/verify", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse<Void>> verify(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "이메일 인증 코드를 확인한다.",
                    content = @Content(schema = @Schema(implementation =  VerifyCodeRequestDto.class))
            )
            @RequestBody
            VerifyCodeRequestDto requestDto
    );
}
