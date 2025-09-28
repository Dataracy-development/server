package com.dataracy.modules.project.adapter.web.mapper.search;

import com.dataracy.modules.project.adapter.web.mapper.support.ChildProjectWebMapper;
import com.dataracy.modules.project.adapter.web.request.search.FilteringProjectWebRequest;
import com.dataracy.modules.project.adapter.web.response.search.FilteredProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.support.ChildProjectWebResponse;
import com.dataracy.modules.project.application.dto.request.search.FilteringProjectRequest;
import com.dataracy.modules.project.application.dto.response.search.FilteredProjectResponse;
import com.dataracy.modules.project.application.dto.response.support.ChildProjectResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProjectFilterWebMapperTest {

    @Mock
    private ChildProjectWebMapper childProjectWebMapper;

    @InjectMocks
    private ProjectFilterWebMapper mapper;

    @Test
    @DisplayName("성공 → FilteringProjectWebRequest를 FilteringProjectRequest로 매핑")
    void toApplicationDto() {
        // given
        FilteringProjectWebRequest webRequest = new FilteringProjectWebRequest(
                "keyword", "LATEST", 1L, 2L, 3L, 4L
        );

        // when
        FilteringProjectRequest requestDto = mapper.toApplicationDto(webRequest);

        // then
        assertThat(requestDto.keyword()).isEqualTo("keyword");
        assertThat(requestDto.sortType()).isEqualTo("LATEST");
        assertThat(requestDto.topicId()).isEqualTo(1L);
        assertThat(requestDto.analysisPurposeId()).isEqualTo(2L);
        assertThat(requestDto.dataSourceId()).isEqualTo(3L);
        assertThat(requestDto.authorLevelId()).isEqualTo(4L);
    }

    @Test
    @DisplayName("성공 → FilteredProjectResponse를 FilteredProjectWebResponse로 매핑")
    void toWebDto() {
        // given
        ChildProjectResponse childProjectResponse = new ChildProjectResponse(
                100L, "child-title", "child-content", 1L, "child-user", "https://~~", 5L, 6L
        );
        FilteredProjectResponse responseDto = new FilteredProjectResponse(
                10L, "title", "content", 1L, "username", "https://~~", "thumb.png",
                "topicLabel", "analysisPurposeLabel", "dataSourceLabel", "authorLevelLabel",
                11L, 12L, 13L, LocalDateTime.of(2025, 8, 27, 10, 0),
                List.of(childProjectResponse)
        );

        ChildProjectWebResponse mappedChild = new ChildProjectWebResponse(
                100L, "child-title", "child-content", 1L, "child-user", "https://~~", 5L, 6L
        );
        given(childProjectWebMapper.toWebDto(childProjectResponse)).willReturn(mappedChild);

        // when
        FilteredProjectWebResponse webResponse = mapper.toWebDto(responseDto);

        // then
        assertThat(webResponse.id()).isEqualTo(10L);
        assertThat(webResponse.title()).isEqualTo("title");
        assertThat(webResponse.commentCount()).isEqualTo(11L);
        assertThat(webResponse.childProjects()).containsExactly(mappedChild);
    }
}

