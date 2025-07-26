package com.dataracy.modules.comment.adapter.jpa.entity;

import com.dataracy.modules.common.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * comment 테이블
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(
        name = "comment"
)
public class CommentEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false)
    private Long projectId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String content;

    @Column
    private Long parentCommentId;

    @Column(nullable = false)
    @Builder.Default
    private Long likeCount = 0L;

    public void modifyContent(String content) {
        this.content = content;
    }

    /**
     * 이 댓글이 답글(부모 댓글이 있는 경우)인지 여부를 반환합니다.
     *
     * @return 부모 댓글이 존재하면 true, 그렇지 않으면 false
     */
    public boolean isReply() {
        return parentCommentId != null;
    }

    /**
     * 댓글의 좋아요 수를 1 증가시킵니다.
     */
    public void increaseLikeCount() {
        this.likeCount++;
    }
    /**
     * 댓글의 좋아요 수를 1 감소시킵니다. 좋아요 수는 0 미만으로 내려가지 않습니다.
     */
    public void decreaseLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }

    public static CommentEntity of(
            Long projectId,
            Long userId,
            String content,
            Long parentCommentId
    ) {
        return CommentEntity.builder()
                .projectId(projectId)
                .userId(userId)
                .content(content)
                .parentCommentId(parentCommentId)
                .build();
    }
}
