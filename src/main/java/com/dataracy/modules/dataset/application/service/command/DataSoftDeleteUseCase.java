package com.dataracy.modules.dataset.application.service.command;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.port.in.command.content.DeleteDataUseCase;
import com.dataracy.modules.dataset.application.port.in.command.content.RestoreDataUseCase;
import com.dataracy.modules.dataset.application.port.out.command.delete.SoftDeleteDataPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class DataSoftDeleteUseCase implements
        DeleteDataUseCase,
        RestoreDataUseCase
{
    private final SoftDeleteDataPort softDeleteDataDbPort;
    private final SoftDeleteDataPort softDeleteDataEsPort;

    /**
     * 데이터셋의 소프트 삭제 및 복구를 위해 데이터베이스와 Elasticsearch 포트 구현체를 주입받아 초기화합니다.
     *
     * @param softDeleteDataDbDbPort 데이터베이스에서 소프트 삭제 작업을 수행하는 포트 구현체
     * @param softDeleteDataEsPort Elasticsearch에서 소프트 삭제 작업을 수행하는 포트 구현체
     */
    public DataSoftDeleteUseCase(
            @Qualifier("softDeleteDataDbAdapter") SoftDeleteDataPort softDeleteDataDbDbPort,
            @Qualifier("softDeleteDataEsAdapter") SoftDeleteDataPort softDeleteDataEsPort
    ) {
        this.softDeleteDataDbPort = softDeleteDataDbDbPort;
        this.softDeleteDataEsPort = softDeleteDataEsPort;
    }

    /**
     * 지정한 데이터셋을 소프트 삭제 처리하여 데이터베이스와 Elasticsearch 모두에서 삭제 상태로 반영합니다.
     *
     * @param dataId 삭제 처리할 데이터셋의 고유 식별자
     */
    @Override
    @Transactional
    public void deleteData(Long dataId) {
        Instant startTime = LoggerFactory.service().logStart("DeleteDataUseCase", "데이터셋 Soft Delete 삭제 서비스 시작 dataId=" + dataId);
        softDeleteDataDbPort.deleteData(dataId);
        softDeleteDataEsPort.deleteData(dataId);
        LoggerFactory.service().logSuccess("DeleteDataUseCase", "데이터셋 Soft Delete 삭제 서비스 종료 dataId=" + dataId, startTime);
    }

    /**
     * 지정한 데이터셋을 복구 상태로 전환하고, 해당 변경 사항을 Elasticsearch 인덱스에도 반영합니다.
     *
     * @param dataId 복구할 데이터셋의 고유 식별자
     */
    @Override
    @Transactional
    public void restoreData(Long dataId) {
        Instant startTime = LoggerFactory.service().logStart("RestoreDataUseCase", "데이터셋 복원 서비스 시작 dataId=" + dataId);
        softDeleteDataDbPort.restoreData(dataId);
        softDeleteDataEsPort.restoreData(dataId);
        LoggerFactory.service().logSuccess("RestoreDataUseCase", "데이터셋 복원 서비스 종료 dataId=" + dataId, startTime);
    }
}
