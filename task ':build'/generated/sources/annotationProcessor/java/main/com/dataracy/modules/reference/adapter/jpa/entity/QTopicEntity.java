package com.dataracy.modules.reference.adapter.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTopicEntity is a Querydsl query type for TopicEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTopicEntity extends EntityPathBase<TopicEntity> {

    private static final long serialVersionUID = -492520299L;

    public static final QTopicEntity topicEntity = new QTopicEntity("topicEntity");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath label = createString("label");

    public final StringPath value = createString("value");

    public QTopicEntity(String variable) {
        super(TopicEntity.class, forVariable(variable));
    }

    public QTopicEntity(Path<? extends TopicEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTopicEntity(PathMetadata metadata) {
        super(TopicEntity.class, metadata);
    }

}

