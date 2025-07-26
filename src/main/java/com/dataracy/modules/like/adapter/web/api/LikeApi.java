package com.dataracy.modules.like.adapter.web.api;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.support.annotation.CurrentUserId;
import com.dataracy.modules.like.adapter.web.request.TargetLikeWebRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@Tag(name = "Like", description = "좋아요 관련 API")
@RequestMapping("/api/v1/likes")
public interface LikeApi {

    @Operation(
            summary = "해당 타겟에 대한 좋아요 처리를 한다.",
            description = "해당 타겟에 대한 좋아요 처리를 한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "해당 타겟에 대한 좋아요 처리를 한다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse<Void>> modifyTargetLike(
            @Parameter(hidden = true)
            @CurrentUserId
            Long userId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "좋아요 타겟 정보",
                    content = @Content(schema = @Schema(implementation = TargetLikeWebRequest.class))
            )
            @Validated @RequestBody
            TargetLikeWebRequest webRequest
    );
}
