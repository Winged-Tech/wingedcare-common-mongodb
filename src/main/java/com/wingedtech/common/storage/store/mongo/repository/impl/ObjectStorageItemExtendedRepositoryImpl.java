package com.wingedtech.common.storage.store.mongo.repository.impl;

import com.google.common.base.Preconditions;
import com.wingedtech.common.service.beloging.MongoRepositoryUtils;
import com.wingedtech.common.storage.ObjectStorageItemStates;
import com.wingedtech.common.storage.store.mongo.ObjectStorageItemDocument;
import com.wingedtech.common.storage.store.mongo.repository.ObjectStorageItemExtendedRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Optional;

import static com.wingedtech.common.storage.store.mongo.ObjectStorageItemDocument.*;

@Slf4j
public class ObjectStorageItemExtendedRepositoryImpl implements ObjectStorageItemExtendedRepository {

    public static final Class<ObjectStorageItemDocument> ENTITY_CLASS = ObjectStorageItemDocument.class;
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public ObjectStorageItemDocument store(ObjectStorageItemDocument item) {
        final ObjectStorageItemStates state = item.getState();
        Update update = Update.update(FIELD_STATE, state);
        update = MongoRepositoryUtils.updateOrInsert(update, item);
        update = update
            .setOnInsert(FIELD_PATH, item.getPath())
            .setOnInsert(FIELD_RESOURCE_CONFIG, item.getResourceConfig())
            .setOnInsert(FIELD_OBJECT_ID, item.getObjectId())
            .setOnInsert(FIELD_TYPE, item.getType())
            .setOnInsert(FIELD_NAME, item.getName())
            .setOnInsert("_class", ObjectStorageItemDocument.class.getTypeName());

        return mongoTemplate.findAndModify(queryUniqueItem(item), update, FindAndModifyOptions.options().upsert(true).returnNew(true), ENTITY_CLASS);
    }

    private Query queryUniqueItem(ObjectStorageItemDocument item) {
        return Query.query(criteriaForUniqueItem(item));
    }

    @Override
    public Optional<ObjectStorageItemDocument> findUniqueItem(ObjectStorageItemDocument item) {
        return Optional.ofNullable(mongoTemplate.findOne(queryUniqueItem(item), ENTITY_CLASS));
    }

    /**
     * 获取一个用来唯一标识一个item的查询条件
     * @param item
     * @return
     */
    public static Criteria criteriaForUniqueItem(ObjectStorageItemDocument item) {
        Criteria criteria;
        Preconditions.checkNotNull(item.getType(), "Type of a unique item must not be null!");
        // 如果对象的name非空, 优先使用name来进行匹配和唯一化
        if (StringUtils.isNotBlank(item.getName())) {
            criteria = Criteria.where(FIELD_NAME).is(item.getName()).and(FIELD_RESOURCE_CONFIG).is(item.getResourceConfig());
            if (item.getObjectId() != null) {
                criteria = criteria.and(FIELD_OBJECT_ID).is(item.getObjectId());
            }
        }
        else {
            final String path = item.getPath();
            Preconditions.checkArgument(StringUtils.isNotBlank(path), "Path must be provided when object name is null");
            criteria = Criteria.where(FIELD_PATH).is(path);
        }
        criteria = criteria.and(FIELD_TYPE).is(item.getType());
        final String id = item.getId();
        if (StringUtils.isNotBlank(id)) {
            criteria = new Criteria().orOperator(Criteria.where("_id").is(id), criteria);
        }
        return criteria;
    }
}
