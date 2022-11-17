package com.wingedtech.common.mongo.lock;

import com.wingedtech.common.domain.AbstractAuditingEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class MongoLock extends AbstractAuditingEntity {

    public static final String KEY = "key";

    @Id
    private String id;

    @Indexed(unique = true)
    @Field(value = KEY)
    @NotNull
    private String key;

}
