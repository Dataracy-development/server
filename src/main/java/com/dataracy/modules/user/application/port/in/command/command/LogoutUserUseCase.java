package com.dataracy.modules.user.application.port.in.command.command;

public interface LogoutUserUseCase {
    void logout(Long userId, String refreshToken);
}
