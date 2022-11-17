package com.wingedtech.common.multitenancy.data.mongo;

import com.github.mongobee.exception.MongobeeConfigurationException;
import com.wingedtech.common.autoconfigure.multitenancy.MultiTenancyConfiguration;
import com.wingedtech.common.mongodb.autoconfigure.multitenancy.MongoMultiTenantConfiguration;
import com.wingedtech.common.multitenancy.config.MultiTenancyProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

import static com.wingedtech.common.multitenancy.config.MultiTenancyDataProperties.PROPERTY_ENABLE_DB_MIGRATION;
import static com.wingedtech.common.multitenancy.config.MultiTenancyDataProperties.PROPERTY_RUN_DB_MIGRATION_ON_STARTUP;
import static com.wingedtech.common.multitenancy.data.mongo.MultiTenantMongoConstants.TENANT_MONGO_TEMPLATE;

/**
 * Created on 2018/10/22.
 *
 * @author ssy
 */

@Configuration
@ConditionalOnBean({MultiTenancyConfiguration.class})
@ConditionalOnProperty(value = {PROPERTY_ENABLE_DB_MIGRATION})
@AutoConfigureAfter(MongoMultiTenantConfiguration.class)
@Slf4j
public class MongobeeConfiguration {

    @Bean
    @ConditionalOnProperty(value = {PROPERTY_RUN_DB_MIGRATION_ON_STARTUP}, havingValue = "true", matchIfMissing = true)
    public MongoBeeList mongoBeeList(@Qualifier(TENANT_MONGO_TEMPLATE) MongoTemplate mongoTemplate, MultiTenancyProperties properties, Environment environment) throws MongobeeConfigurationException {
        return MongoBeeList.createMongoBeeList(mongoTemplate, properties, environment);
    }

    @Bean
    public MongoMultiTenantMigrationResource mongoMultiTenantMigrationResource(@Qualifier(TENANT_MONGO_TEMPLATE) MongoTemplate mongoTemplate, MultiTenancyProperties properties, Environment environment) {
        return new MongoMultiTenantMigrationResource(mongoTemplate, properties, environment);
    }

}
