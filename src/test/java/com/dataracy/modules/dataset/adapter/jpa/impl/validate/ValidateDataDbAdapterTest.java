package com.dataracy.modules.dataset.adapter.jpa.impl.validate;

import com.dataracy.modules.dataset.adapter.jpa.repository.DataJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ValidateDataDbAdapterTest {

    @Mock
    private DataJpaRepository repo;

    @InjectMocks
    private ValidateDataDbAdapter adapter;

    @Nested
    @DisplayName("데이터셋 존재 검증")
    class ExistsDataSet {

        @Test
        @DisplayName("데이터가 존재하면 true 반환")
        void existsDataByIdShouldReturnTrueWhenExists() {
            // given
            given(repo.existsById(1L)).willReturn(true);

            // when
            boolean result = adapter.existsDataById(1L);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("데이터가 존재하지 않으면 false 반환")
        void existsDataByIdShouldReturnFalseWhenNotExists() {
            // given
            given(repo.existsById(2L)).willReturn(false);

            // when
            boolean result = adapter.existsDataById(2L);

            // then
            assertThat(result).isFalse();
        }
    }
}
