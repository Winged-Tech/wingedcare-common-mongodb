package com.wingedtech.common.mongo.lock;

import com.github.mongobee.exception.MongobeeConfigurationException;
import com.wingedtech.common.errors.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;

@Slf4j
public class MongoLockManager {

    private final MongoTemplate mongoTemplate;

    public MongoLockManager(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * @param lockCollection 获取锁的mongo表名
     */
    public MongoLockHolder getMongoLockHolder(String lockCollection) {
        MongoLockHolder changeEntryDao = new MongoLockHolder(lockCollection, mongoTemplate);
        tryConnect(changeEntryDao);
        return changeEntryDao;
    }

    private void tryConnect(MongoLockHolder changeEntryDao) {
        try {
            changeEntryDao.connectMongoDb();
        } catch (MongobeeConfigurationException e) {
            log.error("mongodb 连接失败", e);
            throw new BusinessException("同步锁初始化异常");
        }
    }

    /**
     * @param lockCollection mongo表名
     * @param retryPeriod 重试间隔时间(ms)
     * @param maxAttempts 重试次数
     */
    public MongoLockHolder getMongoLockHolder(String lockCollection, Long retryPeriod, Integer maxAttempts) {
        MongoLockHolder changeEntryDao = new MongoLockHolder(lockCollection, mongoTemplate, retryPeriod, maxAttempts);
        tryConnect(changeEntryDao);
        return changeEntryDao;
    }
}
