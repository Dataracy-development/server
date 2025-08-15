package com.dataracy.modules.project.adapter.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QEsProjectionTaskEntity is a Querydsl query type for EsProjectionTaskEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEsProjectionTaskEntity extends EntityPathBase<EsProjectionTaskEntity> {

    private static final long serialVersionUID = -635852592L;

    public static final QEsProjectionTaskEntity esProjectionTaskEntity = new QEsProjectionTaskEntity("esProjectionTaskEntity");

    public final com.dataracy.modules.common.base.QBaseTimeEntity _super = new com.dataracy.modules.common.base.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Integer> deltaComment = createNumber("deltaComment", Integer.class);

    public final NumberPath<Integer> deltaLike = createNumber("deltaLike", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath lastError = createString("lastError");

    public final DateTimePath<java.time.LocalDateTime> nextRunAt = createDateTime("nextRunAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> projectId = createNumber("projectId", Long.class);

    public final NumberPath<Integer> retryCount = createNumber("retryCount", Integer.class);

    public final BooleanPath setDeleted = createBoolean("setDeleted");

    public final EnumPath<com.dataracy.modules.project.domain.enums.EsProjectionStatus> status = createEnum("status", com.dataracy.modules.project.domain.enums.EsProjectionStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QEsProjectionTaskEntity(String variable) {
        super(EsProjectionTaskEntity.class, forVariable(variable));
    }

    public QEsProjectionTaskEntity(Path<? extends EsProjectionTaskEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEsProjectionTaskEntity(PathMetadata metadata) {
        super(EsProjectionTaskEntity.class, metadata);
    }

}

