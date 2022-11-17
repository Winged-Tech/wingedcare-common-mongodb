package com.wingedtech.common.service.logging;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObjectLogRepository extends MongoRepository<ObjectLog, String>, ObjectLogOperationRepository {
}
