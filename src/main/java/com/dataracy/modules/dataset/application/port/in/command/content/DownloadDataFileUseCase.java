package com.dataracy.modules.dataset.application.port.in.command.content;

public interface DownloadDataFileUseCase {
    /**
     * 지정된 데이터셋 파일에 대한 다운로드 링크를 생성합니다.
     *
     * @param dataId 다운로드할 데이터셋의 식별자
     * @param expirationSeconds 다운로드 링크의 만료 시간(초)
     * @return 생성된 다운로드 링크 또는 토큰
     */
    String download(Long dataId, int expirationSeconds);
}
