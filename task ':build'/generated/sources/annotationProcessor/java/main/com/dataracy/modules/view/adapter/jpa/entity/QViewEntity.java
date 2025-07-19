package com.dataracy.modules.view.adapter.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QViewEntity is a Querydsl query type for ViewEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QViewEntity extends EntityPathBase<ViewEntity> {

    private static final long serialVersionUID = -1348964843L;

    public static final QViewEntity viewEntity = new QViewEntity("viewEntity");

    public final com.dataracy.modules.common.base.QBaseTimeEntity _super = new com.dataracy.modules.common.base.QBaseTimeEntity(this);

    public final StringPath anonymousId = createString("anonymousId");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath ip = createString("ip");

    public final NumberPath<Long> projectId = createNumber("projectId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final StringPath userAgent = createString("userAgent");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QViewEntity(String variable) {
        super(ViewEntity.class, forVariable(variable));
    }

    public QViewEntity(Path<? extends ViewEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QViewEntity(PathMetadata metadata) {
        super(ViewEntity.class, metadata);
    }

}

