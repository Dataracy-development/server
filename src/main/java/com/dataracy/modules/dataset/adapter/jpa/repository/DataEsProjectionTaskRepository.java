package com.dataracy.modules.dataset.adapter.jpa.repository;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEsProjectionTaskEntity;
import com.dataracy.modules.dataset.domain.enums.DataEsProjectionStatus;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface DataEsProjectionTaskRepository extends JpaRepository<DataEsProjectionTaskEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2")) // SKIP LOCKED
    @Query("""
        select t
          from DataEsProjectionTaskEntity t
         where t.status in :statuses
           and t.nextRunAt <= :now
         order by t.id
    """)
    List<DataEsProjectionTaskEntity> findBatchForWork(
            @Param("now") LocalDateTime now,
            @Param("statuses") List<DataEsProjectionStatus> statuses,
            Pageable pageable
    );
}
