package com.wingedtech.common.mongo.lock;

import com.wingedtech.common.errors.BusinessException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author ssy
 * @date 2020/7/1 11:06
 */
public class CloseableMongoLockHolder implements AutoCloseable {

    private MongoLockHolder mongoLockHolder;
    private String keyValue;
    private boolean gotKey;

    public CloseableMongoLockHolder(@NotNull MongoLockHolder mongoLockHolder, @NotBlank String keyValue) {
        this(mongoLockHolder, keyValue, false, null);
    }

    public CloseableMongoLockHolder(@NotNull MongoLockHolder mongoLockHolder, @NotBlank String keyValue, boolean throwException, String exceptionMessage) {
        this.mongoLockHolder = mongoLockHolder;
        this.keyValue = keyValue;
        this.gotKey = mongoLockHolder.acquireProcessLock(keyValue);
        if (throwException && !this.gotKey) {
            throw new BusinessException(exceptionMessage);
        }
    }

    /**
     * 当前Holder是否已成功获取到锁
     * @return
     */
    public boolean gotLock() {
        return this.gotKey;
    }

    @Override
    public void close() {
        if (this.gotKey) {
            mongoLockHolder.releaseProcessLock(keyValue);
        }
    }
}
