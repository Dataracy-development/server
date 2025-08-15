package com.dataracy.modules.dataset.adapter.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDataEsProjectionDlqEntity is a Querydsl query type for DataEsProjectionDlqEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDataEsProjectionDlqEntity extends EntityPathBase<DataEsProjectionDlqEntity> {

    private static final long serialVersionUID = 687474459L;

    public static final QDataEsProjectionDlqEntity dataEsProjectionDlqEntity = new QDataEsProjectionDlqEntity("dataEsProjectionDlqEntity");

    public final com.dataracy.modules.common.base.QBaseTimeEntity _super = new com.dataracy.modules.common.base.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> dataId = createNumber("dataId", Long.class);

    public final NumberPath<Integer> deltaDownload = createNumber("deltaDownload", Integer.class);

    public final StringPath error = createString("error");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QDataEsProjectionDlqEntity(String variable) {
        super(DataEsProjectionDlqEntity.class, forVariable(variable));
    }

    public QDataEsProjectionDlqEntity(Path<? extends DataEsProjectionDlqEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDataEsProjectionDlqEntity(PathMetadata metadata) {
        super(DataEsProjectionDlqEntity.class, metadata);
    }

}

