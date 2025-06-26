package com.dataracy.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 엔티티 생성 및 수정 시 자동으로 시간 정보, 작성자 정보를 관리하기 위한 클래스입니다.
 * 해당 클래스를 상속받는 경우, @EntityListeners를 통해
 * JPA Auditing 기능이 적용되어 자동으로 'createdAt'과 'updatedAt', 'createdBy'와 'updatedBy' 필드가
 * 관리됩니다.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity extends BaseTimeEntity {

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
