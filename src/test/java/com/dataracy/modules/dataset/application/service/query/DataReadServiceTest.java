package com.dataracy.modules.dataset.application.service.query;

import com.dataracy.modules.dataset.application.dto.response.read.*;
import com.dataracy.modules.dataset.application.dto.response.support.DataLabelMapResponse;
import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;
import com.dataracy.modules.dataset.application.mapper.read.DataReadDtoMapper;
import com.dataracy.modules.dataset.application.port.in.query.read.FindDataLabelMapUseCase;
import com.dataracy.modules.dataset.application.port.out.query.read.*;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.model.Data;
import com.dataracy.modules.dataset.domain.model.DataMetadata;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datatype.GetDataTypeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.GetOccupationLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.GetUserInfoUseCase;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.vo.UserInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class DataReadServiceTest {

    @InjectMocks
    private DataReadService service;

    @Mock private DataReadDtoMapper mapper;
    @Mock private GetPopularDataSetsPort getPopularDataSetsPort;
    @Mock private GetRecentDataSetsPort getRecentDataSetsPort;
    @Mock private FindDataWithMetadataPort findDataWithMetadataPort;
    @Mock private GetDataGroupCountPort getDataGroupCountPort;
    @Mock private GetConnectedDataSetsPort getConnectedDataSetsPort;
    @Mock private GetUserInfoUseCase getUserInfoUseCase;
    @Mock private GetTopicLabelFromIdUseCase topicUseCase;
    @Mock private GetDataSourceLabelFromIdUseCase dsUseCase;
    @Mock private GetDataTypeLabelFromIdUseCase dtUseCase;
    @Mock private GetAuthorLevelLabelFromIdUseCase authorUseCase;
    @Mock private GetOccupationLabelFromIdUseCase occUseCase;
    @Mock private FindDataLabelMapUseCase labelMapUseCase;

    private Data sample() {
        return Data.of(1L,"t",2L,3L,4L,5L,
                LocalDate.now(),LocalDate.now(),"d","g","f","thumb",
                1,10L, DataMetadata.of(1L,1,1,"{}"), LocalDateTime.now());
    }

    @Test
    void getPopularDataSetsSuccess() {
        DataWithProjectCountDto dto = new DataWithProjectCountDto(sample(), 2L);
        given(getPopularDataSetsPort.getPopularDataSets(3)).willReturn(List.of(dto));
        given(labelMapUseCase.labelMapping(any())).willReturn(new DataLabelMapResponse(
                java.util.Map.of(3L,"user"), java.util.Map.of(2L,"topic"),
                java.util.Map.of(4L,"ds"), java.util.Map.of(5L,"dt")
        ));
        PopularDataResponse mockRes = new PopularDataResponse(1L,"t","u","topic","ds","dt",null,null,"d","thumb",1,10L,1,1, LocalDateTime.now(),2L);
        given(mapper.toResponseDto(any(), any(), any(), any(), any(), any())).willReturn(mockRes);

        List<PopularDataResponse> result = service.getPopularDataSets(3);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("t");
    }

    @Test
    void getDataDetailThrowsWhenNotFound() {
        given(findDataWithMetadataPort.findDataWithMetadataById(1L)).willReturn(Optional.empty());

        DataException ex = catchThrowableOfType(() -> service.getDataDetail(1L), DataException.class);

        assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
    }

    @Test
    void getDataDetailSuccess() {
        Data d = sample();
        given(findDataWithMetadataPort.findDataWithMetadataById(1L)).willReturn(Optional.of(d));

        UserInfo info = new UserInfo(
                3L,
                RoleType.ROLE_USER,
                "e",
                "nick",
                1L,
                2L,
                List.of(),
                null,
                "p",
                "intro"
        );
        given(getUserInfoUseCase.extractUserInfo(3L)).willReturn(info);

        given(topicUseCase.getLabelById(any())).willReturn("topic");
        given(dsUseCase.getLabelById(any())).willReturn("ds");
        given(dtUseCase.getLabelById(any())).willReturn("dt");
        given(mapper.toResponseDto(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .willReturn(new DataDetailResponse(
                        1L, "t", "nick", "p", "intro", "a", "o",
                        "topic", "ds", "dt",
                        null, null, "d", "g", "thumb", 1, 10L, 1, 1,
                        "{}", LocalDateTime.now()
                ));

        DataDetailResponse res = service.getDataDetail(1L);

        assertThat(res.username()).isEqualTo("nick");
    }


    @Test
    void getRecentDataSetsSuccess() {
        given(getRecentDataSetsPort.getRecentDataSets(2)).willReturn(List.of(sample()));
        RecentMinimalDataResponse r = new RecentMinimalDataResponse(1L,"t","thumb", LocalDateTime.now());
        given(mapper.toResponseDto(any(Data.class))).willReturn(r);

        List<RecentMinimalDataResponse> list = service.getRecentDataSets(2);

        assertThat(list).hasSize(1);
    }

    @Test
    void getDataGroupCountSuccess() {
        given(getDataGroupCountPort.getDataGroupCount()).willReturn(List.of(new DataGroupCountResponse(1L,"t",2L)));
        List<DataGroupCountResponse> res = service.getDataGroupCountByTopicLabel();
        assertThat(res).hasSize(1);
    }

    @Test
    void findConnectedDataSetsAssociatedWithProjectSuccess() {
        DataWithProjectCountDto dto = new DataWithProjectCountDto(sample(), 1L);
        Page<DataWithProjectCountDto> page = new PageImpl<>(List.of(dto));
        given(getConnectedDataSetsPort.getConnectedDataSetsAssociatedWithProject(eq(99L), any())).willReturn(page);
        given(labelMapUseCase.labelMapping(any())).willReturn(new DataLabelMapResponse(java.util.Map.of(),java.util.Map.of(),java.util.Map.of(),java.util.Map.of()));
        ConnectedDataResponse res = new ConnectedDataResponse(1L,"t","topic","dt",null,null,"thumb",1,10L,1,1, LocalDateTime.now(),1L);
        given(mapper.toResponseDto(any(),any(),any(),any())).willReturn(res);

        Page<ConnectedDataResponse> result = service.findConnectedDataSetsAssociatedWithProject(99L, PageRequest.of(0,10));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void findDataSetsByIdsEmptyInput() {
        List<ConnectedDataResponse> res = service.findDataSetsByIds(List.of());
        assertThat(res).isEmpty();
    }
}
