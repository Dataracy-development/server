package com.dataracy.modules.dataset.adapter.jpa.impl.validate;

import org.springframework.stereotype.Repository;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataJpaRepository;
import com.dataracy.modules.dataset.application.port.out.validate.CheckDataExistsByIdPort;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ValidateDataDbAdapter implements CheckDataExistsByIdPort {
  private final DataJpaRepository dataJpaRepository;

  // Entity 상수 정의
  private static final String DATA_ENTITY = "DataEntity";

  /**
   * 주어진 ID에 해당하는 데이터가 저장소에 존재하는지 반환합니다.
   *
   * @param dataId 존재 여부를 확인할 데이터의 ID
   * @return 데이터가 존재하면 true, 존재하지 않으면 false
   */
  @Override
  public boolean existsDataById(Long dataId) {
    boolean isExist = dataJpaRepository.existsById(dataId);
    LoggerFactory.db()
        .logExist(
            DATA_ENTITY,
            "주어진 ID의 데이터가 저장소에 존재 확인이 완료되었습니다. dataId=" + dataId + ", exists=" + isExist);
    return isExist;
  }
}
