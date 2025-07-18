package com.dataracy.modules.reference.adapter.persistence.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAnalysisPurposeEntity is a Querydsl query type for AnalysisPurposeEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAnalysisPurposeEntity extends EntityPathBase<AnalysisPurposeEntity> {

    private static final long serialVersionUID = 1888257220L;

    public static final QAnalysisPurposeEntity analysisPurposeEntity = new QAnalysisPurposeEntity("analysisPurposeEntity");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath label = createString("label");

    public final StringPath value = createString("value");

    public QAnalysisPurposeEntity(String variable) {
        super(AnalysisPurposeEntity.class, forVariable(variable));
    }

    public QAnalysisPurposeEntity(Path<? extends AnalysisPurposeEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAnalysisPurposeEntity(PathMetadata metadata) {
        super(AnalysisPurposeEntity.class, metadata);
    }

}

