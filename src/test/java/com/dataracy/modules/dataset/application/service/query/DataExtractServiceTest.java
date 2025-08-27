package com.dataracy.modules.dataset.application.service.query;

import com.dataracy.modules.dataset.application.port.out.query.extractor.ExtractDataOwnerPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DataExtractServiceTest {

    @InjectMocks
    private DataExtractService service;

    @Mock
    private ExtractDataOwnerPort port;

    @Nested
    @DisplayName("데이터셋 업로드")
    class ExtractUploaderId {

        @Test
        @DisplayName("데이터 존재 → userId 반환")
        void findUserIdByDataIdSuccess() {
            // given
            given(port.findUserIdByDataId(1L))
                    .willReturn(99L);

            // when
            Long res = service.findUserIdByDataId(1L);

            // then
            assertThat(res).isEqualTo(99L);
        }

        @Test
        @DisplayName("삭제 포함 데이터 조회 → userId 반환")
        void findUserIdIncludingDeletedSuccess() {
            // given
            given(port.findUserIdIncludingDeleted(1L))
                    .willReturn(77L);

            // when
            Long res = service.findUserIdIncludingDeleted(1L);

            // then
            assertThat(res).isEqualTo(77L);
        }
    }
}
