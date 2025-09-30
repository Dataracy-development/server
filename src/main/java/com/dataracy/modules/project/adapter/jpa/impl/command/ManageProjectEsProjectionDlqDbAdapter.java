package com.dataracy.modules.project.adapter.jpa.impl.command;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectEsProjectionDlqEntity;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectEsProjectionDlqRepository;
import com.dataracy.modules.project.application.port.out.command.projection.ManageProjectProjectionDlqPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManageProjectEsProjectionDlqDbAdapter implements ManageProjectProjectionDlqPort {
    private final ProjectEsProjectionDlqRepository dlqRepo;

    /**
     * 프로젝트 관련 ES 프로젝션의 DLQ(Dead Letter Queue) 항목을 DB에 저장한다.
     *
     * 주어진 프로젝트 ID와 변경량(delta) 정보, 삭제 플래그 및 마지막 에러 메시지를 포함하는
     * ProjectEsProjectionDlqEntity를 생성하여 저장소에 영구화한다.
     *
     * @param projectId   대상 프로젝트의 식별자
     * @param deltaComment 댓글 변경량(없을 경우 null)
     * @param deltaLike    좋아요 변경량(없을 경우 null)
     * @param deltaView    조회수 변경량(없을 경우 null)
     * @param setDeleted   프로젝션을 삭제 상태로 설정할지 여부(없을 경우 null)
     * @param lastError    마지막 처리 실패 시점의 에러 메시지(없을 경우 null)
     */
    @Override
    public void save(
            Long projectId,
            Integer deltaComment,
            Integer deltaLike,
            Long deltaView,
            Boolean setDeleted,
            String lastError
    ) {
        dlqRepo.save(ProjectEsProjectionDlqEntity.builder()
                .projectId(projectId)
                .deltaComment(deltaComment)
                .deltaLike(deltaLike)
                .deltaView(deltaView)
                .setDeleted(setDeleted)
                .lastError(lastError)
                .build());
    }
}
