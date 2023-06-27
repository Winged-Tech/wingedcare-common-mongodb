package com.wingedtech.common.mongo.log.repository;

import com.wingedtech.common.mongo.log.domain.OperationLog;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author taozhou
 * @date 2021/8/11
 */
public interface OperationLogRepository extends MongoRepository<OperationLog, String>, OperationLogExtendRepository {
}
