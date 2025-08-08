package com.dataracy.modules.user.application.mapper.command;

import com.dataracy.modules.user.application.dto.request.signup.OnboardingRequest;
import com.dataracy.modules.user.application.dto.request.signup.SelfSignUpRequest;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 도메인 요청 DTO -> 도메인 모델
 */
@Component
public class CreateUserDtoMapper {

    @Value("${default.profile.image-url}")
    private String defaultProfileImageUrl;

    public User toDomain(
            SelfSignUpRequest requestDto,
            String providerId,
            String encodedPassword
    ) {
        String introductionText = "안녕하세요. " + requestDto.nickname() + "입니다.";
        return User.of(
                null,
                ProviderType.LOCAL,
                providerId,
                RoleType.ROLE_USER,
                requestDto.email(),
                encodedPassword,
                requestDto.nickname(),
                requestDto.authorLevelId(),
                requestDto.occupationId(),
                requestDto.topicIds(),
                requestDto.visitSourceId(),
                defaultProfileImageUrl,
                introductionText,
                requestDto.isAdTermsAgreed(),
                false
        );
    }

    public User toDomain(
            OnboardingRequest requestDto,
            String provider,
            String providerId,
            String email
    ) {
        String introductionText = "안녕하세요. " + requestDto.nickname() + "입니다.";
        return User.of(
                null,
                ProviderType.of(provider),
                providerId,
                RoleType.ROLE_USER,
                email,
                null,
                requestDto.nickname(),
                requestDto.authorLevelId(),
                requestDto.occupationId(),
                requestDto.topicIds(),
                requestDto.visitSourceId(),
                defaultProfileImageUrl,
                introductionText,
                requestDto.isAdTermsAgreed(),
                false
        );
    }
}
