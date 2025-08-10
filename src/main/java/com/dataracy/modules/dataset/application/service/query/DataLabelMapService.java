package com.dataracy.modules.dataset.application.service.query;

import com.dataracy.modules.dataset.application.dto.response.support.DataLabelMapResponse;
import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;
import com.dataracy.modules.dataset.application.port.in.query.read.FindDataLabelMapUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datatype.GetDataTypeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUsernameUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataLabelMapService implements FindDataLabelMapUseCase {
    private final FindUsernameUseCase findUsernameUseCase;

    private final GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;
    private final GetDataSourceLabelFromIdUseCase getDataSourceLabelFromIdUseCase;
    private final GetDataTypeLabelFromIdUseCase getDataTypeLabelFromIdUseCase;

    /**
     * 데이터셋 DTO 컬렉션에서 사용자, 토픽, 데이터 소스, 데이터 타입의 ID를 추출하여 각 ID에 해당하는 레이블 매핑 정보를 조회합니다.
     *
     * @param savedDataSets 프로젝트 개수가 포함된 데이터셋 DTO 컬렉션
     * @return 사용자명, 토픽 레이블, 데이터 소스 레이블, 데이터 타입 레이블의 매핑 정보를 포함하는 응답 객체
     */
    @Transactional(readOnly = true)
    public DataLabelMapResponse labelMapping(Collection<DataWithProjectCountDto> savedDataSets) {
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

        return new DataLabelMapResponse(
                findUsernameUseCase.findUsernamesByIds(userIds),
                getTopicLabelFromIdUseCase.getLabelsByIds(topicIds),
                getDataSourceLabelFromIdUseCase.getLabelsByIds(dataSourceIds),
                getDataTypeLabelFromIdUseCase.getLabelsByIds(dataTypeIds)
        );
    }
}
