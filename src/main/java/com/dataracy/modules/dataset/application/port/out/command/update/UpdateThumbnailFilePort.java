package com.dataracy.modules.dataset.application.port.out.command.update;

public interface UpdateThumbnailFilePort {
    /**
     * 지정된 데이터 엔티티의 썸네일 파일 URL을 새 값으로 변경합니다.
     *
     * @param dataId 썸네일 파일 URL을 업데이트할 데이터의 고유 식별자
     * @param thumbFileUrl 새로 저장할 썸네일 파일의 URL
     */
    void updateThumbnailFile(Long dataId, String thumbFileUrl);
}
