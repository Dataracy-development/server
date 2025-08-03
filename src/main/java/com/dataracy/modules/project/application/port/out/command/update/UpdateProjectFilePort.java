package com.dataracy.modules.project.application.port.out.command.update;

public interface UpdateProjectFilePort {
    /**
     * 지정된 프로젝트의 파일(이미지 URL) 정보를 새로운 URL로 갱신합니다.
     *
     * @param projectId 파일 정보를 변경할 프로젝트의 식별자
     * @param fileUrl 새로 저장할 이미지 URL
     */
    void updateFile(Long projectId, String fileUrl);
}
