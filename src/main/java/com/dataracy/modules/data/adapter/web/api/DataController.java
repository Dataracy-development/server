package com.dataracy.modules.data.adapter.web.api;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.data.adapter.web.mapper.DataWebMapper;
import com.dataracy.modules.data.adapter.web.request.DataUploadWebRequest;
import com.dataracy.modules.data.application.dto.request.DataUploadRequest;
import com.dataracy.modules.data.application.port.in.DataUploadUseCase;
import com.dataracy.modules.data.domain.status.DataSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class DataController implements DataApi {
    private final DataWebMapper dataWebMapper;

    private final DataUploadUseCase dataUploadUseCase;

    /**
     * 데이터 업로드 요청을 처리하고, 데이터셋 생성 성공 응답을 반환합니다.
     *
     * @param userId 업로드를 요청한 사용자 ID
     * @param dataFile 업로드할 데이터 파일
     * @param thumbnailFile 데이터셋 썸네일 파일
     * @param webRequest 데이터 업로드 요청 정보
     * @return 데이터셋 생성 성공 상태가 포함된 HTTP 201(Created) 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<Void>> uploadData(
            Long userId,
            MultipartFile dataFile,
            MultipartFile thumbnailFile,
            DataUploadWebRequest webRequest
    ) {
        DataUploadRequest requestDto = dataWebMapper.toApplicationDto(webRequest);
        dataUploadUseCase.upload(userId, dataFile, thumbnailFile, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of(DataSuccessStatus.CREATED_DATASET));
    }
}
