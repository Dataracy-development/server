package com.dataracy.modules.dataset.application.service.query;

import com.dataracy.modules.dataset.application.dto.response.support.DataLabelMapResponse;
import com.dataracy.modules.dataset.application.dto.response.support.DataWithProjectCountDto;
import com.dataracy.modules.dataset.domain.model.Data;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class DataLabelMapServiceTest {

    @InjectMocks
    private DataLabelMapService service;

    @Mock private com.dataracy.modules.user.application.port.in.query.extractor.FindUsernameUseCase userCase;
    @Mock private com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase topicCase;
    @Mock private com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase dsCase;
    @Mock private com.dataracy.modules.reference.application.port.in.datatype.GetDataTypeLabelFromIdUseCase dtCase;

    @Test
    void labelMappingShouldReturnCorrectMaps() {
        Data d = Data.of(1L,"t",2L,3L,4L,5L,null,null,"d","g","f","t",1,1L,null, LocalDateTime.now());
        DataWithProjectCountDto dto = new DataWithProjectCountDto(d,2L);

        given(userCase.findUsernamesByIds(any())).willReturn(Map.of(3L,"u"));
        given(topicCase.getLabelsByIds(any())).willReturn(Map.of(2L,"topic"));
        given(dsCase.getLabelsByIds(any())).willReturn(Map.of(4L,"ds"));
        given(dtCase.getLabelsByIds(any())).willReturn(Map.of(5L,"dt"));

        DataLabelMapResponse res = service.labelMapping(List.of(dto));

        assertThat(res.usernameMap().get(3L)).isEqualTo("u");
    }
}
