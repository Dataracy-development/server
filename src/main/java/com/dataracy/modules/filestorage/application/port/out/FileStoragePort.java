package com.dataracy.modules.filestorage.application.port.out;

import org.springframework.web.multipart.MultipartFile;

public interface FileStoragePort {
    /**
     * 지정된 디렉터리에 MultipartFile을 업로드하고 업로드된 파일의 식별자 또는 URL을 반환합니다.
     *
     * @param directory 파일을 저장할 디렉터리 경로
     * @param file 업로드할 MultipartFile 객체
     * @return 업로드된 파일의 식별자 또는 접근 가능한 URL
     */
    String upload(String directory, MultipartFile file);
    /**
     * 지정된 파일 URL에 해당하는 파일을 삭제합니다.
     *
     * @param fileUrl 삭제할 파일의 URL
     */
    void delete(String fileUrl);
    /**
     * 주어진 키에 해당하는 파일의 접근 가능한 URL을 반환합니다.
     *
     * @param key 파일을 식별하는 키
     * @return 파일의 접근 가능한 URL
     */
    String getUrl(String key);
}
