package com.dataracy.modules.dataset.adapter.web.api;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.dataset.adapter.web.mapper.DataFilterWebMapper;
import com.dataracy.modules.dataset.adapter.web.mapper.DataSearchWebMapper;
import com.dataracy.modules.dataset.adapter.web.mapper.DataWebMapper;
import com.dataracy.modules.dataset.adapter.web.request.DataFilterWebRequest;
import com.dataracy.modules.dataset.adapter.web.request.DataUploadWebRequest;
import com.dataracy.modules.dataset.adapter.web.response.*;
import com.dataracy.modules.dataset.application.dto.request.DataFilterRequest;
import com.dataracy.modules.dataset.application.dto.request.DataUploadRequest;
import com.dataracy.modules.dataset.application.dto.response.*;
import com.dataracy.modules.dataset.application.port.in.*;
import com.dataracy.modules.dataset.domain.status.DataSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DataController implements DataApi {
    private final DataWebMapper dataWebMapper;
    private final DataSearchWebMapper dataSearchWebMapper;
    private final DataFilterWebMapper dataFilterWebMapper;

    private final DataUploadUseCase dataUploadUseCase;
    private final DataSimilarSearchUseCase dataSimilarSearchUseCase;
    private final DataPopularSearchUseCase dataPopularSearchUseCase;
    private final DataDetailUseCase dataDetailUseCase;
    private final DataFilteredSearchUseCase dataFilteredSearchUseCase;
    private final DataRecentUseCase dataRecentUseCase;
    private final DataRealTimeUseCase dataRealTimeUseCase;
    private final CountDataGroupByTopicLabelUseCase countDataGroupByTopicLabelUseCase;
    private final ConnectedDataAssociatedWithProjectUseCase connectedDataAssociatedWithProjectUseCase;

    /**
     * 데이터 업로드 요청을 처리하여 데이터셋을 생성하고, 성공 상태의 HTTP 201(Created) 응답을 반환합니다.
     *
     * @param userId 업로드를 요청한 사용자의 ID
     * @param dataFile 업로드할 데이터 파일
     * @param thumbnailFile 데이터셋의 썸네일 파일
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

    /**
     * 주어진 데이터 ID를 기준으로 유사한 데이터셋 목록을 조회합니다.
     *
     * @param dataId 유사 데이터셋 검색의 기준이 되는 데이터 ID
     * @param size 반환할 유사 데이터셋의 최대 개수
     * @return 유사 데이터셋 목록과 성공 상태가 포함된 HTTP 200 OK 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<List<DataSimilarSearchWebResponse>>> searchSimilarDataSets(Long dataId, int size) {
        List<DataSimilarSearchResponse> responseDto = dataSimilarSearchUseCase.findSimilarDataSets(dataId, size);
        List<DataSimilarSearchWebResponse> webResponse = responseDto.stream()
                .map(dataSearchWebMapper::toWebDto)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(DataSuccessStatus.FIND_SIMILAR_DATASETS, webResponse));
    }

    /**
     * 지정한 개수만큼 인기 데이터셋 목록을 조회하여 반환합니다.
     *
     * @param size 조회할 인기 데이터셋의 최대 개수
     * @return 인기 데이터셋 목록과 성공 상태가 포함된 HTTP 200 OK 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<List<DataPopularSearchWebResponse>>> searchPopularDataSets(int size) {
        List<DataPopularSearchResponse> responseDto = dataPopularSearchUseCase.findPopularDataSets(size);
        List<DataPopularSearchWebResponse> webResponse = responseDto.stream()
                .map(dataSearchWebMapper::toWebDto)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(DataSuccessStatus.FIND_POPULAR_DATASETS, webResponse));
    }

    /**
     * 필터 조건과 페이지 정보를 기반으로 데이터셋 목록을 검색하여 반환합니다.
     *
     * @param webRequest 데이터셋 필터링 조건이 담긴 요청 객체
     * @param pageable 페이지네이션 정보
     * @return 필터링 및 페이징된 데이터셋 목록이 포함된 성공 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<Page<DataFilterWebResponse>>> searchFilteredDataSets(DataFilterWebRequest webRequest, Pageable pageable) {
        DataFilterRequest requestDto = dataSearchWebMapper.toApplicationDto(webRequest);
        Page<DataFilterResponse> responseDto = dataFilteredSearchUseCase.findFilteredDataSets(requestDto, pageable);
        Page<DataFilterWebResponse> webResponse = responseDto.map(dataFilterWebMapper::toWebDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(DataSuccessStatus.FIND_FILTERED_DATASETS, webResponse));
    }

    /**
     * 주어진 데이터셋 ID에 해당하는 상세 정보를 조회하여 반환합니다.
     *
     * @param dataId 조회할 데이터셋의 고유 ID
     * @return 데이터셋 상세 정보와 성공 상태가 포함된 HTTP 200 OK 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<DataDetailWebResponse>> getDataDetail(Long dataId) {
        DataDetailResponse responseDto = dataDetailUseCase.getDataDetail(dataId);
        DataDetailWebResponse webResponse = dataWebMapper.toWebDto(responseDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(DataSuccessStatus.GET_DATA_DETAIL, webResponse));
    }

    /**
     * 최근 등록된 데이터셋 목록을 조회하여 반환합니다.
     *
     * @param size 반환할 데이터셋의 최대 개수
     * @return 최근 데이터셋 목록이 포함된 성공 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<List<DataMinimalSearchWebResponse>>> getRecentDataSets(int size) {
        List<DataMinimalSearchResponse> responseDto = dataRecentUseCase.findRecentDataSets(size);
        List<DataMinimalSearchWebResponse> webResponse = responseDto.stream()
                .map(dataSearchWebMapper::toWebDto)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(DataSuccessStatus.GET_RECENT_DATASETS, webResponse));
    }

    /**
     * 실시간으로 키워드에 해당하는 데이터셋 목록을 조회합니다.
     *
     * @param keyword 검색할 키워드
     * @param size 반환할 데이터셋 최대 개수
     * @return 실시간 데이터셋 목록이 포함된 성공 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<List<DataMinimalSearchWebResponse>>> getRealTimeDataSets(String keyword, int size) {
        List<DataMinimalSearchResponse> responseDto = dataRealTimeUseCase.findRealTimeDataSets(keyword, size);
        List<DataMinimalSearchWebResponse> webResponse = responseDto.stream()
                .map(dataSearchWebMapper::toWebDto)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(DataSuccessStatus.FIND_REAL_TIME_DATASETS, webResponse));
    }

    /**
     * 데이터셋을 주제 라벨별로 그룹화하여 각 그룹의 개수를 반환합니다.
     *
     * @return 주제 라벨별 데이터셋 개수 목록이 포함된 성공 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<List<CountDataGroupWebResponse>>> countDataSetsByTopicLabel() {
        List<CountDataGroupResponse> responseDto = countDataGroupByTopicLabelUseCase.countDataGroups();
        List<CountDataGroupWebResponse> webResponse = responseDto.stream()
                .map(dataWebMapper::toWebDto)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(DataSuccessStatus.COUNT_DATASETS_GROUP_BY_TOPIC, webResponse));
    }

    @Override
    public ResponseEntity<SuccessResponse<Page<ConnectedDataAssociatedWithProjectWebResponse>>> searchConnectedDataSetsAssociatedWithProject(Long projectId, Pageable pageable) {
        Page<ConnectedDataAssociatedWithProjectResponse> responseDto = connectedDataAssociatedWithProjectUseCase.findConnectedDataSetsAssociatedWithProject(projectId, pageable);
        Page<ConnectedDataAssociatedWithProjectWebResponse> webResponse = responseDto.map(dataWebMapper::toWebDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(DataSuccessStatus.GET_CONNECTED_DATASETS_ASSOCIATED_PROJECT, webResponse));
    }
}
