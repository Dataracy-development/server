package com.dataracy.modules.filestorage.application.port.out;

import org.springframework.web.multipart.MultipartFile;

public interface FileStoragePort {
    /**
 * 지정된 디렉터리에 파일을 업로드하고 업로드된 파일의 식별자 또는 접근 가능한 URL을 반환합니다.
 *
 * @param directory 파일을 저장할 대상 디렉터리 경로
 * @param file 업로드할 파일 객체
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
 * 지정된 키로 식별되는 파일의 접근 가능한 URL을 반환합니다.
 *
 * @param key 파일을 식별하는 고유 키
 * @return 파일의 접근 가능한 URL 문자열
 */
    String getUrl(String key);
}
