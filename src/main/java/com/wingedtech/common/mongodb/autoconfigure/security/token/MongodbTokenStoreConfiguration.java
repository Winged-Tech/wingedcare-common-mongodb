package com.wingedtech.common.mongodb.autoconfigure.security.token;

import com.mongodb.MongoClient;
import com.wingedtech.common.autoconfigure.multitenancy.MultiTenancyConfiguration;
import com.wingedtech.common.autoconfigure.multitenancy.service.TenantInformationServiceConfiguration;
import com.wingedtech.common.multitenancy.config.MultiTenancyProperties;
import com.wingedtech.common.multitenancy.data.tokenstore.MultiTenantTokenStoreMongoDbFactory;
import com.wingedtech.common.multitenancy.data.tokenstore.TokenStoreMongoProperties;
import com.wingedtech.common.security.oauth2.provider.token.store.MongodbTokenStore;
import com.wingedtech.common.security.oauth2.token.ConditionalOnMongodbTokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * @author 6688 Sun
 */
@Configuration
@ConditionalOnMongodbTokenStore
@ConditionalOnBean({MultiTenancyConfiguration.class, MongoAutoConfiguration.class})
@EnableConfigurationProperties({MultiTenancyProperties.class, TokenStoreMongoProperties.class})
@AutoConfigureAfter({TenantInformationServiceConfiguration.class})
public class MongodbTokenStoreConfiguration {

    private final MultiTenancyProperties multiTenancyProperties;
    @Autowired
    private MongoDbFactory mongoDbFactory;
    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private MongoConverter mongoConverter;
    private TokenStoreMongoProperties tokenStoreMongoProperties;

    public MongodbTokenStoreConfiguration(MultiTenancyProperties multiTenancyProperties, TokenStoreMongoProperties tokenStoreMongoProperties) {
        this.multiTenancyProperties = multiTenancyProperties;
        this.tokenStoreMongoProperties = tokenStoreMongoProperties;
    }

    @Bean
    @Primary
    public TokenStore tokenStore() {
        return new MongodbTokenStore(tokenStoreMongoTemplate(mongoDbFactory, mongoClient, mongoConverter));
    }

//    @Bean
//    public MappingMongoConverter tenantTokenStoreMongoConverter(MongoClient mongoClient, MongoMappingContext context, MongoCustomConversions conversions) {
//        DbRefResolver dbRefResolver = new DefaultDbRefResolver(multiTenantMongoDbFactory(mongoClient));
//        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver,
//            context);
//        mappingConverter.setCustomConversions(conversions);
//        return mappingConverter;
//    }

    /**
     * 默认的mongoTemplate，确保该实例为primary
     */
    @Bean
    public MongoTemplate tokenStoreMongoTemplate(MongoDbFactory mongoDbFactory, MongoClient mongoClient, MongoConverter converter) {
        if (this.multiTenancyProperties.getData() != null && this.multiTenancyProperties.getData().isUseTenantTemplateAsPrimary()) {
            return tenantTokenStoreMongoTemplate(mongoClient, converter);
        }
        return masterTokenStoreMongoTemplate(mongoDbFactory, converter);
    }

    /**
     * Master专用mongoTemplate
     */
    @Bean
    public MongoTemplate masterTokenStoreMongoTemplate(MongoDbFactory mongoDbFactory, MongoConverter converter) {
        return new MongoTemplate(mongoDbFactory, converter);
    }

    /**
     * 租户专用mongoTemplate
     */
    @Bean
    public MongoTemplate tenantTokenStoreMongoTemplate(MongoClient mongoClient, MongoConverter converter) {
        return new MongoTemplate(multiTenantMongoDbFactory(mongoClient), converter);
    }

    private MongoDbFactory multiTenantMongoDbFactory(MongoClient mongoClient) {
        return new MultiTenantTokenStoreMongoDbFactory(mongoClient, tokenStoreMongoProperties, multiTenancyProperties.getData(), "uaa");
    }
}
