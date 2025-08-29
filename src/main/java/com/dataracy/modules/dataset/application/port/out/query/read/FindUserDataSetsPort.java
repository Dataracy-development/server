package com.dataracy.modules.dataset.application.port.out.query.read;

import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindUserDataSetsPort {
    Page<DataWithProjectCountDto> findUserDataSets(Long userId, Pageable pageable);
}
