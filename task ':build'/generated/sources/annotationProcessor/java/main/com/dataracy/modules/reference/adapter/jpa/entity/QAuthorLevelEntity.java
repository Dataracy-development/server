package com.dataracy.modules.reference.adapter.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAuthorLevelEntity is a Querydsl query type for AuthorLevelEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuthorLevelEntity extends EntityPathBase<AuthorLevelEntity> {

    private static final long serialVersionUID = 1555109147L;

    public static final QAuthorLevelEntity authorLevelEntity = new QAuthorLevelEntity("authorLevelEntity");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath label = createString("label");

    public final StringPath value = createString("value");

    public QAuthorLevelEntity(String variable) {
        super(AuthorLevelEntity.class, forVariable(variable));
    }

    public QAuthorLevelEntity(Path<? extends AuthorLevelEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAuthorLevelEntity(PathMetadata metadata) {
        super(AuthorLevelEntity.class, metadata);
    }

}

