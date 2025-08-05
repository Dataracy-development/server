package com.dataracy.modules.dataset.application.port.in.command.content;

import com.dataracy.modules.dataset.application.dto.request.command.UploadDataRequest;
import org.springframework.web.multipart.MultipartFile;

public interface UploadDataUseCase {
    /**
 * 데이터셋 파일과 썸네일 파일을 업로드하여 고유 식별자를 반환합니다.
 *
 * @param userId 업로드를 요청한 사용자의 ID
 * @param dataFile 업로드할 데이터셋 파일
 * @param thumbnailFile 데이터셋에 연결할 썸네일 파일
 * @param requestDto 업로드 요청에 대한 추가 정보가 담긴 DTO
 * @return 업로드된 데이터의 고유 식별자
 */
    Long uploadData(Long userId, MultipartFile dataFile, MultipartFile thumbnailFile, UploadDataRequest requestDto);
}
