package com.dataracy.modules.project.adapter.jpa.repository;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectEsProjectionTaskEntity;
import com.dataracy.modules.project.domain.enums.ProjectEsProjectionType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProjectEsProjectionTaskRepository extends JpaRepository<ProjectEsProjectionTaskEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2") // SKIP LOCKED (Hibernate)
    })
    @Query("""
        select t
          from ProjectEsProjectionTaskEntity t
         where t.status in :statuses
           and t.nextRunAt <= :now
         order by t.nextRunAt ASC, t.id ASC
    """)
    List<ProjectEsProjectionTaskEntity> findBatchForWork(
            @Param("now") LocalDateTime now,
            @Param("statuses") List<ProjectEsProjectionType> statuses,
            Pageable pageable
    );
}
