package com.dataracy.modules.dataset.adapter.jpa.impl.command;

import org.springframework.stereotype.Repository;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.adapter.jpa.entity.DataEntity;
import com.dataracy.modules.dataset.adapter.jpa.mapper.DataEntityMapper;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataJpaRepository;
import com.dataracy.modules.dataset.application.dto.request.command.ModifyDataRequest;
import com.dataracy.modules.dataset.application.port.out.command.create.CreateDataPort;
import com.dataracy.modules.dataset.application.port.out.command.update.UpdateDataFilePort;
import com.dataracy.modules.dataset.application.port.out.command.update.UpdateDataPort;
import com.dataracy.modules.dataset.application.port.out.command.update.UpdateThumbnailFilePort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DataCommandDbAdapter
    implements CreateDataPort, UpdateDataFilePort, UpdateThumbnailFilePort, UpdateDataPort {
  private final DataJpaRepository dataJpaRepository;

  // Entity 및 메시지 상수 정의
  private static final String DATA_ENTITY = "DataEntity";
  private static final String DATA_NOT_FOUND_MESSAGE = "해당 데이터셋이 존재하지 않습니다. dataId=";

  /**
   * 데이터셋 도메인 객체를 데이터베이스에 저장하고 저장된 결과를 반환합니다.
   *
   * @param data 저장할 데이터셋 도메인 객체
   * @return 저장된 데이터셋 도메인 객체
   */
  @Override
  public Data saveData(Data data) {
    DataEntity dataEntity = DataEntityMapper.toEntity(data);
    Data savedData = DataEntityMapper.toDomain(dataJpaRepository.save(dataEntity));
    LoggerFactory.db()
        .logSave(DATA_ENTITY, String.valueOf(savedData.getId()), "데이터셋 파일 업로드가 완료되었습니다.");
    return savedData;
  }

  /**
   * 지정한 데이터의 파일 URL과 파일 크기(Byte)를 업데이트합니다.
   *
   * <p>데이터 엔티티가 존재하지 않으면 DataException(DataErrorStatus.NOT_FOUND_DATA)을 던집니다.
   *
   * @param dataId 데이터 엔티티의 식별자
   * @param dataFileUrl 새로 설정할 데이터 파일의 URL
   * @param dataFileSize 새 데이터 파일의 크기(바이트) — null일 수 있으며, null이면 크기 업데이트를 생략하거나 기존 값을 유지합니다.
   * @throws com.dataracy.modules.dataset.exception.DataException 데이터가 존재하지 않을 경우 발생
   */
  @Override
  public void updateDataFile(Long dataId, String dataFileUrl, Long dataFileSize) {
    DataEntity dataEntity =
        dataJpaRepository
            .findById(dataId)
            .orElseThrow(
                () -> {
                  LoggerFactory.db().logWarning(DATA_ENTITY, DATA_NOT_FOUND_MESSAGE + dataId);
                  return new DataException(DataErrorStatus.NOT_FOUND_DATA);
                });
    dataEntity.updateDataFile(dataFileUrl, dataFileSize);
    dataJpaRepository.save(dataEntity);
    LoggerFactory.db().logUpdate(DATA_ENTITY, String.valueOf(dataId), "데이터셋 파일 업데이트가 완료되었습니다.");
  }

  /**
   * 지정한 데이터 ID의 데이터셋 썸네일 파일 URL을 새로운 값으로 변경합니다.
   *
   * @param dataId 썸네일 파일 URL을 변경할 데이터의 ID
   * @param thumbnailFileUrl 새로 지정할 썸네일 파일의 URL
   * @throws DataException 데이터가 존재하지 않을 경우 발생합니다.
   */
  @Override
  public void updateThumbnailFile(Long dataId, String thumbnailFileUrl) {
    DataEntity dataEntity =
        dataJpaRepository
            .findById(dataId)
            .orElseThrow(
                () -> {
                  LoggerFactory.db().logWarning(DATA_ENTITY, DATA_NOT_FOUND_MESSAGE + dataId);
                  return new DataException(DataErrorStatus.NOT_FOUND_DATA);
                });
    dataEntity.updateDataThumbnailFile(thumbnailFileUrl);
    dataJpaRepository.save(dataEntity);
    LoggerFactory.db()
        .logUpdate(DATA_ENTITY, String.valueOf(dataId), "데이터셋 썸네일 이미지 파일 업데이트가 완료되었습니다.");
  }

  /**
   * 데이터 ID에 해당하는 데이터셋 정보를 수정 요청에 따라 변경합니다. 데이터가 존재하지 않을 경우 DataException이 발생합니다.
   *
   * @param dataId 수정할 데이터셋의 ID
   * @param requestDto 데이터 수정 요청 정보
   */
  @Override
  public void modifyData(Long dataId, ModifyDataRequest requestDto) {
    DataEntity dataEntity =
        dataJpaRepository
            .findById(dataId)
            .orElseThrow(
                () -> {
                  LoggerFactory.db().logWarning(DATA_ENTITY, DATA_NOT_FOUND_MESSAGE + dataId);
                  return new DataException(DataErrorStatus.NOT_FOUND_DATA);
                });
    dataEntity.modify(requestDto);
    dataJpaRepository.save(dataEntity);
    LoggerFactory.db().logUpdate(DATA_ENTITY, String.valueOf(dataId), "데이터셋 업데이트가 완료되었습니다.");
  }
}
