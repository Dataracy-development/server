package com.dataracy.modules.project.adapter.jpa.repository;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface ProjectDataJpaRepository extends JpaRepository<ProjectDataEntity, Long> {
    @Query("SELECT pd.dataId FROM ProjectDataEntity pd WHERE pd.project.id = :projectId")
    Set<Long> findDataIdsByProjectId(@Param("projectId") Long projectId);

    /**
     * 특정 프로젝트 ID에 대해, 지정된 dataId 목록에 해당하는 연결만 삭제합니다.
     *
     * @param projectId 프로젝트 ID
     * @param dataIds 삭제할 dataId 목록
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ProjectDataEntity pd WHERE pd.project.id = :projectId AND pd.dataId IN :dataIds")
    void deleteByProjectIdAndDataIdIn(@Param("projectId") Long projectId, @Param("dataIds") Set<Long> dataIds);
}
