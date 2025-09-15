package com.dataracy.modules.dataset.application.port.out.query.extractor;

import java.util.Optional;

public interface FindDownloadDataFileUrlPort {
    /**
     * 주어진 데이터 ID에 해당하는 데이터셋 파일의 경로나 URL을 Optional로 반환합니다.
     *
     * @param dataId 조회할 데이터의 고유 ID
     * @return 데이터셋 파일의 경로나 URL이 존재하면 해당 값을 포함한 Optional, 없으면 빈 Optional
     */
    Optional<String> findDownloadedDataFileUrl(Long dataId);
}
