package com.wingedtech.common.service.beloging;

import com.wingedtech.common.domain.AuditingEntity;
import org.springframework.data.mongodb.core.query.Update;

import static com.wingedtech.common.service.beloging.AbstractAuditingEntityWithUserId.FIELD_USER_ID;

public class MongoRepositoryUtils {
    public static Update updateOrInsert(Update update, AuditingEntityWithUserId item) {
        update = updateOrInsertAuditingEntity(update, item);
        if (item.getUserId() != null) {
            update = update.setOnInsert(FIELD_USER_ID, item.getUserId());
        }
        return update;
    }

    private static Update updateOrInsertAuditingEntity(Update update, AuditingEntity item) {
        if (item.getLastModifiedBy() != null) {
            update = update.set(AuditingEntity.FIELD_LAST_MODIFIED_BY, item.getLastModifiedBy());
        }
        if (item.getLastModifiedDate() != null) {
            update = update.set(AuditingEntity.FIELD_LAST_MODIFIED_DATE, item.getLastModifiedDate());
        }
        if (item.getCreatedBy() != null) {
            update = update.setOnInsert(AuditingEntity.FIELD_CREATED_BY, item.getCreatedBy());
        }
        if (item.getCreatedDate() != null) {
            update = update.setOnInsert(AuditingEntity.FIELD_CREATED_DATE, item.getCreatedDate());
        }
        return update;
    }
}
