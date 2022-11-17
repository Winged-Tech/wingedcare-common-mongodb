package com.wingedtech.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.Instant;

/**
 * Base abstract class for entities which will hold definitions for created, last modified by and created,
 * last modified by date.
 */
@Data
public abstract class AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String CREATED_BY = "created_by";
    public static final String CREATED_DATE = "created_date";
    public static final String LAST_MODIFIED_BY = "last_modified_by";
    public static final String LAST_MODIFIED_DATE = "last_modified_date";

    @CreatedBy
    @Field(CREATED_BY)
    @JsonIgnore
    private String createdBy;

    @CreatedDate
    @Field(CREATED_DATE)
    @JsonIgnore
    private Instant createdDate = Instant.now();

    @LastModifiedBy
    @Field(LAST_MODIFIED_BY)
    @JsonIgnore
    private String lastModifiedBy;

    @LastModifiedDate
    @Field(LAST_MODIFIED_DATE)
    @JsonIgnore
    private Instant lastModifiedDate = Instant.now();
}
