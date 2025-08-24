package com.dataracy.modules.dataset.adapter.jpa.impl.validate;

import com.dataracy.modules.dataset.adapter.jpa.repository.DataJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidateDataDbAdapterTest {

    @Mock
    private DataJpaRepository repo;

    @InjectMocks
    private ValidateDataDbAdapter adapter;

    @Test
    void existsDataByIdShouldReturnTrueWhenExists() {
        // given
        when(repo.existsById(1L)).thenReturn(true);

        // when
        boolean result = adapter.existsDataById(1L);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void existsDataByIdShouldReturnFalseWhenNotExists() {
        // given
        when(repo.existsById(2L)).thenReturn(false);

        // when
        boolean result = adapter.existsDataById(2L);

        // then
        assertThat(result).isFalse();
    }
}
