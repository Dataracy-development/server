package com.dataracy.modules.user.application.port.in.query.extractor;

import com.dataracy.modules.user.application.dto.response.read.GetOtherUserDataResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserInfoResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserProjectResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetOtherUserInfoUseCase {
    GetOtherUserInfoResponse getOtherUserInfo(Long userId);
    Page<GetOtherUserProjectResponse> getOtherExtraProjects(Long userId, Pageable pageable);
    Page<GetOtherUserDataResponse> getOtherExtraDataSets(Long userId, Pageable pageable);
}
