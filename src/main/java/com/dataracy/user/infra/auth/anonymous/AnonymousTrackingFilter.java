//package com.dataracy.user.infra.anonymous;
//
//import com.dataracy.common.util.CookieUtil;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.authentication.AnonymousAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.UUID;
//
//@Component
//@Order(1)
//public class AnonymousTrackingFilter extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String anonymousId = CookieUtil.getAnonymousIdFromCookies(request);
//
//        // 없으면 생성하고 Set-Cookie로 응답
//        if (anonymousId == null) {
//            anonymousId = UUID.randomUUID().toString();
//            CookieUtil.setAnonymousIdInCookies(response, anonymousId, 60 * 60 * 24 * 14 * 1000);
//            System.out.println("new : " + anonymousId);
//        } else {
//            System.out.println("old : " + anonymousId);
//        }
//
//        request.setAttribute("anonymousId", anonymousId);
//
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        // 인증 객체가 없다면, 익명 인증 객체 수동 등록
//        if (auth == null) {
//            Authentication authentication = createAnonymousAuthentication(anonymousId);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            System.out.println("inSecurityContext : " + anonymousId);
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//private Authentication createAnonymousAuthentication(String anonymousId) {
//    AnonymousUser principal = AnonymousUser.of(anonymousId);
//    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
//    return new AnonymousAuthenticationToken("anonymousKey", principal, authorities);
//}
//
//}
