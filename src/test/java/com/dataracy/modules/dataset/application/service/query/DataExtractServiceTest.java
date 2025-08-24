package com.dataracy.modules.dataset.application.service.query;

import com.dataracy.modules.dataset.application.port.out.query.extractor.ExtractDataOwnerPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class DataExtractServiceTest {

    @InjectMocks
    private DataExtractService service;

    @Mock private ExtractDataOwnerPort port;

    @Test
    void findUserIdByDataIdShouldReturnValue() {
        given(port.findUserIdByDataId(1L)).willReturn(99L);
        Long res = service.findUserIdByDataId(1L);
        assertThat(res).isEqualTo(99L);
    }

    @Test
    void findUserIdIncludingDeletedShouldReturnValue() {
        given(port.findUserIdIncludingDeleted(1L)).willReturn(77L);
        Long res = service.findUserIdIncludingDeleted(1L);
        assertThat(res).isEqualTo(77L);
    }
}
