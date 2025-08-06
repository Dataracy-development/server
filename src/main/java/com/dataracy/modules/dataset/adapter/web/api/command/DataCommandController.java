package com.dataracy.modules.dataset.adapter.web.api.command;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.support.annotation.AuthorizationDataEdit;
import com.dataracy.modules.dataset.adapter.web.mapper.command.DataCommandWebMapper;
import com.dataracy.modules.dataset.adapter.web.request.command.ModifyDataWebRequest;
import com.dataracy.modules.dataset.adapter.web.request.command.UploadDataWebRequest;
import com.dataracy.modules.dataset.application.dto.request.command.ModifyDataRequest;
import com.dataracy.modules.dataset.application.dto.request.command.UploadDataRequest;
import com.dataracy.modules.dataset.application.port.in.command.content.*;
import com.dataracy.modules.dataset.domain.status.DataSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
public class DataCommandController implements DataCommandApi {
    private final DataCommandWebMapper dataCommandWebMapper;

    private final UploadDataUseCase uploadDataUseCase;
    private final ModifyDataUseCase modifyDataUseCase;
    private final DeleteDataUseCase deleteDataUseCase;
    private final RestoreDataUseCase restoreDataUseCase;
    private final DownloadDataFileUseCase downloadDataFileUseCase;

    private static final int PRESIGNED_URL_EXPIRY_SECONDS = 300;

    /**
     * 데이터 파일과 썸네일 파일을 업로드하여 새로운 데이터셋을 생성합니다.
     *
     * @param userId 업로드를 요청한 사용자의 ID
     * @param dataFile 업로드할 데이터 파일
     * @param thumbnailFile 데이터셋의 썸네일 파일
     * @param webRequest 데이터셋 생성에 필요한 추가 정보
     * @return 데이터셋 생성 성공 상태가 포함된 HTTP 201(Created) 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<Void>> uploadData(
            Long userId,
            MultipartFile dataFile,
            MultipartFile thumbnailFile,
            UploadDataWebRequest webRequest
    ) {
        Instant startTime = LoggerFactory.api().logRequest("[UploadData] 데이터셋 업로드 API 요청 시작");

        try {
            UploadDataRequest requestDto = dataCommandWebMapper.toApplicationDto(webRequest);
            uploadDataUseCase.uploadData(userId, dataFile, thumbnailFile, requestDto);
        } finally {
            LoggerFactory.api().logResponse("[UploadData] 데이터셋 업로드 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of(DataSuccessStatus.CREATED_DATASET));
    }

    /****
     * 데이터셋의 정보를 수정합니다.
     *
     * @param dataId 수정할 데이터셋의 식별자
     * @param dataFile 새로운 데이터 파일 (선택 사항)
     * @param thumbnailFile 새로운 썸네일 파일 (선택 사항)
     * @param webRequest 데이터셋 수정 요청 정보
     * @return 데이터셋 수정 성공 시 성공 응답을 반환합니다.
     */
    @Override
    @AuthorizationDataEdit
    public ResponseEntity<SuccessResponse<Void>> modifyData(Long dataId, MultipartFile dataFile, MultipartFile thumbnailFile, ModifyDataWebRequest webRequest) {
        Instant startTime = LoggerFactory.api().logRequest("[ModifyData] 데이터셋 수정 API 요청 시작");

        try {
            ModifyDataRequest requestDto = dataCommandWebMapper.toApplicationDto(webRequest);
            modifyDataUseCase.modifyData(dataId, dataFile, thumbnailFile, requestDto);
        } finally {
            LoggerFactory.api().logResponse("[ModifyData] 데이터셋 수정 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(DataSuccessStatus.MODIFY_DATASET));
    }

    /**
     * 지정한 데이터셋을 소프트 삭제 상태로 변경합니다.
     *
     * @param dataId 삭제할 데이터셋의 식별자
     * @return 데이터셋 삭제 성공 여부를 나타내는 응답
     */
    @Override
    @AuthorizationDataEdit
    public ResponseEntity<SuccessResponse<Void>> deleteData(Long dataId) {
        Instant startTime = LoggerFactory.api().logRequest("[DeleteData] 데이터셋 Soft Delete 삭제 API 요청 시작");

        try {
            deleteDataUseCase.deleteData(dataId);
        } finally {
            LoggerFactory.api().logResponse("[DeleteData] 데이터셋 Soft Delete 삭제 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(DataSuccessStatus.DELETE_DATASET));
    }

    /**
     * 삭제된 데이터셋을 복구하여 활성 상태로 전환합니다.
     *
     * @param dataId 복구할 데이터셋의 식별자
     * @return 복구 성공 상태가 포함된 HTTP 200 OK 응답
     */
    @Override
    @AuthorizationDataEdit(restore = true)
    public ResponseEntity<SuccessResponse<Void>> restoreData(Long dataId) {
        Instant startTime = LoggerFactory.api().logRequest("[RestoreData] 데이터셋 복원 API 요청 시작");

        try {
            restoreDataUseCase.restoreData(dataId);
        } finally {
            LoggerFactory.api().logResponse("[RestoreData] 데이터셋 복원 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(DataSuccessStatus.RESTORE_DATASET));
    }

    /**
     * 데이터셋 파일의 300초 유효 사전 서명 다운로드 URL을 반환합니다.
     *
     * @param dataId 다운로드할 데이터셋의 ID
     * @return 사전 서명된 다운로드 URL이 포함된 성공 응답 객체
     */
    @Override
    public ResponseEntity<SuccessResponse<String>> getPreSignedDataUrl(Long dataId) {
        Instant startTime = LoggerFactory.api().logRequest("[GetPreSignedDataUrl] 데이터셋 다운로드 url API 요청 시작");
        String preSignedUrl;

        try {
            preSignedUrl = downloadDataFileUseCase.download(dataId, PRESIGNED_URL_EXPIRY_SECONDS);
        } finally {
            LoggerFactory.api().logResponse("[GetPreSignedDataUrl] 데이터셋 다운로드 url 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(DataSuccessStatus.DOWNLOAD_DATASET, preSignedUrl));
    }
}
