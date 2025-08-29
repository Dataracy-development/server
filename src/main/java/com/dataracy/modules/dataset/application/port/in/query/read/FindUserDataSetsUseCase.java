package com.dataracy.modules.dataset.application.port.in.query.read;

import com.dataracy.modules.dataset.application.dto.response.read.UserDataResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindUserDataSetsUseCase {
    Page<UserDataResponse> findUserDataSets(Long userId, Pageable pageable);
}
