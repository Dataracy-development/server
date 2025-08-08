package com.dataracy.modules.reference.adapter.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOccupationEntity is a Querydsl query type for OccupationEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOccupationEntity extends EntityPathBase<OccupationEntity> {

    private static final long serialVersionUID = -265518005L;

    public static final QOccupationEntity occupationEntity = new QOccupationEntity("occupationEntity");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath label = createString("label");

    public final StringPath value = createString("value");

    public QOccupationEntity(String variable) {
        super(OccupationEntity.class, forVariable(variable));
    }

    public QOccupationEntity(Path<? extends OccupationEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOccupationEntity(PathMetadata metadata) {
        super(OccupationEntity.class, metadata);
    }

}

