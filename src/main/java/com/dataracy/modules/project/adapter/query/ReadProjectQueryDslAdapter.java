package com.dataracy.modules.project.adapter.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.like.adapter.jpa.entity.QLikeEntity;
import com.dataracy.modules.like.domain.enums.TargetType;
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
import com.querydsl.core.Tuple;
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
    private final QLikeEntity like = QLikeEntity.likeEntity;

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
         * - 성능 최적화: 하나의 쿼리로 프로젝트와 연결된 데이터 ID를 함께 조회하여 N+1 문제 해결
         *
         * @param projectId 조회할 프로젝트의 ID
         * @return 프로젝트(부모 포함)과 연결된 데이터셋 ID 목록을 포함한 Optional. 프로젝트가 없으면 Optional.empty()
         */
    @Override
    public Optional<ProjectWithDataIdsResponse> findProjectWithDataById(Long projectId) {
        Instant startTime = LoggerFactory.query().logQueryStart("ProjectEntity", "[findProjectWithDataById] 아이디를 통해 삭제되지 않은 프로젝트를 연결된 데이터셋과 함께 조회 시작. projectId=" + projectId);
        
        // 최적화: 하나의 쿼리로 프로젝트와 연결된 데이터 ID를 함께 조회
        List<Tuple> results = queryFactory
                .select(project, projectData.dataId)
                .from(project)
                .leftJoin(project.parentProject)
                .leftJoin(projectData).on(
                        projectData.project.id.eq(project.id)
                        .and(ProjectDataFilterPredicate.notDeleted())
                )
                .where(
                        ProjectFilterPredicate.projectIdEq(projectId),
                        ProjectFilterPredicate.notDeleted()
                )
                .fetch();

        if (results.isEmpty()) {
            LoggerFactory.query().logQueryEnd("ProjectEntity", "[findProjectWithDataById] 해당하는 프로젝트 리소스가 존재하지 않습니다. projectId=" + projectId, startTime);
            return Optional.empty();
        }

        // 첫 번째 결과에서 프로젝트 엔티티 추출 (모든 결과는 같은 프로젝트)
        ProjectEntity entity = results.get(0).get(project);
        
        // 연결된 dataId 목록 추출 (null 제외)
        List<Long> dataIds = results.stream()
                .map(tuple -> tuple.get(projectData.dataId))
                .filter(Objects::nonNull)
                .distinct()
                .toList();

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

    /**
     * 특정 사용자가 작성한 비삭제 프로젝트들을 페이지 단위로 조회한다.
     *
     * <p>작성일 기준 최신순으로 정렬된 프로젝트들의 최소 정보(minimal)를 반환하며,
     * 전달된 Pageable이 null이면 기본 페이지(PageRequest.of(0, 5))를 사용한다.
     * 결과의 total은 해당 사용자에 대한 비삭제 프로젝트 총 개수를 반영한다.
     *
     * @param userId   조회할 사용자 ID
     * @param pageable 결과 페이징/정렬 정보 (null이면 기본 페이징 사용)
     * @return 주어진 페이징 조건에 따른 Project의 페이지 (각 항목은 minimal 형태)
     */
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

    /**
     * 특정 사용자가 '좋아요'한 프로젝트들을 좋아요 기준(최신 좋아요 순)으로 조회하여 페이징된 결과를 반환한다.
     *
     * 요청된 페이지의 좋아요 기록에서 프로젝트 ID들을 먼저 가져오고, 해당 ID에 대응하는 삭제되지 않은 프로젝트들을 로드한 뒤
     * 좋아요 순서를 유지하여 최소 정보 형태(Project 최소 도메인)로 매핑해 반환한다.
     *
     * @param userId  조회할 사용자의 식별자
     * @param pageable  결과 페이징 및 정렬을 지정하는 Pageable 객체(널이 아니어야 함)
     * @return 페이징된 프로젝트 목록(Page<Project>) — 콘텐츠는 Project의 최소 정보이며 전체 항목 수(total)는 해당 사용자의 좋아요 총 개수에 기반함
     */
    @Override
    public Page<Project> findLikeProjects(Long userId, Pageable pageable) {
        Instant startTime = LoggerFactory.query().logQueryStart(
                "ProjectEntity",
                "[findLikeProjects] 해당 회원이 좋아요한 프로젝트 목록 조회 시작. userId=" + userId
        );

        // 좋아요 ID 페이징
        List<Long> likeProjectIds = queryFactory
                .select(like.targetId)
                .from(like)
                .where(like.userId.eq(userId), like.targetType.eq(TargetType.PROJECT))
                .orderBy(like.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (likeProjectIds.isEmpty()) {
            return Page.empty(pageable);
        }

        long total = Optional.ofNullable(
                queryFactory
                        .select(like.count())
                        .from(like)
                        .where(like.userId.eq(userId), like.targetType.eq(TargetType.PROJECT))
                        .fetchOne()
        ).orElse(0L);

        // 프로젝트 조회
        List<ProjectEntity> entities = queryFactory
                .selectFrom(project)
                .where(project.id.in(likeProjectIds), ProjectFilterPredicate.notDeleted())
                .fetch();

        // 순서 보정
        Map<Long, ProjectEntity> projectMap = entities.stream()
                .collect(Collectors.toMap(ProjectEntity::getId, e -> e));

        List<Project> contents = likeProjectIds.stream()
                .map(projectMap::get)
                .filter(Objects::nonNull)
                .map(ProjectEntityMapper::toMinimal)
                .toList();

        LoggerFactory.query().logQueryEnd(
                "ProjectEntity",
                "[findLikeProjects] 해당 회원이 좋아요한 프로젝트 목록 조회 완료. userId=" + userId,
                startTime
        );

        return new PageImpl<>(contents, pageable, total);
    }

}
