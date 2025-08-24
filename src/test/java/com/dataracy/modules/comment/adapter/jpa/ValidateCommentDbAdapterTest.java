package com.dataracy.modules.comment.adapter.jpa;

import com.dataracy.modules.comment.adapter.jpa.impl.validate.ValidateCommentDbAdapter;
import com.dataracy.modules.comment.adapter.jpa.repository.CommentJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

class ValidateCommentDbAdapterTest {

    private CommentJpaRepository repo;
    private ValidateCommentDbAdapter adapter;

    @BeforeEach
    void setup() {
        repo = mock(CommentJpaRepository.class);
        adapter = new ValidateCommentDbAdapter(repo);
    }

    @Test
    @DisplayName("댓글 존재 O → true 반환")
    void existsByCommentIdSuccessTrue() {
        // given
        given(repo.existsById(1L)).willReturn(true);

        // when
        boolean result = adapter.existsByCommentId(1L);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("댓글 존재 X → false 반환")
    void existsByCommentIdSuccessFalse() {
        // given
        given(repo.existsById(1L)).willReturn(false);

        // when
        boolean result = adapter.existsByCommentId(1L);

        // then
        assertThat(result).isFalse();
    }
}
