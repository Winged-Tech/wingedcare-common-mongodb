package com.wingedtech.common.storage.store.mongo;

import com.wingedtech.common.service.beloging.AbstractAuditingEntityWithUserId;
import com.wingedtech.common.storage.ObjectStorageItemStates;
import com.wingedtech.common.storage.ObjectStorageType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "storage_items")
public class ObjectStorageItemDocument extends AbstractAuditingEntityWithUserId {

    public static final String FIELD_PATH = "path";
    public static final String FIELD_STATE = "state";
    public static final String FIELD_RESOURCE_CONFIG = "resourceConfig";
    public static final String FIELD_OBJECT_ID = "objectId";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_NAME = "name";

    /**
     * 对象存储完整路径（相对路径）
     */
    @NotNull
    @Indexed(unique = true)
    private String path;

    /**
     * 该对象使用的resource配置，需要在application.yml中预先配置
     */
    @Indexed
    private String resourceConfig;
    /**
     * 关联的业务对象id，该id仅用于拼接最终的存储路径，并不做其他形式的存储。
     */
    @Indexed
    private String objectId;
    /**
     * 对象类型
     */
    @Indexed
    private ObjectStorageType type;
    /**
     * 对象名，一般可以是一个文件名，或者带有相对路径的文件名
     */
    @Indexed
    private String name;

    /**
     * 对象存储状态
     */
    @Indexed
    private ObjectStorageItemStates state;
}
