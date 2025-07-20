package com.dataracy.modules.user.application.port.in.user;

import com.dataracy.modules.user.domain.model.vo.UserInfo;

public interface GetUserInfoUseCase {
    UserInfo getUserInfo(Long userId);
}
