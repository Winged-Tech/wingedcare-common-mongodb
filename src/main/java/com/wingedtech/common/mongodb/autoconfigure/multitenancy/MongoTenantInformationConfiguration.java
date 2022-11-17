package com.wingedtech.common.mongodb.autoconfigure.multitenancy;

import com.wingedtech.common.multitenancy.ConditionalOnMultiTenant;
import com.wingedtech.common.multitenancy.service.information.mongo.MongoTenantInformationStoreConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author taozhou
 * @date 2021/4/22
 */
@Configuration
@ConditionalOnMultiTenant
@Import(MongoTenantInformationStoreConfiguration.class)
public class MongoTenantInformationConfiguration {
}
