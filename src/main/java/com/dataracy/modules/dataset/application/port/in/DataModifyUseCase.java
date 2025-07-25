package com.dataracy.modules.dataset.application.port.in;

import com.dataracy.modules.dataset.application.dto.request.DataModifyRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * 데이터셋 업로드 유스케이스
 */
public interface DataModifyUseCase {
void modify(Long dataId, MultipartFile dataFile, MultipartFile thumbnailFile, DataModifyRequest requestDto);
}
