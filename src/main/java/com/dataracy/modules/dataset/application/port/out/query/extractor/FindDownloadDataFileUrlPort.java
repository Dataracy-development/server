package com.dataracy.modules.dataset.application.port.out.query.extractor;

import java.util.Optional;

public interface FindDownloadDataFileUrlPort {
    /**
     * 지정된 데이터 ID에 해당하는 데이터셋 파일을 조회하여 반환합니다.
     *
     * @param dataId 조회할 데이터의 고유 ID
     * @return 데이터셋 파일의 경로나 URL을 포함하는 Optional 객체. 파일이 없으면 빈 Optional 반환
     */
    Optional<String> findDownloadedDataFileUrl(Long dataId);
}
