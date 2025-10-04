package com.dataracy.modules.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.util.ExtractHeaderUtil;
import com.dataracy.modules.security.config.SecurityPathConfig;
import com.dataracy.modules.security.principal.CustomUserDetails;
import com.dataracy.modules.security.principal.UserAuthentication;
import com.dataracy.modules.user.domain.enums.RoleType;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
  private final JwtValidateUseCase jwtValidateUseCase;
  private final SecurityPathConfig securityPathConfig;

  /**
   * HTTP 요청에서 JWT 토큰을 추출하고 검증하여 인증 정보를 설정합니다.
   *
   * <p>요청 헤더에서 JWT 액세스 토큰을 추출하고, 토큰의 유효성을 검증한 후 사용자 ID와 역할 정보를 기반으로 인증 객체를 생성하여
   * SecurityContextHolder에 등록합니다. 토큰이 없거나 유효하지 않은 경우 인증 예외를 발생시키며, 예외 정보는 요청 속성에 저장됩니다.
   *
   * @param request 현재 HTTP 요청
   * @param response 현재 HTTP 응답
   * @param filterChain 필터 체인
   * @throws BusinessException 비즈니스 로직 처리 중 발생하는 예외
   * @throws ServletException 서블릿 처리 중 발생하는 예외
   * @throws IOException 입출력 처리 중 발생하는 예외
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws BusinessException, ServletException, IOException {
    try {
      // 헤더에서 어세스 토큰 추출
      String accessToken =
          ExtractHeaderUtil.extractAccessToken(request)
              .orElseThrow(
                  () -> new AuthException(AuthErrorStatus.NOT_FOUND_ACCESS_TOKEN_IN_HEADER));

      // 어세스 토큰 유효성 검증
      jwtValidateUseCase.validateToken(accessToken);

      // 토큰에서 유저 id, 유저 역할 반환
      Long userId = jwtValidateUseCase.getUserIdFromToken(accessToken);
      RoleType role = jwtValidateUseCase.getRoleFromToken(accessToken);

      // 인증 객체 SecurityContextHolder에 주입
      setAuthentication(request, userId, role);
    } catch (BusinessException e) {
      // 예외를 요청에 저장
      request.setAttribute("filter.error", e);
      throw new AuthenticationCredentialsNotFoundException("JWT 인증 실패", e);
    }
    filterChain.doFilter(request, response);
  }

  /**
   * 현재 HTTP 요청이 JWT 인증 필터를 우회해야 하는지 여부를 반환합니다.
   *
   * @param request 현재 HTTP 요청 객체
   * @return 필터 예외 대상 경로일 경우 true, 그렇지 않으면 false
   */
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();
    String method = request.getMethod();

    // 1. 기본 예외 경로 확인
    // 2. GET 메서드의 공개 API 확인
    return securityPathConfig.isJwtExcludedPath(path)
        || ("GET".equals(method)
            && (securityPathConfig.isPublicProjectPath(path)
                || securityPathConfig.isPublicDatasetPath(path)));
  }

  /**
   * 해당 유저를 인증 객체를 SecurityContextHolder에 추가한다.
   *
   * @param request 현재 HTTP 요청 객체
   * @param userId 유저 아이디
   * @param role 유저 역할
   */
  private void setAuthentication(HttpServletRequest request, Long userId, RoleType role) {
    CustomUserDetails userDetails = new CustomUserDetails(userId, role);
    UserAuthentication authentication =
        new UserAuthentication(userDetails, null, userDetails.getAuthorities());
    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
