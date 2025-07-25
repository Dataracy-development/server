package com.dataracy.modules.dataset.application.port.in;

import com.dataracy.modules.dataset.application.dto.request.DataModifyRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * 데이터셋 업로드 유스케이스
 */
public interface DataModifyUseCase {
/**
 * 지정된 데이터셋의 정보를 새로운 파일 및 썸네일과 함께 수정합니다.
 *
 * @param dataId        수정할 데이터셋의 식별자
 * @param dataFile      새로 업로드할 데이터 파일
 * @param thumbnailFile 새로 업로드할 썸네일 파일
 * @param requestDto    데이터셋 수정에 필요한 추가 정보가 담긴 요청 객체
 */
void modify(Long dataId, MultipartFile dataFile, MultipartFile thumbnailFile, DataModifyRequest requestDto);
}
