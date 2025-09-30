package com.dataracy.modules.dataset.adapter.jpa.impl.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.adapter.jpa.entity.DataEntity;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataJpaRepository;
import com.dataracy.modules.dataset.application.port.out.query.extractor.FindDownloadDataFileUrlPort;
import com.dataracy.modules.dataset.application.port.out.query.extractor.ExtractDataOwnerPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ExtractDataFileUrlDbAdapterFind implements
        ExtractDataOwnerPort,
        FindDownloadDataFileUrlPort
{
    private final DataJpaRepository dataJpaRepository;

    // Entity 및 메시지 상수 정의
    private static final String DATA_ENTITY = "DataEntity";
    private static final String DATA_NOT_FOUND_MESSAGE = "해당 데이터셋이 존재하지 않습니다. dataId=";

    /**
     * 주어진 데이터 ID에 해당하는 데이터의 업로드 유저 ID를 반환합니다.
     * 데이터가 존재하지 않을 경우 DataException이 발생합니다.
     *
     * @param dataId 사용자 ID를 조회할 데이터의 ID
     * @return 데이터에 연결된 사용자 ID
     * @throws DataException 데이터가 존재하지 않을 때 발생합니다.
     */
    @Override
    public Long findUserIdByDataId(Long dataId) {
        Instant startTime = LoggerFactory.db().logQueryStart(DATA_ENTITY, "[findById] 데이터셋의 작성자 아이디 조회 시작 dataId=" + dataId);
        DataEntity dataEntity = dataJpaRepository.findById(dataId)
                .orElseThrow(() -> {
                    LoggerFactory.db().logWarning(DATA_ENTITY, DATA_NOT_FOUND_MESSAGE + dataId);
                    return new DataException(DataErrorStatus.NOT_FOUND_DATA);
                });
        LoggerFactory.db().logQueryEnd(DATA_ENTITY, "[findById] 데이터셋의 작성자 아이디 조회 종료 dataId=" + dataId, startTime);
        return dataEntity.getUserId();
    }

    /**
     * 삭제된 데이터를 포함하여 주어진 데이터 ID에 해당하는 데이터의 업로드 유저 ID를 반환합니다.
     *
     * @param dataId 소유자 ID를 조회할 데이터의 ID
     * @return 데이터에 연결된 사용자 ID
     * @throws DataException 데이터가 존재하지 않을 경우 DataErrorStatus.NOT_FOUND_DATA 상태로 예외가 발생합니다.
     */
    @Override
    public Long findUserIdIncludingDeleted(Long dataId) {
        Instant startTime = LoggerFactory.db().logQueryStart(DATA_ENTITY, "[findIncludingDeletedData] 삭제된 데이터셋을 포함하여 데이터셋의 작성자 아이디 조회 시작 dataId=" + dataId);
        DataEntity dataEntity = dataJpaRepository.findIncludingDeletedData(dataId)
                .orElseThrow(() -> {
                    LoggerFactory.db().logWarning(DATA_ENTITY, DATA_NOT_FOUND_MESSAGE + dataId);
                    return new DataException(DataErrorStatus.NOT_FOUND_DATA);
                });
        LoggerFactory.db().logQueryEnd(DATA_ENTITY, "[findIncludingDeletedData] 삭제된 데이터셋을 포함하여 데이터셋의 작성자 아이디 조회 종료 dataId=" + dataId, startTime);
        return dataEntity.getUserId();
    }

    /**
     * 주어진 데이터 ID에 해당하는 다운로드된 데이터셋 파일의 URL을 Optional로 반환합니다.
     *
     * @param dataId 조회할 데이터의 ID
     * @return 데이터셋 파일 URL이 존재하면 해당 값을 포함한 Optional, 없으면 Optional.empty()
     */
    @Override
    public Optional<String> findDownloadedDataFileUrl(Long dataId) {
        return findDataFileUrl(dataId, "[findDataFileUrlById] 주어진 데이터 ID에 해당하는 데이터셋 파일 url 조회");
    }

    /**
     * 지정된 데이터 ID에 해당하는 데이터 파일 URL을 Optional로 반환합니다.
     *
     * @param dataId    조회할 데이터의 ID
     * @param logPrefix 로그 메시지에 사용할 접두사
     * @return 데이터 파일 URL이 존재하면 Optional로 반환하며, 없으면 Optional.empty()를 반환합니다.
     */
    private Optional<String> findDataFileUrl(Long dataId, String logPrefix) {
        Instant startTime = LoggerFactory.db().logQueryStart(DATA_ENTITY, logPrefix + " 시작 dataId=" + dataId);
        Optional<String> dataFileUrl = dataJpaRepository.findDataFileUrlById(dataId);
        LoggerFactory.db().logQueryEnd(DATA_ENTITY, logPrefix + " 종료 dataId=" + dataId, startTime);
        return dataFileUrl;
    }
}
