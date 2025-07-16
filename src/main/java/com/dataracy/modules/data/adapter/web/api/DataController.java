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
