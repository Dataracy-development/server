package com.dataracy.modules.user.adapter.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserEntity is a Querydsl query type for UserEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserEntity extends EntityPathBase<UserEntity> {

    private static final long serialVersionUID = 122933089L;

    public static final QUserEntity userEntity = new QUserEntity("userEntity");

    public final com.dataracy.modules.common.base.QBaseTimeEntity _super = new com.dataracy.modules.common.base.QBaseTimeEntity(this);

    public final NumberPath<Long> authorLevelId = createNumber("authorLevelId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isAdTermsAgreed = createBoolean("isAdTermsAgreed");

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final StringPath nickname = createString("nickname");

    public final NumberPath<Long> occupationId = createNumber("occupationId", Long.class);

    public final StringPath password = createString("password");

    public final EnumPath<com.dataracy.modules.user.domain.enums.ProviderType> provider = createEnum("provider", com.dataracy.modules.user.domain.enums.ProviderType.class);

    public final StringPath providerId = createString("providerId");

    public final EnumPath<com.dataracy.modules.user.domain.enums.RoleType> role = createEnum("role", com.dataracy.modules.user.domain.enums.RoleType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final ListPath<UserTopicEntity, QUserTopicEntity> userTopicEntities = this.<UserTopicEntity, QUserTopicEntity>createList("userTopicEntities", UserTopicEntity.class, QUserTopicEntity.class, PathInits.DIRECT2);

    public final NumberPath<Long> visitSourceId = createNumber("visitSourceId", Long.class);

    public QUserEntity(String variable) {
        super(UserEntity.class, forVariable(variable));
    }

    public QUserEntity(Path<? extends UserEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserEntity(PathMetadata metadata) {
        super(UserEntity.class, metadata);
    }

}

