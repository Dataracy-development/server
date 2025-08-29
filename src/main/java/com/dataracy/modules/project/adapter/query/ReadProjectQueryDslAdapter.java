package com.dataracy.modules.project.adapter.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import com.dataracy.modules.project.adapter.jpa.entity.QProjectDataEntity;
import com.dataracy.modules.project.adapter.jpa.entity.QProjectEntity;
import com.dataracy.modules.project.adapter.jpa.mapper.ProjectEntityMapper;
import com.dataracy.modules.project.adapter.query.predicates.ProjectDataFilterPredicate;
import com.dataracy.modules.project.adapter.query.predicates.ProjectFilterPredicate;
import com.dataracy.modules.project.adapter.query.sort.ProjectPopularOrderBuilder;
import com.dataracy.modules.project.adapter.query.sort.ProjectSortBuilder;
import com.dataracy.modules.project.application.dto.response.support.ProjectWithDataIdsResponse;
import com.dataracy.modules.project.application.port.out.query.read.*;
import com.dataracy.modules.project.domain.enums.ProjectSortType;
import com.dataracy.modules.project.domain.model.Project;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ReadProjectQueryDslAdapter implements
        FindProjectPort,
        FindContinuedProjectsPort,
        FindConnectedProjectsPort,
        GetPopularProjectsPort,
        FindUserProjectsPort
{
    private final JPAQueryFactory queryFactory;

    private final QProjectEntity project = QProjectEntity.projectEntity;
    private final QProjectDataEntity projectData = QProjectDataEntity.projectDataEntity;

    /**
     * 주어진 ID에 해당하며 삭제되지 않은 프로젝트를 최소 정보로 조회하여 반환합니다.
     *
     * @param projectId 조회할 프로젝트의 ID
     * @return 프로젝트가 존재하면 최소 정보가 매핑된 Optional<Project>, 없으면 빈 Optional
     */
    @Override
    public Optional<Project> findProjectById(Long projectId) {
        Instant startTime = LoggerFactory.query().logQueryStart("ProjectEntity", "[findProjectById] 아이디를 통해 삭제되지 않은 프로젝트를 조회 시작. projectId=" + projectId);
        ProjectEntity entity = queryFactory
                .selectFrom(project)
                .where(
                        ProjectFilterPredicate.projectIdEq(projectId),
                        ProjectFilterPredicate.notDeleted()
                )
                .fetchOne();
        Optional<Project> savedProject = Optional.ofNullable(ProjectEntityMapper.toMinimal(entity));
        LoggerFactory.query().logQueryEnd("ProjectEntity", "[findProjectById] 아이디를 통해 삭제되지 않은 프로젝트를 조회 완료. projectId=" + projectId, startTime);
        return savedProject;
    }

    /**
         * 지정한 프로젝트 ID에 해당하는 삭제되지 않은 프로젝트와 해당 프로젝트에 연결된 데이터셋 ID 목록을 조회합니다.
         *
         * 상세:
         * - 프로젝트가 존재하지 않으면 Optional.empty()를 반환합니다.
         * - 존재하면 프로젝트(부모 프로젝트 정보 포함)와 그에 연결된 데이터셋 ID 목록을 ProjectWithDataIdsResponse로 감싸 반환합니다.
         *
         * @param projectId 조회할 프로젝트의 ID
         * @return 프로젝트(부모 포함)과 연결된 데이터셋 ID 목록을 포함한 Optional. 프로젝트가 없으면 Optional.empty()
         */
    @Override
    public Optional<ProjectWithDataIdsResponse> findProjectWithDataById(Long projectId) {
        Instant startTime = LoggerFactory.query().logQueryStart("ProjectEntity", "[findProjectWithDataById] 아이디를 통해 삭제되지 않은 프로젝트를 연결된 데이터셋과 함께 조회 시작. projectId=" + projectId);
        ProjectEntity entity = queryFactory
                .selectFrom(project)
                .leftJoin(project.parentProject)
                .where(
                        ProjectFilterPredicate.projectIdEq(projectId),
                        ProjectFilterPredicate.notDeleted()
                )
                .fetchOne();

        if (entity == null) {
            LoggerFactory.query().logQueryEnd("ProjectEntity", "[findProjectWithDataById] 해당하는 프로젝트 리소스가 존재하지 않습니다. projectId=" + projectId, startTime);
            return Optional.empty();
        }

        // 연결된 dataId 목록
        List<Long> dataIds = queryFactory
                .select(projectData.dataId)
                .from(projectData)
                .where(
                        projectData.project.id.eq(projectId),
                        ProjectDataFilterPredicate.notDeleted()
                )
                .fetch();

        Optional<ProjectWithDataIdsResponse> projectWithDataIdsResponse = Optional.of(new ProjectWithDataIdsResponse(
                ProjectEntityMapper.toWithParent(entity),
                dataIds
        ));
        LoggerFactory.query().logQueryEnd("ProjectEntity", "[findProjectWithDataById] 아이디를 통해 삭제되지 않은 프로젝트를 연결된 데이터셋과 함께 조회 완료. projectId=" + projectId, startTime);
        return projectWithDataIdsResponse;
    }

    /**
     * 지정된 부모 프로젝트 ID에 속한 자식 프로젝트들을 최신순으로 페이지네이션하여 반환합니다.
     *
     * @param projectId 부모 프로젝트의 ID
     * @param pageable 결과 페이지네이션 정보
     * @return 자식 프로젝트들의 페이지네이션된 목록
     */
    @Override
    public Page<Project> findContinuedProjects(Long projectId, Pageable pageable) {
        Instant startTime = LoggerFactory.query().logQueryStart("ProjectEntity", "[findContinuedProjects] 이어가기 프로젝트 목록 조회 시작. projectId=" + projectId);
        List<ProjectEntity> entities = queryFactory
                .selectFrom(project)
                .orderBy(ProjectSortBuilder.fromSortOption(ProjectSortType.LATEST))
                .where(
                        ProjectFilterPredicate.parentProjectIdEq(projectId),
                        ProjectFilterPredicate.notDeleted()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Project> contents = entities.stream()
                .map(ProjectEntityMapper::toMinimal)
                .toList();

        long total = Optional.ofNullable(
                queryFactory
                        .select(project.count())
                        .from(project)
                        .where(
                                ProjectFilterPredicate.parentProjectIdEq(projectId),
                                ProjectFilterPredicate.notDeleted()
                        )
                        .fetchOne()
        ).orElse(0L);

        LoggerFactory.query().logQueryEnd("ProjectEntity", "[findContinuedProjects] 이어가기 프로젝트 목록 조회 완료. projectId=" + projectId, startTime);
        return new PageImpl<>(contents, pageable, total);
    }

    /**
     * 지정된 데이터 ID와 연결된 프로젝트들을 페이징하여 조회합니다.
     *
     * <p>프로젝트는 연결된 ProjectData 엔티티를 기준으로 프로젝트 생성일(createdAt) 내림차순으로 정렬되어 반환됩니다.
     * 조회는 soft-delete(삭제 플래그가 설정되지 않은) 된 연결만 대상으로 하며, 결과 콘텐츠는 최소 정보(minimal) 형태로 매핑됩니다.
     * 총건수는 해당 데이터와 연결된 서로 다른 프로젝트 수(distinct)를 기준으로 계산됩니다.</p>
     *
     * @param dataId 조회할 데이터(데이터셋)의 ID
     * @param pageable 페이지 번호·크기 및 정렬 정보를 포함한 페이징 파라미터
     * @return 페이징된 프로젝트 목록을 담은 Page 객체(정렬: 프로젝트 생성일 내림차순, 총건수는 distinct 기준)
     */
    @Override
    public Page<Project> findConnectedProjectsAssociatedWithDataset(Long dataId, Pageable pageable) {
        Instant startTime = LoggerFactory.query().logQueryStart("ProjectEntity", "[findConnectedProjectsAssociatedWithDataset] 데이터셋과 연결된 프로젝트 목록 조회 시작. dataId=" + dataId);

        // 먼저 id 목록 조회 (페이징 포함)
        List<Long> projectIds = queryFactory
                .select(projectData.project.id)
                .from(projectData)
                .where(
                        ProjectDataFilterPredicate.dataIdEq(dataId),
                        ProjectDataFilterPredicate.notDeleted()
                )
                .orderBy(projectData.project.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (projectIds.isEmpty()) {
            return Page.empty(pageable);
        }

        List<ProjectEntity> entities = queryFactory
                .selectFrom(project)
                .where(project.id.in(projectIds))
                .fetch();

        Map<Long, ProjectEntity> entityMap = entities.stream()
                .collect(Collectors.toMap(ProjectEntity::getId, Function.identity()));

        List<Project> contents = projectIds.stream()
                .map(entityMap::get)
                .filter(Objects::nonNull)
                .map(ProjectEntityMapper::toMinimal)
                .toList();

        long total = Optional.ofNullable(
                queryFactory
                        .select(projectData.project.countDistinct())
                        .from(projectData)
                        .where(
                                ProjectDataFilterPredicate.dataIdEq(dataId),
                                ProjectDataFilterPredicate.notDeleted()
                        )
                        .fetchOne()
        ).orElse(0L);

        LoggerFactory.query().logQueryEnd("ProjectEntity", "[findConnectedProjectsAssociatedWithDataset] 데이터셋과 연결된 프로젝트 목록 조회 완료. dataId=" + dataId, startTime);
        return new PageImpl<>(contents, pageable, total);
    }

    /**
     * 지정된 개수만큼 인기 순으로 정렬된 프로젝트의 최소 정보 목록을 반환합니다.
     *
     * 논리적으로 삭제되지 않은 프로젝트만 포함되며, 내부적으로 인기 순 정렬을 적용하고 결과를 도메인 모델의 최소 표현(Project)으로 매핑합니다.
     *
     * @param size 반환할 최대 프로젝트 개수
     * @return 인기 순으로 정렬된 Project 도메인 객체의 리스트
     */
    @Override
    public List<Project> getPopularProjects(int size) {
        Instant startTime = LoggerFactory.query().logQueryStart("ProjectEntity", "[getPopularProjects] 인기있는 프로젝트 목록 조회 시작.");
        List<Project> popularProjects =  queryFactory
                .selectFrom(project)
                .where(
                        ProjectFilterPredicate.notDeleted()
                )
                .orderBy(ProjectPopularOrderBuilder.popularOrder())
                .limit(size)
                .fetch()
                .stream()
                .map(ProjectEntityMapper::toMinimal)
                .toList();

        LoggerFactory.query().logQueryEnd("ProjectEntity", "[getPopularProjects] 인기있는 프로젝트 목록 조회 완료.", startTime);
        return popularProjects;
    }

    @Override
    public Page<Project> findUserProjects(Long userId, Pageable pageable) {
        // 기본 Pageable: page=0, size=5
        Pageable effectivePageable = (pageable == null)
                ? PageRequest.of(0, 5)
                : pageable;

        Instant startTime = LoggerFactory.query().logQueryStart(
                "ProjectEntity",
                "[findUserProjects] 해당 회원이 작성한 프로젝트 목록 조회 시작. userId=" + userId
        );

        List<ProjectEntity> entities = queryFactory
                .selectFrom(project)
                .orderBy(ProjectSortBuilder.fromSortOption(ProjectSortType.LATEST))
                .where(
                        ProjectFilterPredicate.userIdEq(userId),
                        ProjectFilterPredicate.notDeleted()
                )
                .offset(effectivePageable.getOffset())
                .limit(effectivePageable.getPageSize())
                .fetch();

        List<Project> contents = entities.stream()
                .map(ProjectEntityMapper::toMinimal)
                .toList();

        long total = Optional.ofNullable(
                queryFactory
                        .select(project.count())
                        .from(project)
                        .where(
                                ProjectFilterPredicate.userIdEq(userId),
                                ProjectFilterPredicate.notDeleted()
                        )
                        .fetchOne()
        ).orElse(0L);

        LoggerFactory.query().logQueryEnd(
                "ProjectEntity",
                "[findUserProjects] 해당 회원이 작성한 프로젝트 목록 조회 완료. userId=" + userId,
                startTime
        );

        return new PageImpl<>(contents, effectivePageable, total);
    }
}
