package com.dataracy.modules.user.adapter.web.api.read;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserDataWebResponse;
import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserInfoWebResponse;
import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserProjectWebResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "User - Read", description = "사용자 관련 API - 조회")
@RequestMapping("/api/v1/users")
public interface OtherUserReadApi {
    @Operation(
            summary = "타인 유저의 회원 정보를 조회한다.",
            description = "타인 유저의 회원 정보를 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "타인의 회원 정보 조회에 성공했습니다.", useReturnTypeSchema = true)
    })
    @GetMapping("/{userId}")
    ResponseEntity<SuccessResponse<GetOtherUserInfoWebResponse>> getOtherUserInfo(
            @PathVariable
            Long userId
    );

    @Operation(
            summary = "타인 유저가 업로드한 프로젝트 목록의 추가 해당 페이지를 조회한다.",
            description = "타인 유저가 업로드한 프로젝트 목록의 추가 해당 페이지를 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "타인 유저가 업로드한 프로젝트 목록의 추가 해당 페이지를 조회에 성공했습니다.", useReturnTypeSchema = true)
    })
    @GetMapping("/{userId}/projects")
    ResponseEntity<SuccessResponse<Page<GetOtherUserProjectWebResponse>>> getOtherProjects(
            @PathVariable
            Long userId,

            @PageableDefault(size = 5, page = 0)
            Pageable pageable
    );

    @Operation(
            summary = "타인 유저가 업로드한 데이터셋 목록의 추가 해당 페이지를 조회한다.",
            description = "타인 유저가 업로드한 데이터셋 목록의 추가 해당 페이지를 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "타인 유저가 업로드한 데이터셋 목록의 추가 해당 페이지를 조회에 성공했습니다.", useReturnTypeSchema = true)
    })
    @GetMapping("/{userId}/datasets")
    ResponseEntity<SuccessResponse<Page<GetOtherUserDataWebResponse>>> getOtherDataSets(
            @PathVariable
            Long userId,

            @PageableDefault(size = 5, page = 0)
            Pageable pageable
    );
}
