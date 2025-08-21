package com.dataracy.modules.dataset.adapter.jpa.impl.command;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEsProjectionDlqEntity;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataEsProjectionDlqRepository;
import com.dataracy.modules.dataset.application.port.out.command.projection.ManageDataProjectionDlqPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManageDataEsProjectionDlqDbAdapter implements ManageDataProjectionDlqPort {
    private final DataEsProjectionDlqRepository dlqRepo;

    /**
     * DataEsProjection DLQ 엔트리를 생성해 영속화한다.
     *
     * <p>주어진 식별자와 상태 정보를 사용해 DataEsProjectionDlqEntity를 생성한 뒤 저장소에 저장한다.</p>
     *
     * @param dataId       대상 데이터의 식별자
     * @param deltaDownload 다운로드 수 변경량(없으면 null 가능)
     * @param setDeleted   삭제 상태로 표시할지 여부(없으면 null 가능)
     * @param lastError    마지막 처리 실패 메시지(없으면 null 가능)
     */
    @Override
    public void save(
            Long dataId,
            Integer deltaDownload,
            Boolean setDeleted,
            String lastError
    ) {
        dlqRepo.save(DataEsProjectionDlqEntity.builder()
                .dataId(dataId)
                .deltaDownload(deltaDownload)
                .setDeleted(setDeleted)
                .lastError(lastError)
                .build());
    }
}
