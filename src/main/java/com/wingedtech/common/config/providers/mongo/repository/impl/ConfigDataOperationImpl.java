package com.wingedtech.common.config.providers.mongo.repository.impl;

import com.wingedtech.common.config.providers.mongo.ConfigData;
import com.wingedtech.common.config.providers.mongo.repository.ConfigDataOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.wingedtech.common.config.providers.mongo.ConfigData.*;

public class ConfigDataOperationImpl implements ConfigDataOperation {

    @Autowired private MongoTemplate mongoTemplate;

    @Override
    public ConfigData saveConfigData(@NotNull ConfigData data) {
        return saveConfigDataWithMongoTemplate(mongoTemplate, data);
    }

    /**
     * 使用指定的mongoTemplate，存储一个配置数据
     * @param mongoTemplate
     * @param data
     * @return
     */
    public static ConfigData saveConfigDataWithMongoTemplate(MongoTemplate mongoTemplate, @NotNull ConfigData data) {
        @NotBlank final String objectId = data.getObjectId();
        @NotBlank final String key = data.getKey();
        Update update = Update.update(FIELD_DATA, data.getData()).setOnInsert(FIELD_OBJECT_ID, objectId).setOnInsert(FIELD_KEY, key);
        return mongoTemplate.findAndModify(buildConfigQuery(objectId, key), update, FindAndModifyOptions.options().upsert(true).returnNew(true), ConfigData.class);
    }

    private static Query buildConfigQuery(@NotBlank String objectId, @NotBlank String key) {
        return Query.query(Criteria.where(FIELD_OBJECT_ID).is(objectId).and(FIELD_KEY).is(key));
    }
}
