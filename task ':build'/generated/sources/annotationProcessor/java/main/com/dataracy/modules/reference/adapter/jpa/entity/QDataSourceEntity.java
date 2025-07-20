package com.dataracy.modules.reference.adapter.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDataSourceEntity is a Querydsl query type for DataSourceEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDataSourceEntity extends EntityPathBase<DataSourceEntity> {

    private static final long serialVersionUID = -1642316827L;

    public static final QDataSourceEntity dataSourceEntity = new QDataSourceEntity("dataSourceEntity");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath label = createString("label");

    public final StringPath value = createString("value");

    public QDataSourceEntity(String variable) {
        super(DataSourceEntity.class, forVariable(variable));
    }

    public QDataSourceEntity(Path<? extends DataSourceEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDataSourceEntity(PathMetadata metadata) {
        super(DataSourceEntity.class, metadata);
    }

}

