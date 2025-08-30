package com.dataracy.modules.user.application.port.in.command.command;

import com.dataracy.modules.user.application.dto.request.command.ModifyUserInfoRequest;
import org.springframework.web.multipart.MultipartFile;

public interface ModifyUserInfoUseCase {
    void modifyUserInfo(Long userId, MultipartFile profileImageFile, ModifyUserInfoRequest requestDto);
}
