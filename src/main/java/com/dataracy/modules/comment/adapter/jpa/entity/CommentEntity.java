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

    /**
     * 댓글의 내용을 새로운 값으로 수정합니다.
     *
     * @param content 변경할 댓글 내용
     */
    public void modifyContent(String content) {
        this.content = content;
    }

    /**
     * 이 댓글이 답글(부모 댓글이 있는 경우)인지 여부를 반환합니다.
     *
     * @return 부모 댓글 ID가 존재하면 true, 없으면 false
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
     * 댓글의 좋아요 수를 1 감소시킵니다.
     * 좋아요 수는 0보다 작아지지 않습니다.
     */
    public void decreaseLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }

    /**
     * 지정된 프로젝트 ID, 사용자 ID, 댓글 내용, 부모 댓글 ID로 새로운 CommentEntity 인스턴스를 생성합니다.
     *
     * @param projectId        댓글이 속한 프로젝트의 ID
     * @param userId           댓글을 작성한 사용자의 ID
     * @param content          댓글 내용
     * @param parentCommentId  부모 댓글의 ID (대댓글이 아닌 경우 null)
     * @return                 생성된 CommentEntity 인스턴스
     */
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
