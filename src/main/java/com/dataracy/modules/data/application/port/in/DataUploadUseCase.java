package com.dataracy.modules.data.application.port.in;

import com.dataracy.modules.data.application.dto.request.DataUploadRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * 데이터셋 업로드 유스케이스
 */
public interface DataUploadUseCase {
    Long upload(Long userId, MultipartFile dataFile, MultipartFile thumbnailFile, DataUploadRequest requestDto);
}
