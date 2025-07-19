package com.dataracy.modules.reference.adapter.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QVisitSourceEntity is a Querydsl query type for VisitSourceEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVisitSourceEntity extends EntityPathBase<VisitSourceEntity> {

    private static final long serialVersionUID = -1231803416L;

    public static final QVisitSourceEntity visitSourceEntity = new QVisitSourceEntity("visitSourceEntity");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath label = createString("label");

    public final StringPath value = createString("value");

    public QVisitSourceEntity(String variable) {
        super(VisitSourceEntity.class, forVariable(variable));
    }

    public QVisitSourceEntity(Path<? extends VisitSourceEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QVisitSourceEntity(PathMetadata metadata) {
        super(VisitSourceEntity.class, metadata);
    }

}

