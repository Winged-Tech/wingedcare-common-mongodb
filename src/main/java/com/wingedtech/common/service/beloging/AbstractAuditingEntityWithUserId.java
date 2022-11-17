package com.wingedtech.common.service.beloging;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wingedtech.common.domain.AuditingEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;

/**
 * 实现了ObjectWithUserId接口的AbstractAuditingEntity
 * Base abstract class for entities which will hold definitions for created, last modified by and created,
 * last modified by date.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class AbstractAuditingEntityWithUserId extends MongoDocumentWithUserId implements AuditingEntityWithUserId {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @CreatedBy
    @JsonIgnore
    private String createdBy;

    @CreatedDate
    @JsonIgnore
    @Indexed
    private Instant createdDate = Instant.now();

    @LastModifiedBy
    @JsonIgnore
    private String lastModifiedBy;

    @LastModifiedDate
    @JsonIgnore
    @Indexed
    private Instant lastModifiedDate = Instant.now();
}
