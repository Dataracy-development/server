package com.dataracy.modules.comment.adapter.jpa;

import com.dataracy.modules.comment.adapter.jpa.impl.command.UpdateCommentLikeDbAdapter;
import com.dataracy.modules.comment.adapter.jpa.repository.CommentJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.BDDMockito.*;

class UpdateCommentLikeDbAdapterTest {

    private CommentJpaRepository repo;
    private UpdateCommentLikeDbAdapter adapter;

    @BeforeEach
    void setup() {
        repo = mock(CommentJpaRepository.class);
        adapter = new UpdateCommentLikeDbAdapter(repo);
    }

    @Test
    @DisplayName("좋아요 증가 성공")
    void increaseLikeCountSuccess() {
        // given
        willDoNothing().given(repo).increaseLikeCount(1L);

        // when
        adapter.increaseLikeCount(1L);

        // then
        then(repo).should().increaseLikeCount(1L);
    }

    @Test
    @DisplayName("좋아요 감소 성공")
    void decreaseLikeCountSuccess() {
        // given
        willDoNothing().given(repo).decreaseLikeCount(1L);

        // when
        adapter.decreaseLikeCount(1L);

        // then
        then(repo).should().decreaseLikeCount(1L);
    }
}
