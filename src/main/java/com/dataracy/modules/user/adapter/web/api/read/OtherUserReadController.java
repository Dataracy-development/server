package com.dataracy.modules.user.adapter.web.api.read;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.adapter.web.mapper.read.OtherUserReadWebMapper;
import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserDataWebResponse;
import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserInfoWebResponse;
import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserProjectWebResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserDataResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserInfoResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserProjectResponse;
import com.dataracy.modules.user.application.port.in.query.extractor.GetOtherUserInfoUseCase;
import com.dataracy.modules.user.domain.status.UserSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
public class OtherUserReadController implements OtherUserReadApi {
    private final OtherUserReadWebMapper otherUserReadWebMapper;

    private final GetOtherUserInfoUseCase getOtherUserInfoUseCase;

    @Override
    public ResponseEntity<SuccessResponse<GetOtherUserInfoWebResponse>> getOtherUserInfo(Long userId) {
        Instant startTime = LoggerFactory.api().logRequest("[GetOtherUserInfo] 타인 회원정보 조회 API 요청 시작");
        GetOtherUserInfoWebResponse webResponse;

        try {
            GetOtherUserInfoResponse responseDto = getOtherUserInfoUseCase.getOtherUserInfo(userId);
            webResponse = otherUserReadWebMapper.toWebDto(responseDto);
        } finally {
            LoggerFactory.api().logResponse("[GetOtherUserInfo] 타인 회원정보 조회 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(UserSuccessStatus.OK_GET_OTHER_USER_INFO, webResponse));
    }

    @Override
    public ResponseEntity<SuccessResponse<Page<GetOtherUserProjectWebResponse>>> getOtherProjects(Long userId, Pageable pageable) {
        Instant startTime = LoggerFactory.api().logRequest("[GetOtherProjects] 타인이 업로드한 프로젝트 목록 추가 조회 API 요청 시작");
        Page<GetOtherUserProjectWebResponse> webResponse;

        try {
            Page<GetOtherUserProjectResponse> responseDto = getOtherUserInfoUseCase.getOtherExtraProjects(userId, pageable);
            webResponse = responseDto.map(otherUserReadWebMapper::toWebDto);
        } finally {
            LoggerFactory.api().logResponse("[GetOtherProjects] 타인이 업로드한 프로젝트 목록 추가 조회 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(UserSuccessStatus.OK_GET_OTHER_EXTRA_PROJECTS, webResponse));

    }

    @Override
    public ResponseEntity<SuccessResponse<Page<GetOtherUserDataWebResponse>>> getOtherDataSets(Long userId, Pageable pageable) {
        Instant startTime = LoggerFactory.api().logRequest("[GetOtherDataSets] 타인이 업로드한 데이터셋 목록 추가 조회 API 요청 시작");
        Page<GetOtherUserDataWebResponse> webResponse;

        try {
            Page<GetOtherUserDataResponse> responseDto = getOtherUserInfoUseCase.getOtherExtraDataSets(userId, pageable);
            webResponse = responseDto.map(otherUserReadWebMapper::toWebDto);
        } finally {
            LoggerFactory.api().logResponse("[GetOtherDataSets] 타인이 업로드한 데이터셋 목록 추가 조회 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(UserSuccessStatus.OK_GET_OTHER_EXTRA_DATASETS, webResponse));
    }
}
