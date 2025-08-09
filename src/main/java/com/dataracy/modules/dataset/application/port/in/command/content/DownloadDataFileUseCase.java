package com.dataracy.modules.dataset.application.port.in.command.content;

import com.dataracy.modules.dataset.application.dto.response.download.GetDataPreSignedUrlResponse;

public interface DownloadDataFileUseCase {
    /**
 * 지정된 데이터셋 파일에 대해 만료 시간이 설정된 다운로드 링크 또는 토큰을 생성합니다.
 *
 * @param dataId 다운로드할 데이터셋의 고유 식별자
 * @param expirationSeconds 다운로드 링크의 유효 기간(초 단위)
 * @return 생성된 다운로드 링크 또는 토큰 문자열
 */
    GetDataPreSignedUrlResponse downloadDataFile(Long dataId, int expirationSeconds);
}
