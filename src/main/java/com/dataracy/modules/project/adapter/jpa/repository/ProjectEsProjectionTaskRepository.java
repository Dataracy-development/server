package com.dataracy.modules.project.adapter.jpa.repository;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectEsProjectionTaskEntity;
import com.dataracy.modules.project.domain.enums.ProjectEsProjectionType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProjectEsProjectionTaskRepository extends JpaRepository<ProjectEsProjectionTaskEntity, Long> {
    /**
     * 지정된 상태이고 nextRunAt이 주어진 시점 이전(이하)인 프로젝션 작업을 페이징 단위로 페시미스틱 쓰기 잠금하여 조회한다.
     *
     * 조회 시 선택된 행에는 PESSIMISTIC_WRITE 잠금을 적용하고, 다른 트랜잭션에 의해 잠긴 행은 건너뛴다 (JPA 힌트: SKIP LOCKED).
     * 결과는 nextRunAt 오름차순, 동일 시각일 경우 id 오름차순으로 정렬된다.
     *
     * @param now 조회 기준 시각 (nextRunAt <= now 인 항목을 대상으로 함)
     * @param statuses 포함할 상태들의 리스트
     * @param pageable 페이지 처리 및 추가 정렬/제한 정보
     * @return 조건에 맞는 ProjectEsProjectionTaskEntity의 리스트 (일치하는 항목이 없으면 빈 리스트)
     */
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

    /**
     * 지정한 ID를 가진 ProjectEsProjectionTaskEntity를 즉시 삭제합니다.
     *
     * 삭제 쿼리를 직접 실행하며, 실행 후 영속성 컨텍스트를 플러시하고(clearAutomatically=false가 아님) 자동으로 초기화합니다.
     * 일치하는 엔티티가 없으면 아무것도 삭제되지 않습니다.
     *
     * @param id 삭제할 엔티티의 식별자
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from ProjectEsProjectionTaskEntity t where t.id = :id")
    void deleteImmediate(@Param("id") Long id);
}
