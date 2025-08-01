package com.dataracy.modules.user.adapter.jpa.impl;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.adapter.jpa.repository.UserJpaRepository;
import com.dataracy.modules.user.application.port.out.jpa.UserExistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserExistenceAdapter implements UserExistencePort {
    private final UserJpaRepository userJpaRepository;

    @Override
    public boolean existsByNickname(String nickname) {
        boolean exists = userJpaRepository.existsByNickname(nickname);
        LoggerFactory.db().logExist("UserEntity", "해당 닉네임에 해당하는 유저가 존재하는지 유무를 확인하였습니다.");
        return exists;
    }

    @Override
    public boolean existsByEmail(String email) {
        boolean exists = userJpaRepository.existsByEmail(email);
        LoggerFactory.db().logExist("UserEntity", "해당 이메일에 해당하는 유저가 존재하는지 유무를 확인하였습니다.");
        return exists;
    }
}
