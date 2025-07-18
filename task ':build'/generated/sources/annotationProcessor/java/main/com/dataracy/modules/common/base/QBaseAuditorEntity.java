package com.dataracy.modules.common.base;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBaseAuditorEntity is a Querydsl query type for BaseAuditorEntity
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QBaseAuditorEntity extends EntityPathBase<BaseAuditorEntity> {

    private static final long serialVersionUID = -335914659L;

    public static final QBaseAuditorEntity baseAuditorEntity = new QBaseAuditorEntity("baseAuditorEntity");

    public final NumberPath<Long> createdBy = createNumber("createdBy", Long.class);

    public final NumberPath<Long> modifiedBy = createNumber("modifiedBy", Long.class);

    public QBaseAuditorEntity(String variable) {
        super(BaseAuditorEntity.class, forVariable(variable));
    }

    public QBaseAuditorEntity(Path<? extends BaseAuditorEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBaseAuditorEntity(PathMetadata metadata) {
        super(BaseAuditorEntity.class, metadata);
    }

}

