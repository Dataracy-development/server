package com.dataracy.modules.reference.adapter.jpa.impl;

import com.dataracy.modules.reference.adapter.jpa.entity.TopicEntity;
import com.dataracy.modules.reference.adapter.jpa.repository.TopicJpaRepository;
import com.dataracy.modules.reference.domain.model.Topic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class TopicDbAdapterTest {

    @Mock
    private TopicJpaRepository topicJpaRepository;

    @InjectMocks
    private TopicDbAdapter adapter;

    @Test
    @DisplayName("토픽 전체 조회 성공")
    void findAllTopicsSuccess() {
        // given
        TopicEntity e1 = TopicEntity.builder().id(1L).value("v1").label("l1").build();
        TopicEntity e2 = TopicEntity.builder().id(2L).value("v2").label("l2").build();
        given(topicJpaRepository.findAll()).willReturn(List.of(e1, e2));

        // when
        List<Topic> result = adapter.findAllTopics();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).value()).isEqualTo("v1");
        assertThat(result.get(0).label()).isEqualTo("l1");
        then(topicJpaRepository).should().findAll();
    }

    @Test
    @DisplayName("토픽 단건 조회 성공 및 실패")
    void findTopicByIdSuccessAndEmpty() {
        // given
        Long id = 5L;
        TopicEntity entity = TopicEntity.builder().id(id).value("v").label("l").build();
        given(topicJpaRepository.findById(id)).willReturn(Optional.of(entity));
        given(topicJpaRepository.findById(999L)).willReturn(Optional.empty());

        // when
        Optional<Topic> found = adapter.findTopicById(id);
        Optional<Topic> missing = adapter.findTopicById(999L);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(id);
        assertThat(missing).isEmpty();
        then(topicJpaRepository).should(times(1)).findById(id);
        then(topicJpaRepository).should(times(1)).findById(999L);
    }

    @Test
    @DisplayName("토픽 존재 여부 확인 성공")
    void existsTopicByIdSuccess() {
        // given
        given(topicJpaRepository.existsById(1L)).willReturn(true);
        given(topicJpaRepository.existsById(2L)).willReturn(false);

        // when & then
        assertThat(adapter.existsTopicById(1L)).isTrue();
        assertThat(adapter.existsTopicById(2L)).isFalse();
        then(topicJpaRepository).should().existsById(1L);
        then(topicJpaRepository).should().existsById(2L);
    }

    @Test
    @DisplayName("토픽 라벨 단건 조회 성공 및 실패")
    void getLabelByIdSuccessAndEmpty() {
        // given
        given(topicJpaRepository.findLabelById(1L)).willReturn(Optional.of("L1"));
        given(topicJpaRepository.findLabelById(9L)).willReturn(Optional.empty());

        // when & then
        assertThat(adapter.getLabelById(1L)).contains("L1");
        assertThat(adapter.getLabelById(9L)).isEmpty();
        then(topicJpaRepository).should().findLabelById(1L);
        then(topicJpaRepository).should().findLabelById(9L);
    }

    @Test
    @DisplayName("토픽 라벨 다건 조회 성공")
    void getLabelsByIdsSuccess() {
        // given
        TopicEntity e1 = TopicEntity.builder().id(1L).value("v1").label("L1").build();
        TopicEntity e2 = TopicEntity.builder().id(2L).value("v2").label("L2").build();
        given(topicJpaRepository.findAllById(List.of(1L, 2L))).willReturn(List.of(e1, e2));

        // when
        Map<Long, String> result = adapter.getLabelsByIds(List.of(1L, 2L));

        // then
        assertThat(result).containsEntry(1L, "L1").containsEntry(2L, "L2");
        then(topicJpaRepository).should().findAllById(List.of(1L, 2L));
    }
}
