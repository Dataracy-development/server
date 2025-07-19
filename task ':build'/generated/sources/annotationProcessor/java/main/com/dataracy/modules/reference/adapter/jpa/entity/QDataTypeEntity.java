package com.dataracy.modules.reference.adapter.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDataTypeEntity is a Querydsl query type for DataTypeEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDataTypeEntity extends EntityPathBase<DataTypeEntity> {

    private static final long serialVersionUID = 1430889860L;

    public static final QDataTypeEntity dataTypeEntity = new QDataTypeEntity("dataTypeEntity");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath label = createString("label");

    public final StringPath value = createString("value");

    public QDataTypeEntity(String variable) {
        super(DataTypeEntity.class, forVariable(variable));
    }

    public QDataTypeEntity(Path<? extends DataTypeEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDataTypeEntity(PathMetadata metadata) {
        super(DataTypeEntity.class, metadata);
    }

}

