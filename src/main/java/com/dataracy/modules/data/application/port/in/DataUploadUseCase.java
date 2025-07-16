package com.dataracy.modules.data.application.port.in;

import com.dataracy.modules.data.application.dto.request.DataUploadRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * 데이터셋 업로드 유스케이스
 */
public interface DataUploadUseCase {
    /****
 * 데이터셋과 썸네일 파일을 업로드하고 업로드된 데이터의 식별자를 반환합니다.
 *
 * @param userId 업로드를 수행하는 사용자의 ID
 * @param dataFile 업로드할 데이터 파일
 * @param thumbnailFile 데이터셋에 연결할 썸네일 파일
 * @param requestDto 데이터 업로드 요청에 대한 추가 정보가 담긴 DTO
 * @return 업로드된 데이터의 고유 식별자
 */
Long upload(Long userId, MultipartFile dataFile, MultipartFile thumbnailFile, DataUploadRequest requestDto);
}
