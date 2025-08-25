package com.dataracy.modules.reference.adapter.jpa.impl;

import com.dataracy.modules.reference.adapter.jpa.entity.AuthorLevelEntity;
import com.dataracy.modules.reference.adapter.jpa.repository.AuthorLevelJpaRepository;
import com.dataracy.modules.reference.domain.model.AuthorLevel;
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
class AuthorLevelDbAdapterTest {

    @Mock AuthorLevelJpaRepository authorlevelJpaRepository;

    @InjectMocks AuthorLevelDbAdapter adapter;

    @Test
    @DisplayName("findAllAuthorLevels: 성공 - 엔티티를 도메인으로 변환하여 반환")
    void findAllAuthorLevels_success() {
        // given
        AuthorLevelEntity e1 = AuthorLevelEntity.builder().id(1L).value("v1").label("l1").build();
        AuthorLevelEntity e2 = AuthorLevelEntity.builder().id(2L).value("v2").label("l2").build();
        given(authorlevelJpaRepository.findAll()).willReturn(List.of(e1, e2));

        // when
        List<AuthorLevel> result = adapter.findAllAuthorLevels();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).value()).isEqualTo("v1");
        assertThat(result.get(0).label()).isEqualTo("l1");
        then(authorlevelJpaRepository).should().findAll();
    }

    @Test
    @DisplayName("findAuthorLevelById: 성공/실패 - 존재 시 변환, 없으면 빈 Optional")
    void findAuthorLevelById_success_and_empty() {
        // given
        Long id = 5L;
        AuthorLevelEntity entity = AuthorLevelEntity.builder().id(id).value("v").label("l").build();
        given(authorlevelJpaRepository.findById(id)).willReturn(Optional.of(entity));
        given(authorlevelJpaRepository.findById(999L)).willReturn(Optional.empty());

        // when
        Optional<AuthorLevel> found = adapter.findAuthorLevelById(id);
        Optional<AuthorLevel> missing = adapter.findAuthorLevelById(999L);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(id);
        assertThat(missing).isEmpty();
        then(authorlevelJpaRepository).should(times(1)).findById(id);
        then(authorlevelJpaRepository).should(times(1)).findById(999L);
    }

    @Test
    @DisplayName("existsAuthorLevelById: 성공 - JPA existsById 위임")
    void existsAuthorLevelById_success() {
        // given
        given(authorlevelJpaRepository.existsById(1L)).willReturn(true);
        given(authorlevelJpaRepository.existsById(2L)).willReturn(false);

        // when/then
        assertThat(adapter.existsAuthorLevelById(1L)).isTrue();
        assertThat(adapter.existsAuthorLevelById(2L)).isFalse();
        then(authorlevelJpaRepository).should().existsById(1L);
        then(authorlevelJpaRepository).should().existsById(2L);
    }

    @Test
    @DisplayName("getLabelById: 성공/실패 - Optional 위임")
    void getLabelById_success_and_empty() {
        // given
        given(authorlevelJpaRepository.findLabelById(1L)).willReturn(Optional.of("L1"));
        given(authorlevelJpaRepository.findLabelById(9L)).willReturn(Optional.empty());

        // when/then
        assertThat(adapter.getLabelById(1L)).contains("L1");
        assertThat(adapter.getLabelById(9L)).isEmpty();
        then(authorlevelJpaRepository).should().findLabelById(1L);
        then(authorlevelJpaRepository).should().findLabelById(9L);
    }

    @Test
    @DisplayName("getLabelsByIds: 성공 - findAllById 결과를 id->label 맵으로 반환")
    void getLabelsByIds_success() {
        // given
        AuthorLevelEntity e1 = AuthorLevelEntity.builder().id(1L).value("v1").label("L1").build();
        AuthorLevelEntity e2 = AuthorLevelEntity.builder().id(2L).value("v2").label("L2").build();
        given(authorlevelJpaRepository.findAllById(List.of(1L,2L))).willReturn(List.of(e1,e2));

        // when
        Map<Long,String> result = adapter.getLabelsByIds(List.of(1L,2L));

        // then
        assertThat(result).containsEntry(1L,"L1").containsEntry(2L,"L2");
        then(authorlevelJpaRepository).should().findAllById(List.of(1L,2L));
    }
}
