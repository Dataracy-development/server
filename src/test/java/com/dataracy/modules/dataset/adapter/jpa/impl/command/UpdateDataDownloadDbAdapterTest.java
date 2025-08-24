package com.dataracy.modules.dataset.adapter.jpa.impl.command;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEntity;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(UpdateDataDownloadDbAdapter.class) // Adapter를 Spring Context에 등록
class UpdateDataDownloadDbAdapterTest {

    @Autowired
    private UpdateDataDownloadDbAdapter adapter;

    @Autowired
    private DataJpaRepository repo;

    @Test
    @DisplayName("increaseDownloadCount 호출 시 DB에서 downloadCount가 증가한다")
    void increaseDownloadCountShouldIncreaseDownloadCountInDb() {
        // given
        DataEntity entity = DataEntity.builder()
                .title("test data")
                .downloadCount(0)
                .build();
//        entity.setTitle("test data");
//        entity.setDownloadCount(0L);
        repo.saveAndFlush(entity);

        // when
        adapter.increaseDownloadCount(entity.getId());

        // then
        DataEntity updated = repo.findById(entity.getId()).orElseThrow();
        assertThat(updated.getDownloadCount()).isEqualTo(1L);
    }
}
