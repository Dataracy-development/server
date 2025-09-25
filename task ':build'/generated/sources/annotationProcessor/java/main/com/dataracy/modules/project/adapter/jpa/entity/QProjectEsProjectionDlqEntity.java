package com.dataracy.modules.project.adapter.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QProjectEsProjectionDlqEntity is a Querydsl query type for ProjectEsProjectionDlqEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProjectEsProjectionDlqEntity extends EntityPathBase<ProjectEsProjectionDlqEntity> {

    private static final long serialVersionUID = -1122146111L;

    public static final QProjectEsProjectionDlqEntity projectEsProjectionDlqEntity = new QProjectEsProjectionDlqEntity("projectEsProjectionDlqEntity");

    public final com.dataracy.modules.common.base.QBaseTimeEntity _super = new com.dataracy.modules.common.base.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Integer> deltaComment = createNumber("deltaComment", Integer.class);

    public final NumberPath<Integer> deltaLike = createNumber("deltaLike", Integer.class);

    public final NumberPath<Long> deltaView = createNumber("deltaView", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath lastError = createString("lastError");

    public final NumberPath<Long> projectId = createNumber("projectId", Long.class);

    public final BooleanPath setDeleted = createBoolean("setDeleted");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QProjectEsProjectionDlqEntity(String variable) {
        super(ProjectEsProjectionDlqEntity.class, forVariable(variable));
    }

    public QProjectEsProjectionDlqEntity(Path<? extends ProjectEsProjectionDlqEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProjectEsProjectionDlqEntity(PathMetadata metadata) {
        super(ProjectEsProjectionDlqEntity.class, metadata);
    }

}

