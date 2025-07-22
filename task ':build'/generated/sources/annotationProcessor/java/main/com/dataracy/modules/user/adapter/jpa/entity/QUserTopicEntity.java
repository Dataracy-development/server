package com.dataracy.modules.user.adapter.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserTopicEntity is a Querydsl query type for UserTopicEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserTopicEntity extends EntityPathBase<UserTopicEntity> {

    private static final long serialVersionUID = -1818006152L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserTopicEntity userTopicEntity = new QUserTopicEntity("userTopicEntity");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> topicId = createNumber("topicId", Long.class);

    public final QUserEntity user;

    public QUserTopicEntity(String variable) {
        this(UserTopicEntity.class, forVariable(variable), INITS);
    }

    public QUserTopicEntity(Path<? extends UserTopicEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserTopicEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserTopicEntity(PathMetadata metadata, PathInits inits) {
        this(UserTopicEntity.class, metadata, inits);
    }

    public QUserTopicEntity(Class<? extends UserTopicEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUserEntity(forProperty("user")) : null;
    }

}

