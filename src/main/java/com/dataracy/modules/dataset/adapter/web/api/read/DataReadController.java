package com.dataracy.modules.dataset.adapter.web.api.read;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.dataset.adapter.web.mapper.read.DataReadWebMapper;
import com.dataracy.modules.dataset.adapter.web.response.read.*;
import com.dataracy.modules.dataset.application.dto.response.read.*;
import com.dataracy.modules.dataset.application.port.in.query.read.*;
import com.dataracy.modules.dataset.domain.status.DataSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DataReadController implements DataReadApi {
    private final DataReadWebMapper dataReadWebMapper;

    private final GetPopularDataSetsUseCase getPopularDataSetsUseCase;
    private final GetDataDetailUseCase getDataDetailUseCase;
    private final GetRecentMinimalDataSetsUseCase getRecentMinimalDataSetsUseCase;
    private final GetDataGroupCountUseCase getDataGroupCountUseCase;
    private final FindConnectedDataSetsUseCase findConnectedDataSetsUseCase;

    /**
     * 지정한 개수만큼 인기 데이터셋 목록을 조회하여 반환합니다.
     *
     * @param size 조회할 인기 데이터셋의 최대 개수
     * @return 인기 데이터셋 목록과 성공 상태가 포함된 HTTP 200 OK 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<List<PopularDataWebResponse>>> getPopularDataSets(int size) {
        List<PopularDataResponse> responseDto = getPopularDataSetsUseCase.getPopularDataSets(size);
        List<PopularDataWebResponse> webResponse = responseDto.stream()
                .map(dataReadWebMapper::toWebDto)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(DataSuccessStatus.FIND_POPULAR_DATASETS, webResponse));
    }

    /**
     * 주어진 데이터셋 ID에 해당하는 상세 정보를 조회하여 반환합니다.
     *
     * @param dataId 조회할 데이터셋의 고유 ID
     * @return 데이터셋 상세 정보와 성공 상태가 포함된 HTTP 200 OK 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<DataDetailWebResponse>> getDataDetail(Long dataId) {
        DataDetailResponse responseDto = getDataDetailUseCase.getDataDetail(dataId);
        DataDetailWebResponse webResponse = dataReadWebMapper.toWebDto(responseDto);

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
    public ResponseEntity<SuccessResponse<List<RecentMinimalDataWebResponse>>> getRecentDataSets(int size) {
        List<RecentMinimalDataResponse> responseDto = getRecentMinimalDataSetsUseCase.getRecentDataSets(size);
        List<RecentMinimalDataWebResponse> webResponse = responseDto.stream()
                .map(dataReadWebMapper::toWebDto)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(DataSuccessStatus.GET_RECENT_DATASETS, webResponse));
    }

    /**
     * 데이터셋을 주제 라벨별로 그룹화하여 각 그룹의 데이터셋 개수를 조회합니다.
     *
     * @return 주제 라벨별 데이터셋 개수 목록이 포함된 성공 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<List<DataGroupCountWebResponse>>> getDataCountByTopicLabel() {
        List<DataGroupCountResponse> responseDto = getDataGroupCountUseCase.getDataGroupCountByTopicLabel();
        List<DataGroupCountWebResponse> webResponse = responseDto.stream()
                .map(dataReadWebMapper::toWebDto)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(DataSuccessStatus.COUNT_DATASETS_GROUP_BY_TOPIC, webResponse));
    }

    /**
     * 프로젝트에 연결된 데이터셋 목록을 페이지네이션하여 조회합니다.
     *
     * @param projectId 데이터셋을 조회할 대상 프로젝트의 ID
     * @param pageable 페이지네이션 정보
     * @return 프로젝트에 연결된 데이터셋 목록과 성공 상태를 포함한 HTTP 200 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<Page<ConnectedDataWebResponse>>> findConnectedDataSetsAssociatedWithProject(Long projectId, Pageable pageable) {
        Page<ConnectedDataResponse> responseDto = findConnectedDataSetsUseCase.findConnectedDataSetsAssociatedWithProject(projectId, pageable);
        Page<ConnectedDataWebResponse> webResponse = responseDto.map(dataReadWebMapper::toWebDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(DataSuccessStatus.GET_CONNECTED_DATASETS_ASSOCIATED_PROJECT, webResponse));
    }
}
