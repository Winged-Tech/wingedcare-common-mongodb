package com.wingedtech.common.mongo.lock;

import com.github.mongobee.exception.MongobeeConfigurationException;
import com.github.mongobee.exception.MongobeeConnectionException;
import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoDatabase;
import com.wingedtech.common.util.retry.RetryTemplateManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

@Slf4j
public class MongoLockHolder {

    private MongoDatabase mongoDatabase;
    private MongoTemplate mongoTemplate;
    private final RetryTemplate retryTemplate;

    private MongoLockDAO lockDao;

    public MongoLockHolder(String lockCollectionName, MongoTemplate mongoTemplate) {
        this.lockDao = new MongoLockDAO(lockCollectionName, mongoTemplate);
        this.mongoTemplate = mongoTemplate;
        this.retryTemplate = RetryTemplateManager.buildRetryTemplate(1000L, 3);
    }

    public MongoLockHolder(String lockCollectionName, MongoTemplate mongoTemplate, Long retryPeriod, Integer maxAttempts) {
        this.lockDao = new MongoLockDAO(lockCollectionName, mongoTemplate);
        this.mongoTemplate = mongoTemplate;
        this.retryTemplate = RetryTemplateManager.buildRetryTemplate(retryPeriod, maxAttempts);
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    public MongoDatabase connectMongoDb() throws MongobeeConfigurationException {
        mongoDatabase = mongoTemplate.getDb();
        initializeLock();
        return mongoDatabase;
    }

    /**
     * Try to acquire process lock
     *
     * @return true if successfully acquired, false otherwise
     * @throws MongobeeConnectionException exception
     */
    public boolean acquireProcessLock(String keyValue) {
        try {
            return retryTemplate.execute((RetryCallback<Boolean, Exception>) context -> lockDao.acquireLock(keyValue));
        } catch (MongoWriteException e) {
            if (e.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
                log.debug("Duplicate key exception while acquireLock. Probably the lock has been already acquired.error code:1");
            }
            return false;
        } catch (DuplicateKeyException e) {
            log.debug("Duplicate key exception while acquireLock. Probably the lock has been already acquired.error code:2");
            return false;
        } catch (Exception e) {
            log.error("acquire lock with unexpected exception", e);
            return false;
        }
    }

    public void releaseProcessLock(String key) {
        lockDao.releaseLock(key);
    }

    private void initializeLock() {
        lockDao.initializeLock();
    }

    /* Visible for testing */
    void setLockDao(MongoLockDAO lockDao) {
        this.lockDao = lockDao;
    }

    public MongoLockDAO getLockDao() {
        return lockDao;
    }

    public void setLockCollectionName(String lockCollectionName) {
        this.lockDao.setLockCollectionName(lockCollectionName);
    }

    public void setBackOffPolicy(BackOffPolicy backOffPolicy) {
        this.retryTemplate.setBackOffPolicy(backOffPolicy);
    }

    public void setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryTemplate.setRetryPolicy(retryPolicy);
    }

}
