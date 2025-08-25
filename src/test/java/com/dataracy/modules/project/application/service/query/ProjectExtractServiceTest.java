package com.dataracy.modules.project.application.service.query;

import com.dataracy.modules.project.application.port.out.query.extractor.ExtractProjectOwnerPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ProjectExtractServiceTest {

    @Mock ExtractProjectOwnerPort extractProjectOwnerPort;

    @InjectMocks ProjectExtractService service;

    @Test
    @DisplayName("findUserIdByProjectId - 프로젝트 소유자 ID 정상 반환")
    void findUserIdByProjectId_success() {
        // given
        Long projectId = 10L;
        Long expectedUserId = 100L;
        given(extractProjectOwnerPort.findUserIdByProjectId(projectId)).willReturn(expectedUserId);

        // when
        Long result = service.findUserIdByProjectId(projectId);

        // then
        assertThat(result).isEqualTo(expectedUserId);
        then(extractProjectOwnerPort).should().findUserIdByProjectId(projectId);
    }

    @Test
    @DisplayName("findUserIdIncludingDeleted - 삭제된 프로젝트 포함 소유자 ID 정상 반환")
    void findUserIdIncludingDeleted_success() {
        // given
        Long projectId = 20L;
        Long expectedUserId = 200L;
        given(extractProjectOwnerPort.findUserIdIncludingDeleted(projectId)).willReturn(expectedUserId);

        // when
        Long result = service.findUserIdIncludingDeleted(projectId);

        // then
        assertThat(result).isEqualTo(expectedUserId);
        then(extractProjectOwnerPort).should().findUserIdIncludingDeleted(projectId);
    }
}
