package com.dataracy.modules.project.adapter.jpa.repository;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProjectJpaRepository extends JpaRepository<ProjectEntity, Long> {
    /**
     * 삭제 여부와 관계없이 지정한 프로젝트 ID에 해당하는 프로젝트 엔티티를 조회합니다.
     *
     * @param projectId 조회할 프로젝트의 ID
     * @return 프로젝트가 존재하면 해당 엔티티를 Optional로 반환하며, 없으면 빈 Optional을 반환합니다.
     */
    @Query(value = "SELECT * FROM project WHERE project_id = :projectId", nativeQuery = true) // @Where 무시됨
    Optional<ProjectEntity> findIncludingDeleted(@Param("projectId") Long projectId);

    /**
     * 지정된 프로젝트의 조회수를 주어진 값만큼 증가시킵니다.
     *
     * @param projectId 조회수를 증가시킬 프로젝트의 ID
     * @param count 증가시킬 조회수 값
     */
    @Modifying
    @Query("UPDATE ProjectEntity p SET p.viewCount = p.viewCount + :count WHERE p.id = :projectId")
    void increaseViewCount(@Param("projectId") Long projectId, @Param("count") Long count);

    /**
     * 지정된 프로젝트의 댓글 수를 1 증가시킵니다.
     *
     * @param projectId 댓글 수를 증가시킬 프로젝트의 ID
     */
    @Modifying
    @Query("UPDATE ProjectEntity p SET p.commentCount = p.commentCount + 1 WHERE p.id = :projectId")
    void increaseCommentCount(@Param("projectId") Long projectId);

    /**
     * 지정된 프로젝트의 댓글 수를 1 감소시킵니다. 댓글 수는 0 미만으로 내려가지 않습니다.
     *
     * @param projectId 댓글 수를 감소시킬 프로젝트의 ID
     */
    @Modifying
    @Query("""
    UPDATE ProjectEntity p 
    SET p.commentCount = 
        CASE 
            WHEN p.commentCount > 0 THEN p.commentCount - 1 
            ELSE 0 
        END
    WHERE p.id = :projectId
""")
    void decreaseCommentCount(@Param("projectId") Long projectId);

    @Modifying
    @Query("UPDATE ProjectEntity p SET p.likeCount = p.likeCount + 1 WHERE p.id = :projectId")
    void increaseLikeCount(@Param("projectId") Long projectId);

    @Modifying
    @Query("""
    UPDATE ProjectEntity p 
    SET p.likeCount = 
        CASE 
            WHEN p.likeCount > 0 THEN p.likeCount - 1 
            ELSE 0 
        END
    WHERE p.id = :projectId
""")
    void decreaseLikeCount(@Param("projectId") Long projectId);
}
