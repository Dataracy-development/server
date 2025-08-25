package com.dataracy.modules.reference.adapter.jpa.mapper;

import com.dataracy.modules.reference.adapter.jpa.entity.TopicEntity;
import com.dataracy.modules.reference.domain.model.Topic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TopicEntityMapperTest {

    @Test
    @DisplayName("toDomain: 성공 - 엔티티에서 도메인으로 변환")
    void toDomain_success() {
        // given
        TopicEntity entity = TopicEntity.builder().id(1L).value("v").label("l").build();

        // when
        Topic domain = TopicEntityMapper.toDomain(entity);

        // then
        assertThat(domain.id()).isEqualTo(1L);
        assertThat(domain.value()).isEqualTo("v");
        assertThat(domain.label()).isEqualTo("l");
    }

    @Test
    @DisplayName("toEntity: 성공 - 도메인에서 엔티티로 변환 (id 제외)")
    void toEntity_success() {
        // given
        Topic domain = new Topic(1L, "v", "l");

        // when
        TopicEntity entity = TopicEntityMapper.toEntity(domain);

        // then
        assertThat(entity.getId()).isNull(); // of(value,label)로 생성되어 id는 null
        assertThat(entity.getValue()).isEqualTo("v");
        assertThat(entity.getLabel()).isEqualTo("l");
    }

    @Test
    @DisplayName("null 입력 처리 - null 반환")
    void nullInputs_returnNull() {
        assertThat(TopicEntityMapper.toDomain(null)).isNull();
        assertThat(TopicEntityMapper.toEntity(null)).isNull();
    }
}
