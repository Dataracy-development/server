package com.dataracy.modules.comment.adapter.persistence.entity;

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
    private Boolean isDeleted;

    // 대댓글 관계 (자기 자신을 부모로 가짐)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CommentEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<CommentEntity> children = new ArrayList<>();

    // 답글 여부
    public boolean isReply() {
        return parent != null;
    }

    public static CommentEntity toEntity(
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
