package com.dataracy.modules.project.adapter.jpa.impl.command;

import com.dataracy.modules.project.adapter.jpa.repository.ProjectJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UpdateProjectViewDbAdapter 테스트
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UpdateProjectViewDbAdapterTest {

    @InjectMocks
    private UpdateProjectViewDbAdapter updateProjectViewDbAdapter;

    @Mock
    private ProjectJpaRepository projectJpaRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @BeforeEach
    void setUp() {
        // 기본 mock 설정
        willDoNothing().given(projectJpaRepository).increaseViewCount(anyLong(), anyLong());
        
        // 모든 테스트에서 사용할 기본 mock 설정
        given(entityManager.createNativeQuery(anyString())).willReturn(query);
        given(query.executeUpdate()).willReturn(1);
        
        // @PersistenceContext로 주입되는 EntityManager를 mock으로 설정
        ReflectionTestUtils.setField(updateProjectViewDbAdapter, "entityManager", entityManager);
    }

    @Test
    @DisplayName("단일 프로젝트 조회수 증가 성공")
    void increaseViewCount_성공() {
        // given
        Long projectId = 1L;
        Long count = 5L;

        // when
        updateProjectViewDbAdapter.increaseViewCount(projectId, count);

        // then
        then(projectJpaRepository).should().increaseViewCount(projectId, count);
    }

    @Test
    @DisplayName("배치 조회수 증가 성공 - 여러 프로젝트")
    void increaseViewCountBatch_성공_여러프로젝트() {
        // given
        Map<Long, Long> viewCountUpdates = new HashMap<>();
        viewCountUpdates.put(1L, 3L);
        viewCountUpdates.put(2L, 5L);
        viewCountUpdates.put(3L, 2L);


        // when
        updateProjectViewDbAdapter.increaseViewCountBatch(viewCountUpdates);

        // then
        then(entityManager).should().createNativeQuery(anyString());
        then(query).should().executeUpdate();
    }

    @Test
    @DisplayName("배치 조회수 증가 성공 - 단일 프로젝트")
    void increaseViewCountBatch_성공_단일프로젝트() {
        // given
        Map<Long, Long> viewCountUpdates = new HashMap<>();
        viewCountUpdates.put(1L, 10L);

        // when
        updateProjectViewDbAdapter.increaseViewCountBatch(viewCountUpdates);

        // then
        then(entityManager).should().createNativeQuery(anyString());
        then(query).should().executeUpdate();
    }

    @Test
    @DisplayName("배치 조회수 증가 - 빈 맵으로 호출 시 아무것도 하지 않음")
    void increaseViewCountBatch_빈맵_아무것도하지않음() {
        // given
        Map<Long, Long> viewCountUpdates = new HashMap<>(); // 빈 맵

        // when
        updateProjectViewDbAdapter.increaseViewCountBatch(viewCountUpdates);

        // then
        then(entityManager).should(never()).createNativeQuery(anyString());
        then(query).should(never()).executeUpdate();
    }

    @Test
    @DisplayName("배치 조회수 증가 성공 - null 값 포함")
    void increaseViewCountBatch_성공_null값포함() {
        // given
        Map<Long, Long> viewCountUpdates = new HashMap<>();
        viewCountUpdates.put(1L, 3L);
        viewCountUpdates.put(2L, null); // null 값
        viewCountUpdates.put(3L, 2L);


        // when
        updateProjectViewDbAdapter.increaseViewCountBatch(viewCountUpdates);

        // then
        then(entityManager).should().createNativeQuery(anyString());
        then(query).should().executeUpdate();
    }

    @Test
    @DisplayName("배치 조회수 증가 성공 - 0 값 포함")
    void increaseViewCountBatch_성공_0값포함() {
        // given
        Map<Long, Long> viewCountUpdates = new HashMap<>();
        viewCountUpdates.put(1L, 3L);
        viewCountUpdates.put(2L, 0L); // 0 값
        viewCountUpdates.put(3L, 2L);


        // when
        updateProjectViewDbAdapter.increaseViewCountBatch(viewCountUpdates);

        // then
        then(entityManager).should().createNativeQuery(anyString());
        then(query).should().executeUpdate();
    }

    @Test
    @DisplayName("배치 조회수 증가 성공 - 음수 값 포함")
    void increaseViewCountBatch_성공_음수값포함() {
        // given
        Map<Long, Long> viewCountUpdates = new HashMap<>();
        viewCountUpdates.put(1L, 3L);
        viewCountUpdates.put(2L, -1L); // 음수 값
        viewCountUpdates.put(3L, 2L);


        // when
        updateProjectViewDbAdapter.increaseViewCountBatch(viewCountUpdates);

        // then
        then(entityManager).should().createNativeQuery(anyString());
        then(query).should().executeUpdate();
    }
}