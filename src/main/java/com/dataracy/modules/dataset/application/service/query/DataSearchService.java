package com.dataracy.modules.dataset.application.service.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.dto.request.search.FilteringDataRequest;
import com.dataracy.modules.dataset.application.dto.response.read.RecentMinimalDataResponse;
import com.dataracy.modules.dataset.application.dto.response.search.FilteredDataResponse;
import com.dataracy.modules.dataset.application.dto.response.search.SimilarDataResponse;
import com.dataracy.modules.dataset.application.dto.response.support.DataLabelMapResponse;
import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;
import com.dataracy.modules.dataset.application.mapper.search.FilteredDataDtoMapper;
import com.dataracy.modules.dataset.application.port.in.query.read.FindDataLabelMapUseCase;
import com.dataracy.modules.dataset.application.port.in.query.search.SearchFilteredDataSetsUseCase;
import com.dataracy.modules.dataset.application.port.in.query.search.SearchRealTimeDataSetsUseCase;
import com.dataracy.modules.dataset.application.port.in.query.search.SearchSimilarDataSetsUseCase;
import com.dataracy.modules.dataset.application.port.out.query.read.FindDataPort;
import com.dataracy.modules.dataset.application.port.out.query.search.SearchFilteredDataSetsPort;
import com.dataracy.modules.dataset.application.port.out.query.search.SearchRealTimeDataSetsPort;
import com.dataracy.modules.dataset.application.port.out.query.search.SearchSimilarDataSetsPort;
import com.dataracy.modules.dataset.domain.enums.DataSortType;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataSearchService implements
        SearchSimilarDataSetsUseCase,
        SearchFilteredDataSetsUseCase,
        SearchRealTimeDataSetsUseCase
{
    private final FilteredDataDtoMapper filteredDataDtoMapper;

    private final FindDataPort findDataPort;
    private final FindDataLabelMapUseCase findDataLabelMapUseCase;

    private final SearchSimilarDataSetsPort searchSimilarDataSetsPort;
    private final SearchFilteredDataSetsPort searchFilteredDataSetsPort;
    private final SearchRealTimeDataSetsPort searchRealTimeDataSetsPort;

    /**
     * 지정한 데이터 ID를 기준으로 유사한 데이터셋 목록을 조회합니다.
     *
     * @param dataId 유사 데이터셋 검색의 기준이 되는 데이터 ID
     * @param size 반환할 유사 데이터셋의 최대 개수
     * @return 유사한 데이터셋의 응답 객체 리스트
     * @throws DataException 데이터가 존재하지 않을 경우 발생
     */
    @Override
    @Transactional(readOnly = true)
    public List<SimilarDataResponse> searchSimilarDataSets(Long dataId, int size) {
        Instant startTime = LoggerFactory.service().logStart("SearchSimilarDataSetsUseCase", "지정한 데이터 ID를 기준으로 유사한 데이터셋 목록을 조회 서비스 시작 dataId=" + dataId);
        Data data = findDataPort.findDataById(dataId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning("SearchSimilarDataSetsUseCase", "해당 데이터셋이 존재하지 않습니다. dataId=" + dataId);
                    return new DataException(DataErrorStatus.NOT_FOUND_DATA);
                });
        List<SimilarDataResponse> similarDataResponses = searchSimilarDataSetsPort.searchSimilarDataSets(data, size);
        LoggerFactory.service().logSuccess("SearchSimilarDataSetsUseCase", "지정한 데이터 ID를 기준으로 유사한 데이터셋 목록을 조회 서비스 종료 dataId=" + dataId, startTime);
        return similarDataResponses;
    }

    /**
     * 요청된 필터, 정렬, 페이지네이션을 적용해 데이터셋을 조회하고 라벨(사용자명·주제·데이터소스·데이터유형)과
     * 연결된 프로젝트 수를 주입한 상태로 결과를 페이지 형태로 반환합니다.
     *
     * 상세:
     * - 검색된 데이터들의 라벨 매핑을 조회해 각 데이터 DTO에 사용자명(username), 주제 라벨, 데이터 소스 라벨,
     *   데이터 타입 라벨과 연결된 프로젝트 수를 포함한 FilteredDataResponse로 변환합니다.
     *
     * @param request  필터 및 정렬 기준을 포함한 검색 요청
     * @param pageable 페이지 번호 및 크기 등 페이지네이션 정보
     * @return 필터 및 정렬이 적용된 FilteredDataResponse의 페이지
     */
    @Override
    @Transactional(readOnly = true)
    public Page<FilteredDataResponse> searchFilteredDataSets(FilteringDataRequest request, Pageable pageable) {
        Instant startTime = LoggerFactory.service().logStart("SearchFilteredDataSetsUseCase", "필터링된 데이터셋 목록 조회 서비스 시작 keyword=" + request.keyword());

        DataSortType dataSortType = DataSortType.of(request.sortType());
        Page<DataWithProjectCountDto> savedDataSets = searchFilteredDataSetsPort.searchByFilters(request, pageable, dataSortType);
        DataLabelMapResponse labelResponse = findDataLabelMapUseCase.labelMapping(savedDataSets.getContent());

        Page<FilteredDataResponse> filteredDataResponses = savedDataSets.map(wrapper -> {
            Data data = wrapper.data();
            return filteredDataDtoMapper.toResponseDto(
                    data,
                    labelResponse.usernameMap().get(data.getUserId()),
                    labelResponse.topicLabelMap().get(data.getTopicId()),
                    labelResponse.dataSourceLabelMap().get(data.getDataSourceId()),
                    labelResponse.dataTypeLabelMap().get(data.getDataTypeId()),
                    wrapper.countConnectedProjects()
            );
        });

        LoggerFactory.service().logSuccess("SearchFilteredDataSetsUseCase", "필터링된 데이터셋 목록 조회 서비스 종료 keyword=" + request.keyword(), startTime);
        return filteredDataResponses;
    }

    /**
     * 주어진 키워드로 실시간 데이터셋을 검색하여 최소 정보 목록을 반환합니다.
     *
     * @param keyword 검색에 사용할 키워드
     * @param size 반환할 최대 데이터셋 개수
     * @return 검색된 데이터셋의 최소 정보 목록. 키워드가 비어 있거나 null이면 빈 리스트를 반환합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public List<RecentMinimalDataResponse> searchRealTimeDataSets(String keyword, int size) {
        Instant startTime = LoggerFactory.service().logStart("SearchRealTimeDataSetsUseCase", "자동완성을 위한 실시간 데이터셋 목록 조회 서비스 시작 keyword=" + keyword);
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        List<RecentMinimalDataResponse> recentMinimalDataResponses = searchRealTimeDataSetsPort.searchRealTimeDataSets(keyword, size);
        LoggerFactory.service().logSuccess("SearchRealTimeDataSetsUseCase", "자동완성을 위한 실시간 데이터셋 목록 조회 서비스 종료 keyword=" + keyword, startTime);
        return recentMinimalDataResponses;
    }
}
