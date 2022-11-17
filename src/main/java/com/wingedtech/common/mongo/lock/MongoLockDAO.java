package com.wingedtech.common.mongo.lock;

import com.wingedtech.common.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.Instant;

@Slf4j
public class MongoLockDAO {

    private final MongoTemplate mongoTemplate;
    private String lockCollectionName;

    public MongoLockDAO(String lockCollectionName, MongoTemplate mongoTemplate) {
        this.lockCollectionName = lockCollectionName;
        this.mongoTemplate = mongoTemplate;
    }

    public void initializeLock() {
        mongoTemplate.indexOps(lockCollectionName).ensureIndex(new Index().on(MongoLock.KEY, Sort.Direction.DESC).unique());
    }

    public boolean acquireLock(String keyValue) {
        // acquire lock by attempting to insert the same value in the collection - if it already exists (i.e. lock held)
        // there will be an exception
        if (log.isDebugEnabled()) {
            log.debug("try to acquire [{}] lock: {}", lockCollectionName, keyValue);
        }
        ObjectId o = new ObjectId();
        Instant now = Instant.now();
        Query query = Query.query(Criteria.where(MongoLock.KEY).is(keyValue));
        String operator;
        if (SecurityUtils.isAuthenticated()) {
            operator = SecurityUtils.getCurrentUserLogin().orElse("");
        } else {
            operator = "system";
        }
        Update update = new Update().setOnInsert("_id", o)
            .setOnInsert(MongoLock.KEY, keyValue)
            .setOnInsert(MongoLock.CREATED_DATE, now)
            .setOnInsert(MongoLock.LAST_MODIFIED_DATE, now)
            .setOnInsert(MongoLock.CREATED_BY, operator)
            .setOnInsert(MongoLock.LAST_MODIFIED_BY, operator)
            .setOnInsert("_class", MongoLock.class.getName());
        final MongoLock andModify = mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().upsert(true).returnNew(true), MongoLock.class, lockCollectionName);
        return andModify.getId().equals(o.toString());
    }

    public void releaseLock(String keyValue) {
        // release lock by deleting collection entry
        Query query = Query.query(Criteria.where(MongoLock.KEY).is(keyValue));
        mongoTemplate.remove(query, lockCollectionName);
    }

    public void setLockCollectionName(String lockCollectionName) {
        this.lockCollectionName = lockCollectionName;
    }

    public String getLockCollectionName() {
        return lockCollectionName;
    }
}
