package com.wingedtech.common.multitenancy.service.information.mongo;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import static com.wingedtech.common.multitenancy.data.mongo.MultiTenantMongoConstants.MASTER_MONGO_TEMPLATE;

/**
 * @author taozhou
 * @date 2021/4/22
 */
@Configuration
@EnableMongoRepositories(value = "com.wingedtech.common.multitenancy.service.information.mongo.repository", mongoTemplateRef = MASTER_MONGO_TEMPLATE)
@EnableConfigurationProperties(MongoTenantInformationStoreProperties.class)
public class MongoTenantInformationStoreConfiguration {

}
