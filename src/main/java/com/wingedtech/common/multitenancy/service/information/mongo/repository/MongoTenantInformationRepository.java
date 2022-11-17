package com.wingedtech.common.multitenancy.service.information.mongo.repository;

import com.wingedtech.common.multitenancy.service.information.mongo.MongoTenantInformation;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author taozhou
 * @date 2021/4/22
 */
public interface MongoTenantInformationRepository extends MongoRepository<MongoTenantInformation, String> {
}
