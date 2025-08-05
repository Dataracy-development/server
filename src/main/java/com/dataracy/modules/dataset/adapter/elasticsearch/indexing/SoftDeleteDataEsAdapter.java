package com.dataracy.modules.dataset.adapter.elasticsearch.indexing;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.adapter.elasticsearch.document.DataDeletedUpdate;
import com.dataracy.modules.dataset.application.dto.document.DataSearchDocument;
import com.dataracy.modules.dataset.application.port.out.command.delete.SoftDeleteDataPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("softDeleteDataEsAdapter")
@RequiredArgsConstructor
public class SoftDeleteDataEsAdapter implements SoftDeleteDataPort {
    private final ElasticsearchClient client;
    private static final String INDEX = "data_index";

    /**
     * 데이터셋 문서의 삭제 상태를 지정된 작업(예: 소프트 삭제, 복원)에 따라 업데이트합니다.
     *
     * @param dataId 삭제 상태를 변경할 데이터셋의 ID
     * @param update 적용할 삭제 상태 업데이트 정보
     * @param operation 수행 중인 작업의 설명(예: "soft delete", "복원")
     */
    private void updateDeletedStatus(Long dataId, DataDeletedUpdate update, String operation) {
        try {
            client.update(u -> u
                            .index(INDEX)
                            .id(dataId.toString())
                            .doc(update),
                    DataSearchDocument.class
            );
            LoggerFactory.elastic().logUpdate(INDEX, String.valueOf(dataId), "데이터셋 " + operation + " 완료: dataId=" + dataId);
        } catch (IOException e) {
            LoggerFactory.elastic().logError(INDEX, "데이터셋 " + operation + " 실패: dataId=" + dataId, e);
        }
    }

    /**
     * 지정된 데이터 ID의 문서를 소프트 삭제 상태로 변경합니다.
     *
     * @param dataId 소프트 삭제할 데이터의 ID
     */
    @Override
    public void deleteData(Long dataId) {
        updateDeletedStatus(dataId, DataDeletedUpdate.deleted(), "soft delete");
    }

    /**
     * 지정한 데이터 문서의 삭제 상태를 복원 상태로 변경합니다.
     *
     * @param dataId 복원할 데이터 문서의 고유 식별자
     */
    @Override
    public void restoreData(Long dataId) {
        updateDeletedStatus(dataId, DataDeletedUpdate.restored(), "복원");
    }
}
