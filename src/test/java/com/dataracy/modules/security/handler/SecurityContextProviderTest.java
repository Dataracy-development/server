package com.dataracy.modules.security.handler;

import com.dataracy.modules.auth.domain.model.AnonymousUser;
import com.dataracy.modules.security.principal.CustomUserDetails;
import com.dataracy.modules.user.domain.enums.RoleType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class SecurityContextProviderTest {

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("getAuthentication - 인증 객체가 설정된 경우 해당 인증 객체를 반환한다")
    void getAuthentication_WhenAuthenticationExists_ReturnsAuthentication() {
        // given
        Authentication mockAuth = mock(Authentication.class);
        SecurityContext mockContext = mock(SecurityContext.class);
        given(mockContext.getAuthentication()).willReturn(mockAuth);
        SecurityContextHolder.setContext(mockContext);

        // when
        Authentication result = SecurityContextProvider.getAuthentication();

        // then
        assertThat(result).isEqualTo(mockAuth);
    }

    @Test
    @DisplayName("getAuthentication - 인증 객체가 없는 경우 null을 반환한다")
    void getAuthentication_WhenNoAuthentication_ReturnsNull() {
        // given
        SecurityContext mockContext = mock(SecurityContext.class);
        given(mockContext.getAuthentication()).willReturn(null);
        SecurityContextHolder.setContext(mockContext);

        // when
        Authentication result = SecurityContextProvider.getAuthentication();

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("isAnonymous - 익명 사용자인 경우 true를 반환한다")
    void isAnonymous_WhenAnonymousUser_ReturnsTrue() {
        // given
        AnonymousUser anonymousUser = AnonymousUser.of("anonymous-123");
        Authentication mockAuth = mock(Authentication.class);
        given(mockAuth.getPrincipal()).willReturn(anonymousUser);
        
        SecurityContext mockContext = mock(SecurityContext.class);
        given(mockContext.getAuthentication()).willReturn(mockAuth);
        SecurityContextHolder.setContext(mockContext);

        // when
        boolean result = SecurityContextProvider.isAnonymous();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isAnonymous - 로그인한 사용자인 경우 false를 반환한다")
    void isAnonymous_WhenAuthenticatedUser_ReturnsFalse() {
        // given
        CustomUserDetails userDetails = new CustomUserDetails(1L, RoleType.ROLE_USER);
        Authentication mockAuth = mock(Authentication.class);
        given(mockAuth.getPrincipal()).willReturn(userDetails);
        
        SecurityContext mockContext = mock(SecurityContext.class);
        given(mockContext.getAuthentication()).willReturn(mockAuth);
        SecurityContextHolder.setContext(mockContext);

        // when
        boolean result = SecurityContextProvider.isAnonymous();

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isAnonymous - 인증 객체가 null인 경우 NullPointerException이 발생한다")
    void isAnonymous_WhenNoAuthentication_ThrowsNullPointerException() {
        // given
        SecurityContext mockContext = mock(SecurityContext.class);
        given(mockContext.getAuthentication()).willReturn(null);
        SecurityContextHolder.setContext(mockContext);

        // when & then
        NullPointerException exception = catchThrowableOfType(
                () -> SecurityContextProvider.isAnonymous(),
                NullPointerException.class
        );
        assertAll(
                () -> assertThat(exception).isNotNull()
        );
    }

    @Test
    @DisplayName("isAuthenticated - 로그인한 사용자인 경우 true를 반환한다")
    void isAuthenticated_WhenAuthenticatedUser_ReturnsTrue() {
        // given
        CustomUserDetails userDetails = new CustomUserDetails(1L, RoleType.ROLE_USER);
        Authentication mockAuth = mock(Authentication.class);
        given(mockAuth.getPrincipal()).willReturn(userDetails);
        
        SecurityContext mockContext = mock(SecurityContext.class);
        given(mockContext.getAuthentication()).willReturn(mockAuth);
        SecurityContextHolder.setContext(mockContext);

        // when
        boolean result = SecurityContextProvider.isAuthenticated();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isAuthenticated - 익명 사용자인 경우 false를 반환한다")
    void isAuthenticated_WhenAnonymousUser_ReturnsFalse() {
        // given
        AnonymousUser anonymousUser = AnonymousUser.of("anonymous-123");
        Authentication mockAuth = mock(Authentication.class);
        given(mockAuth.getPrincipal()).willReturn(anonymousUser);
        
        SecurityContext mockContext = mock(SecurityContext.class);
        given(mockContext.getAuthentication()).willReturn(mockAuth);
        SecurityContextHolder.setContext(mockContext);

        // when
        boolean result = SecurityContextProvider.isAuthenticated();

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isAuthenticated - 인증 객체가 null인 경우 false를 반환한다")
    void isAuthenticated_WhenNoAuthentication_ReturnsFalse() {
        // given
        SecurityContext mockContext = mock(SecurityContext.class);
        given(mockContext.getAuthentication()).willReturn(null);
        SecurityContextHolder.setContext(mockContext);

        // when
        boolean result = SecurityContextProvider.isAuthenticated();

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isAuthenticated - principal이 CustomUserDetails가 아닌 경우 false를 반환한다")
    void isAuthenticated_WhenPrincipalIsNotCustomUserDetails_ReturnsFalse() {
        // given
        String principal = "someOtherPrincipal";
        Authentication mockAuth = mock(Authentication.class);
        given(mockAuth.getPrincipal()).willReturn(principal);
        
        SecurityContext mockContext = mock(SecurityContext.class);
        given(mockContext.getAuthentication()).willReturn(mockAuth);
        SecurityContextHolder.setContext(mockContext);

        // when
        boolean result = SecurityContextProvider.isAuthenticated();

        // then
        assertThat(result).isFalse();
    }
}
