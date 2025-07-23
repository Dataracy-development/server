package com.dataracy.modules.dataset.application.service.query;

import com.dataracy.modules.dataset.application.dto.request.DataFilterRequest;
import com.dataracy.modules.dataset.application.dto.response.*;
import com.dataracy.modules.dataset.application.mapper.FilterDataDtoMapper;
import com.dataracy.modules.dataset.application.mapper.PopularDataSetsDtoMapper;
import com.dataracy.modules.dataset.application.mapper.RecentDataSetsDtoMapper;
import com.dataracy.modules.dataset.application.port.elasticsearch.DataRealTimeSearchPort;
import com.dataracy.modules.dataset.application.port.elasticsearch.DataSimilarSearchPort;
import com.dataracy.modules.dataset.application.port.in.*;
import com.dataracy.modules.dataset.application.port.out.DataRepositoryPort;
import com.dataracy.modules.dataset.application.port.query.DataQueryRepositoryPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.dataset.domain.model.vo.DataUser;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datatype.GetDataTypeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.GetOccupationLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.user.FindUsernameUseCase;
import com.dataracy.modules.user.application.port.in.user.GetUserInfoUseCase;
import com.dataracy.modules.user.domain.model.vo.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataQueryService implements
        ValidateDataUseCase,
        DataSimilarSearchUseCase,
        DataPopularSearchUseCase,
        DataDetailUseCase,
        DataFilteredSearchUseCase,
        DataRecentUseCase,
        DataRealTimeUseCase,
        CountDataGroupByTopicLabelUseCase
{
    private final PopularDataSetsDtoMapper popularDataSetsDtoMapper;
    private final FilterDataDtoMapper filterDataDtoMapper;
    private final RecentDataSetsDtoMapper recentDataSetsDtoMapper;

    private final DataRepositoryPort dataRepositoryPort;
    private final DataSimilarSearchPort dataSimilarSearchPort;
    private final DataQueryRepositoryPort dataQueryRepositoryPort;
    private final DataRealTimeSearchPort dataRealTimeSearchPort;

    private final FindUsernameUseCase findUsernameUseCase;
    private final GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;
    private final GetDataSourceLabelFromIdUseCase getDataSourceLabelFromIdUseCase;
    private final GetDataTypeLabelFromIdUseCase getDataTypeLabelFromIdUseCase;
    private final GetUserInfoUseCase getUserInfoUseCase;
    private final GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;
    private final GetOccupationLabelFromIdUseCase getOccupationLabelFromIdUseCase;

    /**
     * 주어진 데이터 ID에 해당하는 데이터의 존재 여부를 검증합니다.
     *
     * 데이터가 존재하지 않으면 {@code DataException}을 {@code NOT_FOUND_DATA} 상태로 발생시킵니다.
     *
     * @param dataId 존재 여부를 확인할 데이터의 ID
     * @throws DataException 데이터가 존재하지 않을 경우 발생
     */
    @Override
    @Transactional(readOnly = true)
    public void validateData(Long dataId) {
        boolean isExist = dataRepositoryPort.existsDataById(dataId);
        if (!isExist) {
            log.warn("데이터 ID가 존재하지 않습니다: {}", dataId);
            throw new DataException(DataErrorStatus.NOT_FOUND_DATA);
        }
    }

    /**
     * 지정한 데이터 ID를 기준으로 유사한 데이터셋 목록을 반환합니다.
     *
     * @param dataId 유사 데이터셋 검색의 기준이 되는 데이터 ID
     * @param size 반환할 유사 데이터셋의 최대 개수
     * @return 유사한 데이터셋의 응답 객체 리스트
     * @throws DataException 데이터가 존재하지 않을 경우 발생
     */
    @Override
    @Transactional(readOnly = true)
    public List<DataSimilarSearchResponse> findSimilarDataSets(Long dataId, int size) {
        Data data = dataQueryRepositoryPort.findDataById(dataId)
                .orElseThrow(() -> {
                    log.error("데이터 검증 후 조회 실패: dataId={}", dataId);
                    return new DataException(DataErrorStatus.NOT_FOUND_DATA);
                });
        return dataSimilarSearchPort.recommendSimilarDataSets(data, size);
    }

    /**
     * 지정된 개수만큼 인기 있는 데이터셋 목록을 조회하여, 각 데이터셋에 사용자명, 주제, 데이터 소스, 데이터 타입 등의 라벨 정보를 포함한 응답 리스트를 반환합니다.
     *
     * @param size 조회할 인기 데이터셋의 최대 개수
     * @return 인기 데이터셋에 대한 상세 정보와 연결된 프로젝트 수가 포함된 응답 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<DataPopularSearchResponse> findPopularDataSets(int size) {
        List<DataWithProjectCountDto> savedDataSets = dataQueryRepositoryPort.findPopularDataSets(size);

        DataLabelMappingResponse labelResponse = labelMapping(savedDataSets);

        return savedDataSets.stream()
                .map(wrapper -> {
                    Data data = wrapper.data();
                    return popularDataSetsDtoMapper.toResponseDto(
                            data,
                            labelResponse.usernameMap().get(data.getUserId()),
                            labelResponse.topicLabelMap().get(data.getTopicId()),
                            labelResponse.dataSourceLabelMap().get(data.getDataSourceId()),
                            labelResponse.dataTypeLabelMap().get(data.getDataTypeId()),
                            wrapper.countConnectedProjects()
                    );
                })
                .toList();
    }

    @Override
    public Page<DataFilterResponse> findFilteredDataSets(DataFilterRequest request, Pageable pageable) {
        Page<DataWithProjectCountDto> savedDataSets = dataQueryRepositoryPort.searchByFilters(request, pageable, request.sortType());

        DataLabelMappingResponse labelResponse = labelMapping(savedDataSets.getContent());

        return savedDataSets.map(wrapper -> {
            Data data = wrapper.data();
            return filterDataDtoMapper.toResponseDto(
                    data,
                    labelResponse.topicLabelMap().get(data.getTopicId()),
                    labelResponse.dataSourceLabelMap().get(data.getDataSourceId()),
                    labelResponse.dataTypeLabelMap().get(data.getDataTypeId()),
                    wrapper.countConnectedProjects()
            );
        });
    }


    /**
     * 데이터셋 DTO 컬렉션에서 사용자, 토픽, 데이터 소스, 데이터 타입의 ID를 추출하여 각 ID에 해당하는 레이블 매핑 정보를 반환합니다.
     *
     * @param savedDataSets 프로젝트 개수가 포함된 데이터셋 DTO 컬렉션
     * @return 사용자명, 토픽 레이블, 데이터 소스 레이블, 데이터 타입 레이블의 매핑 정보를 담은 응답 객체
     */
    private DataLabelMappingResponse labelMapping(Collection<DataWithProjectCountDto> savedDataSets) {
        List<Long> userIds = savedDataSets.stream()
                .map(dto -> dto.data().getUserId())
                .toList();
        List<Long> topicIds = savedDataSets.stream()
                .map(dto -> dto.data().getTopicId())
                .toList();
        List<Long> dataSourceIds = savedDataSets.stream()
                .map(dto -> dto.data().getDataSourceId())
                .toList();
        List<Long> dataTypeIds = savedDataSets.stream()
                .map(dto -> dto.data().getDataTypeId())
                .toList();

        return new DataLabelMappingResponse(
                findUsernameUseCase.findUsernamesByIds(userIds),
                getTopicLabelFromIdUseCase.getLabelsByIds(topicIds),
                getDataSourceLabelFromIdUseCase.getLabelsByIds(dataSourceIds),
                getDataTypeLabelFromIdUseCase.getLabelsByIds(dataTypeIds)
        );
    }
    /**
     * 주어진 데이터 ID에 해당하는 데이터셋의 상세 정보를 조회합니다.
     *
     * 데이터셋의 기본 정보, 작성자 닉네임, 작성자 등급 및 직업 라벨, 주제/데이터 소스/데이터 타입 라벨, 기간, 설명, 분석 가이드, 썸네일 URL, 다운로드 수, 최근 일주일 다운로드 수, 메타데이터(행/열 개수, 미리보기 JSON), 생성일시를 포함한 상세 정보를 반환합니다.
     *
     * @param dataId 조회할 데이터셋의 ID
     * @return 데이터셋의 상세 정보를 담은 DataDetailResponse 객체
     * @throws DataException 데이터셋이 존재하지 않을 경우 발생
     */
    @Override
    @Transactional(readOnly = true)
    public DataDetailResponse getDataDetail(Long dataId) {
        Data data = dataQueryRepositoryPort.findDataWithMetadataById(dataId)
                .orElseThrow(() -> new DataException(DataErrorStatus.NOT_FOUND_DATA));
        UserInfo userInfo = getUserInfoUseCase.getUserInfo(data.getUserId());
        DataUser dataUser = DataUser.from(userInfo);

        String authorLabel = getAuthorLevelLabelFromIdUseCase.getLabelById(dataUser.authorLevelId());
        String occupationLabel = getOccupationLabelFromIdUseCase.getLabelById(dataUser.occupationId());

        return new DataDetailResponse(
                data.getId(),
                data.getTitle(),
                dataUser.nickname(),
                authorLabel,
                occupationLabel,
                getTopicLabelFromIdUseCase.getLabelById(data.getTopicId()),
                getDataSourceLabelFromIdUseCase.getLabelById(data.getDataSourceId()),
                getDataTypeLabelFromIdUseCase.getLabelById(data.getDataTypeId()),
                data.getStartDate(),
                data.getEndDate(),
                data.getDescription(),
                data.getAnalysisGuide(),
                data.getThumbnailUrl(),
                data.getDownloadCount(),
                data.getRecentWeekDownloadCount(),
                data.getMetadata().getRowCount(),
                data.getMetadata().getColumnCount(),
                data.getMetadata().getPreviewJson(),
                data.getCreatedAt()
        );
    }

    @Override
    public List<DataMinimalSearchResponse> findRecentDataSets(int size) {
        List<Data> recentDataSets = dataQueryRepositoryPort.findRecentDataSets(size);

        return recentDataSets.stream()
                .map(recentDataSetsDtoMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<DataMinimalSearchResponse> findRealTimeDataSets(String keyword, int size) {
        return dataRealTimeSearchPort.search(keyword, size);
    }

    @Override
    public List<CountDataGroupResponse> countDataGroups() {
        return dataQueryRepositoryPort.countDataGroups();
    }
}
