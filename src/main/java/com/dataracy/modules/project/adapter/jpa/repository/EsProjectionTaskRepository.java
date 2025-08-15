package com.dataracy.modules.project.adapter.jpa.repository;

import com.dataracy.modules.project.adapter.jpa.entity.EsProjectionTaskEntity;
import com.dataracy.modules.project.domain.enums.EsProjectionStatus;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EsProjectionTaskRepository extends JpaRepository<EsProjectionTaskEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2") // SKIP LOCKED (Hibernate)
    })
    @Query("""
        select t
          from EsProjectionTaskEntity t
         where t.status in :statuses
           and t.nextRunAt <= :now
         order by t.id
    """)
    List<EsProjectionTaskEntity> findBatchForWork(
            @Param("now") LocalDateTime now,
            @Param("statuses") List<EsProjectionStatus> statuses,
            Pageable pageable
    );
}
