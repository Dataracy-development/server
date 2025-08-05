package com.dataracy.modules.dataset.adapter.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDataMetadataEntity is a Querydsl query type for DataMetadataEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDataMetadataEntity extends EntityPathBase<DataMetadataEntity> {

    private static final long serialVersionUID = 326241126L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDataMetadataEntity dataMetadataEntity = new QDataMetadataEntity("dataMetadataEntity");

    public final NumberPath<Integer> columnCount = createNumber("columnCount", Integer.class);

    public final QDataEntity data;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath previewJson = createString("previewJson");

    public final NumberPath<Integer> rowCount = createNumber("rowCount", Integer.class);

    public QDataMetadataEntity(String variable) {
        this(DataMetadataEntity.class, forVariable(variable), INITS);
    }

    public QDataMetadataEntity(Path<? extends DataMetadataEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDataMetadataEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDataMetadataEntity(PathMetadata metadata, PathInits inits) {
        this(DataMetadataEntity.class, metadata, inits);
    }

    public QDataMetadataEntity(Class<? extends DataMetadataEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.data = inits.isInitialized("data") ? new QDataEntity(forProperty("data"), inits.get("data")) : null;
    }

}

