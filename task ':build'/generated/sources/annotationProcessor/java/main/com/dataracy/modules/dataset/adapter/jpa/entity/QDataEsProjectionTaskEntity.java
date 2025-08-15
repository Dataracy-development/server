package com.dataracy.modules.dataset.adapter.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDataEsProjectionTaskEntity is a Querydsl query type for DataEsProjectionTaskEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDataEsProjectionTaskEntity extends EntityPathBase<DataEsProjectionTaskEntity> {

    private static final long serialVersionUID = 1066788537L;

    public static final QDataEsProjectionTaskEntity dataEsProjectionTaskEntity = new QDataEsProjectionTaskEntity("dataEsProjectionTaskEntity");

    public final com.dataracy.modules.common.base.QBaseTimeEntity _super = new com.dataracy.modules.common.base.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> dataId = createNumber("dataId", Long.class);

    public final NumberPath<Integer> deltaDownload = createNumber("deltaDownload", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath lastError = createString("lastError");

    public final DateTimePath<java.time.LocalDateTime> nextRunAt = createDateTime("nextRunAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> retryCount = createNumber("retryCount", Integer.class);

    public final BooleanPath setDeleted = createBoolean("setDeleted");

    public final EnumPath<com.dataracy.modules.dataset.domain.enums.DataEsProjectionType> status = createEnum("status", com.dataracy.modules.dataset.domain.enums.DataEsProjectionType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QDataEsProjectionTaskEntity(String variable) {
        super(DataEsProjectionTaskEntity.class, forVariable(variable));
    }

    public QDataEsProjectionTaskEntity(Path<? extends DataEsProjectionTaskEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDataEsProjectionTaskEntity(PathMetadata metadata) {
        super(DataEsProjectionTaskEntity.class, metadata);
    }

}

