package com.dataracy.modules.project.adapter.jpa.repository;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProjectJpaRepository extends JpaRepository<ProjectEntity, Long> {
    /**
     * 지정한 프로젝트 ID에 해당하는 프로젝트 엔티티를 삭제 여부와 상관없이 조회합니다.
     *
     * @param projectId 조회할 프로젝트의 ID
     * @return 해당 프로젝트가 존재하면 Optional에 담아 반환하며, 없으면 빈 Optional을 반환합니다.
     */
    @Query(value = "SELECT * FROM project WHERE project_id = :projectId", nativeQuery = true) // @Where 무시됨
    Optional<ProjectEntity> findIncludingDeleted(@Param("projectId") Long projectId);

    @Modifying
    @Query("UPDATE ProjectEntity p SET p.viewCount = p.viewCount + :count WHERE p.id = :projectId")
    void increaseViewCount(@Param("projectId") Long projectId, @Param("count") Long count);
}
