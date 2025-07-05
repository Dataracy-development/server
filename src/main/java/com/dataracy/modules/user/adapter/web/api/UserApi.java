package com.dataracy.modules.user.adapter.web.api;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.support.annotation.CurrentUserId;
import com.dataracy.modules.user.adapter.web.request.ChangePasswordWebRequest;
import com.dataracy.modules.user.adapter.web.request.DuplicateNicknameWebRequest;
import com.dataracy.modules.user.adapter.web.request.OnboardingWebRequest;
import com.dataracy.modules.user.adapter.web.request.SelfSignUpWebRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "사용자 관련 API")
@RequestMapping("/api/v1")
public interface UserApi {
    /**
     * 자체 회원가입을 진행한다.
     *
     * @param webRequest 자체 회원가입 웹 요청 DTO
     * @param response httpServletResponse
     * @return 자체 회원가입 성공 후 쿠키와 레디스에 리프레시 토큰을 저장하고 리다이렉트한다.
     */
    @Operation(
            summary = "자체 회원가입",
            description = "자체 회원가입을 진행합니다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "자체 회원가입 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 DTO 에러(누락 또는 유효성 검증 실패)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/signup/self", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse<Void>> signUpUserSelf(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "회원가입 정보",
                    content = @Content(schema = @Schema(implementation = SelfSignUpWebRequest.class))
            )
            @Validated @RequestBody
            SelfSignUpWebRequest webRequest,

            HttpServletResponse response
    );

    /**
     * 레지스터 토큰에서 추출한 정보들과 닉네임으로 회원가입을 진행한다.
     *
     * @param registerToken 쿠키로부터 받은 레지스터 토큰
     * @param webRequest 웹 요청 DTO (닉네임)
     * @param response httpServletResponse
     * @return 소셜 온보딩 회원가입 진행 후 쿠키와 레디스에 리프레시 토큰을 저장하고 리다이렉트한다.
     */
    @Operation(
            summary = "소셜 로그인 후 온보딩 회원가입",
            description = "레지스터 토큰에서 추출한 정보와 추가 정보를 기반으로 소셜 회원가입을 진행합니다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "소셜 회원가입 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 DTO 에러(누락 또는 유효성 검증 실패)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "레지스터 토큰 인증 실패(재회원가입 진행 요청)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/signup/oauth", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SuccessResponse<Void>> signUpUserOAuth(
            @Parameter(
                    in = ParameterIn.COOKIE,
                    name = "registerToken",
                    required = true,
                    description = "소셜 회원가입을 위한 Register Token",
                    schema = @Schema(type = "string")
            )
            @CookieValue(name = "registerToken")
            String registerToken,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "온보딩 추가 정보 (닉네임, 직업 등)",
                    content = @Content(schema = @Schema(implementation = OnboardingWebRequest.class))
            )
            @Validated @RequestBody
            OnboardingWebRequest webRequest,

            HttpServletResponse response
    );

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

    /**
     * 비밀번호를 변경한다.
     *
     * @param webRequest 웹 요청 DTO (비밀번호)
     * @return 비밀번호 변경 성공
     */
    @Operation(
            summary = "비밀번호를 변경한다.",
            description = "비밀번호를 변경한다. 유저id는 SecurityContext에서 주입받는다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 변겅에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class))),
    })
    @PatchMapping(value = "/user/password/change")
    ResponseEntity<SuccessResponse<Void>> changePassword(
            @Parameter(hidden = true)
            @CurrentUserId
            Long userId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "변경할 비밀번호",
                    content = @Content(schema = @Schema(implementation = ChangePasswordWebRequest.class))
            )
            @Validated @RequestBody ChangePasswordWebRequest webRequest
    );
}
