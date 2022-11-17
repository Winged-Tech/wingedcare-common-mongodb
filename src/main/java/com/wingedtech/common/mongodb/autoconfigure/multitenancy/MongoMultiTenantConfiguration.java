package com.wingedtech.common.mongodb.autoconfigure.multitenancy;


import com.mongodb.MongoClient;
import com.wingedtech.common.autoconfigure.multitenancy.MultiTenancyConfiguration;
import com.wingedtech.common.autoconfigure.multitenancy.service.TenantInformationServiceConfiguration;
import com.wingedtech.common.multitenancy.config.MultiTenancyProperties;
import com.wingedtech.common.multitenancy.data.mongo.MongobeeConfiguration;
import com.wingedtech.common.multitenancy.data.mongo.MultiTenantMongoConstants;
import com.wingedtech.common.multitenancy.data.mongo.MultiTenantMongoDbFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

/**
 * Created by Administrator on 2018/1/2.
 */

@Slf4j
@Configuration
@ConditionalOnBean({MultiTenancyConfiguration.class, MongoAutoConfiguration.class})
@EnableConfigurationProperties(MultiTenancyProperties.class)
@AutoConfigureAfter({TenantInformationServiceConfiguration.class})
@Import({MongobeeConfiguration.class})
public class MongoMultiTenantConfiguration {

    /**
     * 多租户的数据库名默认使用当前服务名来拼接
     */
    @Value("${spring.application.name}")
    private String serviceName;
    private MongoProperties mongoProperties;
    private MultiTenancyProperties multiTenancyProperties;

    public MongoMultiTenantConfiguration(MongoProperties mongoProperties, MultiTenancyProperties multiTenancyProperties) {
        this.mongoProperties = mongoProperties;
        this.multiTenancyProperties = multiTenancyProperties;
    }

    @Bean(name = MultiTenantMongoConstants.TENANT_MONGO_CONVERTER)
    public MappingMongoConverter mappingMongoConverter(MongoClient mongoClient, MongoMappingContext context, MongoCustomConversions conversions) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(multiTenantMongoDbFactory(mongoClient));
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver,
            context);
        mappingConverter.setCustomConversions(conversions);
        return mappingConverter;
    }

    @Bean
    @ConditionalOnMissingBean(MongoDbFactory.class)
    public MongoDbFactory defaultMongoDbFactory(MongoClient mongo) {
        String database = this.mongoProperties.getMongoClientDatabase();
        return new SimpleMongoDbFactory(mongo, database);
    }

    /**
     * 默认的mongoTemplate，确保该实例为primary
     *
     * @param mongoDbFactory
     * @param converter
     * @return
     */
    @Bean
    @Primary
    public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory, MongoClient mongoClient, MongoConverter converter) {
        if (this.multiTenancyProperties.getData() != null && this.multiTenancyProperties.getData().isUseTenantTemplateAsPrimary()) {
            return tenantMongoTemplate(mongoClient, converter);
        }

        return masterMongoTemplate(mongoDbFactory, converter);
    }

    /**
     * Master专用mongoTemplate
     *
     * @param mongoDbFactory
     * @param converter
     * @return
     */
    @Bean(name = MultiTenantMongoConstants.MASTER_MONGO_TEMPLATE)
    public MongoTemplate masterMongoTemplate(MongoDbFactory mongoDbFactory, MongoConverter converter) {
        return new MongoTemplate(mongoDbFactory, converter);
    }

    /**
     * 租户专用mongoTemplate
     *
     * @return
     * @throws Exception
     */
    @Bean(name = MultiTenantMongoConstants.TENANT_MONGO_TEMPLATE)
    public MongoTemplate tenantMongoTemplate(MongoClient mongoClient, MongoConverter converter) {
        MongoTemplate template = new MongoTemplate(multiTenantMongoDbFactory(mongoClient), converter);
        return template;
    }

    public MongoDbFactory multiTenantMongoDbFactory(MongoClient mongoClient) {
        return new MultiTenantMongoDbFactory(mongoClient, mongoProperties, multiTenancyProperties.getData(), serviceName);
    }
}
