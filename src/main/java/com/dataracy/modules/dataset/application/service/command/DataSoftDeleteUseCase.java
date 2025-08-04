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
     * ProjectSoftDeleteService의 생성자입니다.
     *
     * 데이터베이스와 Elasticsearch에 대한 소프트 삭제 작업을 처리하는 두 포트 구현체를 주입받아 초기화합니다.
     */
    public DataSoftDeleteUseCase(
            @Qualifier("softDeleteDataDbAdapter") SoftDeleteDataPort softDeleteDataDbDbPort,
            @Qualifier("softDeleteDataEsAdapter") SoftDeleteDataPort softDeleteDataEsPort
    ) {
        this.softDeleteDataDbPort = softDeleteDataDbDbPort;
        this.softDeleteDataEsPort = softDeleteDataEsPort;
    }

    /**
     * 데이터셋을 삭제 상태로 표시하고 Elasticsearch 인덱스에서도 삭제 상태로 반영합니다.
     *
     * @param dataId 삭제할 데이터셋의 식별자
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
     * 데이터셋을 복구 상태로 변경하고, Elasticsearch 인덱스도 복구 상태로 동기화합니다.
     *
     * @param dataId 복구할 데이터셋의 식별자
     */
    @Override
    @Transactional
    public void restoreData(Long dataId) {
        Instant startTime = LoggerFactory.service().logStart("DeleteDataUseCase", "데이터셋 복원 서비스 시작 dataId=" + dataId);
        softDeleteDataDbPort.restoreData(dataId);
        softDeleteDataEsPort.restoreData(dataId);
        LoggerFactory.service().logSuccess("DeleteDataUseCase", "데이터셋 복원 서비스 종료 dataId=" + dataId, startTime);
    }
}
