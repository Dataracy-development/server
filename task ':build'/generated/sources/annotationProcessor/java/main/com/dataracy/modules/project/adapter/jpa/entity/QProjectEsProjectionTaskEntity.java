package com.dataracy.modules.project.adapter.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QProjectEsProjectionTaskEntity is a Querydsl query type for ProjectEsProjectionTaskEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProjectEsProjectionTaskEntity extends EntityPathBase<ProjectEsProjectionTaskEntity> {

    private static final long serialVersionUID = 803125715L;

    public static final QProjectEsProjectionTaskEntity projectEsProjectionTaskEntity = new QProjectEsProjectionTaskEntity("projectEsProjectionTaskEntity");

    public final com.dataracy.modules.common.base.QBaseTimeEntity _super = new com.dataracy.modules.common.base.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Integer> deltaComment = createNumber("deltaComment", Integer.class);

    public final NumberPath<Integer> deltaLike = createNumber("deltaLike", Integer.class);

    public final NumberPath<Long> deltaView = createNumber("deltaView", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath lastError = createString("lastError");

    public final DateTimePath<java.time.LocalDateTime> nextRunAt = createDateTime("nextRunAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> projectId = createNumber("projectId", Long.class);

    public final NumberPath<Integer> retryCount = createNumber("retryCount", Integer.class);

    public final BooleanPath setDeleted = createBoolean("setDeleted");

    public final EnumPath<com.dataracy.modules.project.domain.enums.ProjectEsProjectionType> status = createEnum("status", com.dataracy.modules.project.domain.enums.ProjectEsProjectionType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QProjectEsProjectionTaskEntity(String variable) {
        super(ProjectEsProjectionTaskEntity.class, forVariable(variable));
    }

    public QProjectEsProjectionTaskEntity(Path<? extends ProjectEsProjectionTaskEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProjectEsProjectionTaskEntity(PathMetadata metadata) {
        super(ProjectEsProjectionTaskEntity.class, metadata);
    }

}

