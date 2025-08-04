package com.dataracy.modules.dataset.adapter.jpa.impl.validate;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataJpaRepository;
import com.dataracy.modules.dataset.application.port.out.query.validate.CheckDataExistsByIdPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ValidateDataDbAdapter implements CheckDataExistsByIdPort {
    private final DataJpaRepository dataJpaRepository;

    /**
     * 주어진 ID의 데이터가 저장소에 존재하는지 확인합니다.
     *
     * @param dataId 존재 여부를 확인할 데이터의 ID
     * @return 데이터가 존재하면 true, 존재하지 않으면 false
     */
    @Override
    public boolean existsDataById(Long dataId) {
        boolean isExist = dataJpaRepository.existsById(dataId);
        LoggerFactory.db().logExist("DataEntity", "주어진 ID의 데이터가 저장소에 존재하ㅈ 완료되었습니다. dataId=" + dataId + ", exists=" + isExist);
        return isExist;
    }
}
