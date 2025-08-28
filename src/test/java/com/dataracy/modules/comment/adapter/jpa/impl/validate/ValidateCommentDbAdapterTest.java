package com.dataracy.modules.comment.adapter.jpa.impl.validate;

import com.dataracy.modules.comment.adapter.jpa.repository.CommentJpaRepository;
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
class ValidateCommentDbAdapterTest {

    @Mock
    private CommentJpaRepository repo;

    @InjectMocks
    private ValidateCommentDbAdapter adapter;

    @Nested
    @DisplayName("댓글 존재 검증")
    class ExistsComment {

        @Test
        @DisplayName("댓글 존재 O → true 반환")
        void existsByCommentIdShouldReturnTrue() {
            // given
            given(repo.existsById(1L)).willReturn(true);

            // when
            boolean result = adapter.existsByCommentId(1L);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("댓글 존재 X → false 반환")
        void existsByCommentIdShouldReturnFalse() {
            // given
            given(repo.existsById(1L)).willReturn(false);

            // when
            boolean result = adapter.existsByCommentId(1L);

            // then
            assertThat(result).isFalse();
        }
    }
}
