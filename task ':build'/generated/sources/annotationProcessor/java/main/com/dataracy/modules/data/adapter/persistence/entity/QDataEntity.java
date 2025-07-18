package com.dataracy.modules.data.adapter.persistence.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDataEntity is a Querydsl query type for DataEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDataEntity extends EntityPathBase<DataEntity> {

    private static final long serialVersionUID = 1430279327L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDataEntity dataEntity = new QDataEntity("dataEntity");

    public final com.dataracy.modules.common.base.QBaseTimeEntity _super = new com.dataracy.modules.common.base.QBaseTimeEntity(this);

    public final StringPath analysisGuide = createString("analysisGuide");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath dataFileUrl = createString("dataFileUrl");

    public final NumberPath<Long> dataSourceId = createNumber("dataSourceId", Long.class);

    public final NumberPath<Long> dataTypeId = createNumber("dataTypeId", Long.class);

    public final StringPath description = createString("description");

    public final NumberPath<Integer> downloadCount = createNumber("downloadCount", Integer.class);

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QDataMetadataEntity metadata;

    public final NumberPath<Integer> recentWeekDownloadCount = createNumber("recentWeekDownloadCount", Integer.class);

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    public final StringPath thumbnailUrl = createString("thumbnailUrl");

    public final StringPath title = createString("title");

    public final NumberPath<Long> topicId = createNumber("topicId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QDataEntity(String variable) {
        this(DataEntity.class, forVariable(variable), INITS);
    }

    public QDataEntity(Path<? extends DataEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDataEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDataEntity(PathMetadata metadata, PathInits inits) {
        this(DataEntity.class, metadata, inits);
    }

    public QDataEntity(Class<? extends DataEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.metadata = inits.isInitialized("metadata") ? new QDataMetadataEntity(forProperty("metadata"), inits.get("metadata")) : null;
    }

}

