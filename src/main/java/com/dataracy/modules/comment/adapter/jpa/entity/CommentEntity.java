package com.dataracy.modules.comment.adapter.jpa.entity;

import com.dataracy.modules.common.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    // 대댓글 관계 (자기 자신을 부모로 가짐)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CommentEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<CommentEntity> children = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private Long likeCount = 0L;

    /**
     * 이 댓글이 답글(부모 댓글이 있는 경우)인지 여부를 반환합니다.
     *
     * @return 부모 댓글이 존재하면 true, 그렇지 않으면 false
     */
    public boolean isReply() {
        return parent != null;
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

    /**
     * 주어진 값들로 새로운 CommentEntity 인스턴스를 생성합니다.
     *
     * @param id 댓글의 고유 식별자
     * @param projectId 댓글이 속한 프로젝트의 식별자
     * @param userId 댓글 작성자의 식별자
     * @param content 댓글 내용
     * @param parent 부모 댓글 엔티티(대댓글일 경우), 없으면 null
     * @return 생성된 CommentEntity 인스턴스
     */
    public static CommentEntity of(
            Long id,
            Long projectId,
            Long userId,
            String content,
            CommentEntity parent
    ) {
        return CommentEntity.builder()
                .id(id)
                .projectId(projectId)
                .userId(userId)
                .content(content)
                .parent(parent)
                .build();
    }
}
