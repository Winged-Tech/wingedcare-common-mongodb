package com.wingedtech.common.config.providers.mongo;

import com.wingedtech.common.config.ConfigProperties;
import com.wingedtech.common.domain.AbstractAuditingEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "config_data")
public class ConfigData extends AbstractAuditingEntity {

    public static final String FIELD_OBJECT_ID = "objectId";
    public static final String FIELD_KEY = "key";
    public static final String FIELD_DATA = "data";

    @Id
    private String id;

    /**
     * 配置关联的对象id
     */
    @Indexed
    @NotBlank
    private String objectId;

    /**
     * 配置key
     */
    @Indexed
    @NotBlank
    private String key;

    @NotNull
    private ConfigProperties data;

    public static ConfigData of(String objectId, String key, ConfigProperties data) {
        ConfigData configData = new ConfigData();
        configData.setObjectId(objectId);
        configData.setKey(key);
        configData.setData(data);
        return configData;
    }
}
