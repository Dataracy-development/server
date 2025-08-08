package com.dataracy.modules.filestorage.adapter.web.api;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "File", description = "파일 관련 API")
@RequestMapping("/api/v1/files")
public interface FileApi {

    /**
     * 지정된 S3 URL과 유효기간(초)을 기반으로 PreSigned URL을 발급합니다.
     *
     * @param s3Url PreSigned URL을 생성할 대상 S3 URL
     * @param expirationSeconds PreSigned URL의 유효기간(초), 기본값은 300초
     * @return 생성된 PreSigned URL을 포함하는 성공 응답
     */
    @Operation(
            summary = "S3 PreSigned URL 발급",
            description = "s3Url을 바탕으로 유효기간이 지정된 PreSigned URL을 발급합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "s3Url을 바탕으로 유효기간이 지정된 PreSigned URL을 발급에 성공했습니다.", useReturnTypeSchema = true)
    })
    @GetMapping("/pre-signed-url")
    ResponseEntity<SuccessResponse<String>> getPreSignedUrl(
            @RequestParam String s3Url,
            @RequestParam(defaultValue = "300") int expirationSeconds
    );
}
