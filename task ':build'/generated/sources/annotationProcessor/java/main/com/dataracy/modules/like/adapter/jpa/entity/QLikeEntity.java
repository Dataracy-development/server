package com.dataracy.modules.like.adapter.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QLikeEntity is a Querydsl query type for LikeEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLikeEntity extends EntityPathBase<LikeEntity> {

    private static final long serialVersionUID = 2055148793L;

    public static final QLikeEntity likeEntity = new QLikeEntity("likeEntity");

    public final com.dataracy.modules.common.base.QBaseTimeEntity _super = new com.dataracy.modules.common.base.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> targetId = createNumber("targetId", Long.class);

    public final EnumPath<com.dataracy.modules.like.domain.enums.TargetType> targetType = createEnum("targetType", com.dataracy.modules.like.domain.enums.TargetType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QLikeEntity(String variable) {
        super(LikeEntity.class, forVariable(variable));
    }

    public QLikeEntity(Path<? extends LikeEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLikeEntity(PathMetadata metadata) {
        super(LikeEntity.class, metadata);
    }

}

