package com.dataracy.modules.dataset.application.service.query;

import com.dataracy.modules.dataset.application.dto.response.DataLabelMappingResponse;
import com.dataracy.modules.dataset.application.dto.response.DataPopularSearchResponse;
import com.dataracy.modules.dataset.application.dto.response.DataSimilarSearchResponse;
import com.dataracy.modules.dataset.application.dto.response.DataWithProjectCountDto;
import com.dataracy.modules.dataset.application.mapper.PopularDataSetsDtoMapper;
import com.dataracy.modules.dataset.application.port.elasticsearch.DataSimilarSearchPort;
import com.dataracy.modules.dataset.application.port.in.DataPopularSearchUseCase;
import com.dataracy.modules.dataset.application.port.in.DataSimilarSearchUseCase;
import com.dataracy.modules.dataset.application.port.in.ValidateDataUseCase;
import com.dataracy.modules.dataset.application.port.out.DataRepositoryPort;
import com.dataracy.modules.dataset.application.port.query.DataQueryRepositoryPort;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datatype.GetDataTypeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.user.FindUsernameUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        DataPopularSearchUseCase
{
    private final PopularDataSetsDtoMapper popularDataSetsDtoMapper;

    private final DataRepositoryPort dataRepositoryPort;
    private final DataSimilarSearchPort dataSimilarSearchPort;
    private final DataQueryRepositoryPort dataQueryRepositoryPort;

    private final FindUsernameUseCase findUsernameUseCase;
    private final GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;
    private final GetDataSourceLabelFromIdUseCase getDataSourceLabelFromIdUseCase;
    private final GetDataTypeLabelFromIdUseCase getDataTypeLabelFromIdUseCase;

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
     * 주어진 데이터 ID를 기반으로 유사한 데이터셋 목록을 조회합니다.
     *
     * @param dataId 유사 데이터셋을 찾을 기준이 되는 데이터의 ID
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

}
