package com.wingedtech.common.service.logging;

import com.google.common.collect.Iterables;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @author ssy
 * @date 2019/8/9 15:48
 */
public class ObjectLogRepositoryImpl implements ObjectLogOperationRepository {

    private final MongoTemplate mongoTemplate;

    public ObjectLogRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public ObjectLog findLatestLogByBusiness(String business, Boolean isException) {
        Criteria criteria = Criteria.where("business").is(business);
        if (isException != null) {
            criteria = criteria.and("isException").is(isException);
        }

        Query query = Query.query(criteria).limit(1).skip(0).with(Sort.by(Sort.Order.desc("_id")));
        List<ObjectLog> objectLogs = mongoTemplate.find(query, ObjectLog.class);
        return Iterables.getFirst(objectLogs, null);
    }
}
