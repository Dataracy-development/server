package com.dataracy.modules.project.adapter.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QEsProjectionDlqEntity is a Querydsl query type for EsProjectionDlqEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEsProjectionDlqEntity extends EntityPathBase<EsProjectionDlqEntity> {

    private static final long serialVersionUID = 1048192548L;

    public static final QEsProjectionDlqEntity esProjectionDlqEntity = new QEsProjectionDlqEntity("esProjectionDlqEntity");

    public final com.dataracy.modules.common.base.QBaseTimeEntity _super = new com.dataracy.modules.common.base.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Integer> deltaComment = createNumber("deltaComment", Integer.class);

    public final NumberPath<Integer> deltaLike = createNumber("deltaLike", Integer.class);

    public final StringPath error = createString("error");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> projectId = createNumber("projectId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QEsProjectionDlqEntity(String variable) {
        super(EsProjectionDlqEntity.class, forVariable(variable));
    }

    public QEsProjectionDlqEntity(Path<? extends EsProjectionDlqEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEsProjectionDlqEntity(PathMetadata metadata) {
        super(EsProjectionDlqEntity.class, metadata);
    }

}

