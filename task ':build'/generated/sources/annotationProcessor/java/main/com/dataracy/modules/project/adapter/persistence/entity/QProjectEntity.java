package com.dataracy.modules.project.adapter.persistence.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProjectEntity is a Querydsl query type for ProjectEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProjectEntity extends EntityPathBase<ProjectEntity> {

    private static final long serialVersionUID = 702340397L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProjectEntity projectEntity = new QProjectEntity("projectEntity");

    public final com.dataracy.modules.common.base.QBaseTimeEntity _super = new com.dataracy.modules.common.base.QBaseTimeEntity(this);

    public final NumberPath<Long> analysisPurposeId = createNumber("analysisPurposeId", Long.class);

    public final NumberPath<Long> authorLevelId = createNumber("authorLevelId", Long.class);

    public final ListPath<ProjectEntity, QProjectEntity> childProjects = this.<ProjectEntity, QProjectEntity>createList("childProjects", ProjectEntity.class, QProjectEntity.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> dataSourceId = createNumber("dataSourceId", Long.class);

    public final StringPath fileUrl = createString("fileUrl");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isContinue = createBoolean("isContinue");

    public final QProjectEntity parentProject;

    public final ListPath<ProjectDataEntity, QProjectDataEntity> projectDataEntities = this.<ProjectDataEntity, QProjectDataEntity>createList("projectDataEntities", ProjectDataEntity.class, QProjectDataEntity.class, PathInits.DIRECT2);

    public final StringPath title = createString("title");

    public final NumberPath<Long> topicId = createNumber("topicId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QProjectEntity(String variable) {
        this(ProjectEntity.class, forVariable(variable), INITS);
    }

    public QProjectEntity(Path<? extends ProjectEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProjectEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProjectEntity(PathMetadata metadata, PathInits inits) {
        this(ProjectEntity.class, metadata, inits);
    }

    public QProjectEntity(Class<? extends ProjectEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.parentProject = inits.isInitialized("parentProject") ? new QProjectEntity(forProperty("parentProject"), inits.get("parentProject")) : null;
    }

}

