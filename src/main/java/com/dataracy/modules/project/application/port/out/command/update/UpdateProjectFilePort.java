package com.dataracy.modules.project.application.port.out.command.update;

public interface UpdateProjectFilePort {
    /****
 * 지정된 프로젝트의 썸네일 이미지 파일 URL을 새로운 값으로 변경합니다.
 *
 * @param projectId 썸네일 파일 URL을 변경할 프로젝트의 고유 식별자
 * @param fileUrl 새로 저장할 썸네일 이미지 파일의 URL
 */
    void updateThumbnailFile(Long projectId, String fileUrl);
}
