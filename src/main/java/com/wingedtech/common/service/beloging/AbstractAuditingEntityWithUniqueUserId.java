package com.wingedtech.common.service.beloging;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.*;

import java.time.Instant;

/**
 * 实现了{@link ObjectWithUserId}接口的{@code AbstractAuditingEntity}
 * Base abstract class for entities which will hold definitions for created, last modified by and created,
 * last modified by date.
 *
 * @author Jason
 * @since 2019-04-01 10:47
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AbstractAuditingEntityWithUniqueUserId extends MongoDocumentWithUniqueUserId implements AuditingEntityWithUserId {

    private static final long serialVersionUID = 1494893105795974280L;

    @Id
    private String id;

    @CreatedBy
    @JsonIgnore
    private String createdBy;

    @CreatedDate
    @JsonIgnore
    private Instant createdDate = Instant.now();

    @LastModifiedBy
    @JsonIgnore
    private String lastModifiedBy;

    @LastModifiedDate
    @JsonIgnore
    private Instant lastModifiedDate = Instant.now();
}
