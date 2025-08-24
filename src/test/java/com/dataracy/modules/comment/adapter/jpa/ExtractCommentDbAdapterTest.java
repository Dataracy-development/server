package com.dataracy.modules.comment.adapter.jpa;

import com.dataracy.modules.comment.adapter.jpa.impl.query.ExtractCommentDbAdapter;
import com.dataracy.modules.comment.adapter.jpa.repository.CommentJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

class ExtractCommentDbAdapterTest {

    private CommentJpaRepository repo;
    private ExtractCommentDbAdapter adapter;

    @BeforeEach
    void setup() {
        repo = mock(CommentJpaRepository.class);
        adapter = new ExtractCommentDbAdapter(repo);
    }

    @Test
    @DisplayName("댓글 ID로 UserId 찾기 성공")
    void findUserIdByCommentIdSuccess() {
        given(repo.findUserIdById(1L)).willReturn(Optional.of(100L));

        Optional<Long> result = adapter.findUserIdByCommentId(1L);

        assertThat(result).contains(100L);
    }

    @Test
    @DisplayName("findUserIdByCommentId → 존재하지 않는 댓글이면 Optional.empty 반환")
    void findUserIdByCommentId_empty() {
        given(repo.findUserIdById(1L)).willReturn(Optional.empty());

        Optional<Long> result = adapter.findUserIdByCommentId(1L);

        assertThat(result).isEmpty();
    }
}
