package com.dataracy.modules.project.adapter.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import com.dataracy.modules.project.adapter.jpa.entity.QProjectDataEntity;
import com.dataracy.modules.project.adapter.jpa.entity.QProjectEntity;
import com.dataracy.modules.project.adapter.jpa.mapper.ProjectEntityMapper;
import com.dataracy.modules.project.adapter.query.predicates.ProjectDataFilterPredicate;
import com.dataracy.modules.project.adapter.query.predicates.ProjectFilterPredicate;
import com.dataracy.modules.project.adapter.query.sort.ProjectSortBuilder;
import com.dataracy.modules.project.application.dto.response.support.ProjectWithDataIdsResponse;
import com.dataracy.modules.project.application.port.out.query.read.FindConnectedProjectsPort;
import com.dataracy.modules.project.application.port.out.query.read.FindContinuedProjectsPort;
import com.dataracy.modules.project.application.port.out.query.read.FindProjectPort;
import com.dataracy.modules.project.domain.enums.ProjectSortType;
import com.dataracy.modules.project.domain.model.Project;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
        FindConnectedProjectsPort
{
    private final JPAQueryFactory queryFactory;

    private final QProjectEntity project = QProjectEntity.projectEntity;
    private final QProjectDataEntity projectData = QProjectDataEntity.projectDataEntity;

    /**
     * 주어진 ID에 해당하며 삭제되지 않은 프로젝트를 Optional로 반환합니다.
     *
     * @param projectId 조회할 프로젝트의 ID
     * @return 존재하면 최소 정보가 매핑된 프로젝트, 없으면 빈 Optional
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

    @Override
    public Optional<ProjectWithDataIdsResponse> findProjectWithDataById(Long projectId) {
        Instant startTime = LoggerFactory.query().logQueryStart("ProjectEntity", "[findProjectWithDataById] 아이디를 통해 삭제되지 않은 프로젝트를 연결된 데이터셋과 함께 조회 시작. projectId=" + projectId);
        ProjectEntity entity = queryFactory
                .selectFrom(project)
                .where(
                        ProjectFilterPredicate.projectIdEq(projectId),
                        ProjectFilterPredicate.notDeleted()
                )
                .fetchOne();

        if (entity == null) {
            LoggerFactory.query().logQueryEnd("ProjectEntity", "[findProjectWithDataIdsById] 해당하는 프로젝트 리소스가 존재하지 않습니다.projectId=" + projectId, startTime);
            return Optional.empty();
        }

        // 2) 연결된 dataId 목록
        List<Long> dataIds = queryFactory
                .select(projectData.dataId)
                .from(projectData)
                .where(projectData.project.id.eq(projectId))
                .fetch();

        Optional<ProjectWithDataIdsResponse> projectWithDataIdsResponse = Optional.of(new ProjectWithDataIdsResponse(
                ProjectEntityMapper.toMinimal(entity),
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
     * 주어진 데이터 ID와 연관된 프로젝트들을 최신 생성일 순으로 페이징하여 조회합니다.
     *
     * @param dataId 연관된 데이터의 ID
     * @param pageable 페이징 및 정렬 정보
     * @return 데이터셋과 연결된 프로젝트들의 페이징 결과. 프로젝트는 최신 생성일 순으로 정렬됩니다.
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
}
