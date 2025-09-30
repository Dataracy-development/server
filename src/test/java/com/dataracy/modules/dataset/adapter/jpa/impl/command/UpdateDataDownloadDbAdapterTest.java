package com.dataracy.modules.dataset.adapter.jpa.impl.command;

import com.dataracy.modules.dataset.adapter.jpa.repository.DataJpaRepository;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UpdateDataDownloadDbAdapterTest {

    @Mock
    private DataJpaRepository repo;

    @InjectMocks
    private UpdateDataDownloadDbAdapter adapter;

    @Test
    @DisplayName("increaseDownloadCount 호출 시 repo.increaseDownload가 실행된다")
    void increaseDownloadCountShouldCallRepoMethod() {
        // given
        Long dataId = 1L;
        willDoNothing().given(repo).increaseDownload(dataId);

        // when
        adapter.increaseDownloadCount(dataId);

        // then
        then(repo).should().increaseDownload(eq(dataId));
    }

    @Test
    @DisplayName("increaseDownloadCount 호출 시 대상이 없으면 예외 발생 → NOT_FOUND_DATA")
    void increaseDownloadCountShouldThrowWhenNotFound() {
        // given
        Long dataId = 999L;
        willThrow(new  DataException(DataErrorStatus.NOT_FOUND_DATA))
                .given(repo).increaseDownload(dataId);

        // when & then
        DataException ex = catchThrowableOfType(
                () -> adapter.increaseDownloadCount(dataId),
                DataException.class
        );

        // then
        assertThat(ex.getErrorCode()).isEqualTo(DataErrorStatus.NOT_FOUND_DATA);
    }
}
