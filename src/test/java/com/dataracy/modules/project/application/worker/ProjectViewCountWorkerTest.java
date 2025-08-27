package com.dataracy.modules.project.application.worker;

import com.dataracy.modules.project.application.port.out.command.projection.ManageProjectProjectionTaskPort;
import com.dataracy.modules.project.application.port.out.command.update.UpdateProjectViewPort;
import com.dataracy.modules.project.application.port.out.view.ManageProjectViewCountPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ProjectViewCountWorkerTest {

    @Mock
    private ManageProjectViewCountPort manageProjectViewCountPort;

    @Mock
    private UpdateProjectViewPort updateProjectViewDbPort;

    @Mock
    private ManageProjectProjectionTaskPort manageProjectProjectionTaskPort;

    @InjectMocks
    private ProjectViewCountWorker worker;

    @Test
    @DisplayName("프로젝트 조회수 집계 성공 - 양수 카운트만 반영")
    void flushProjectViewsSuccessWithPositiveCounts() {
        // given
        Set<String> keys = new LinkedHashSet<>();
        keys.add("viewCount:PROJECT:10");
        keys.add("viewCount:PROJECT:11");

        given(manageProjectViewCountPort.getAllViewCountKeys("PROJECT")).willReturn(keys);
        given(manageProjectViewCountPort.popViewCount(10L, "PROJECT")).willReturn(5L);
        given(manageProjectViewCountPort.popViewCount(11L, "PROJECT")).willReturn(2L);

        // when & then
        assertThatNoException().isThrownBy(() -> worker.flushProjectViews());

        // then
        then(updateProjectViewDbPort).should().increaseViewCount(10L, 5L);
        then(updateProjectViewDbPort).should().increaseViewCount(11L, 2L);
        then(manageProjectProjectionTaskPort).should().enqueueViewDelta(10L, 5L);
        then(manageProjectProjectionTaskPort).should().enqueueViewDelta(11L, 2L);
    }

    @Test
    @DisplayName("프로젝트 조회수 집계 예외 케이스 - 잘못된 키/0/null 값 무시")
    void flushProjectViewsIgnoreZeroOrNullAndBadKey() {
        // given
        Set<String> keys = new LinkedHashSet<>();
        keys.add("viewCount:PROJECT:20");
        keys.add("bad-key-format");
        keys.add("viewCount:PROJECT:21");

        given(manageProjectViewCountPort.getAllViewCountKeys("PROJECT")).willReturn(keys);
        given(manageProjectViewCountPort.popViewCount(20L, "PROJECT")).willReturn(0L);
        given(manageProjectViewCountPort.popViewCount(21L, "PROJECT")).willReturn(null);

        // when & then
        assertThatNoException().isThrownBy(() -> worker.flushProjectViews());

        // then (0, null 값 및 잘못된 키는 무시됨)
        then(updateProjectViewDbPort).shouldHaveNoInteractions();
        then(manageProjectProjectionTaskPort).shouldHaveNoInteractions();
    }
}
