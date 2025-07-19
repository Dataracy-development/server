package com.dataracy.modules.project.adapter.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProjectDataEntity is a Querydsl query type for ProjectDataEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProjectDataEntity extends EntityPathBase<ProjectDataEntity> {

    private static final long serialVersionUID = -1793909641L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProjectDataEntity projectDataEntity = new QProjectDataEntity("projectDataEntity");

    public final NumberPath<Long> dataId = createNumber("dataId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QProjectEntity project;

    public QProjectDataEntity(String variable) {
        this(ProjectDataEntity.class, forVariable(variable), INITS);
    }

    public QProjectDataEntity(Path<? extends ProjectDataEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProjectDataEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProjectDataEntity(PathMetadata metadata, PathInits inits) {
        this(ProjectDataEntity.class, metadata, inits);
    }

    public QProjectDataEntity(Class<? extends ProjectDataEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.project = inits.isInitialized("project") ? new QProjectEntity(forProperty("project"), inits.get("project")) : null;
    }

}

