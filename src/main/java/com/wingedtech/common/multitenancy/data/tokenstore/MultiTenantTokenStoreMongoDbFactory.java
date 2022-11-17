package com.wingedtech.common.multitenancy.data.tokenstore;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.wingedtech.common.multitenancy.Tenant;
import com.wingedtech.common.multitenancy.TenantInformationHelper;
import com.wingedtech.common.multitenancy.config.MultiTenancyDataProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * MongoDbFactory is used to instantiate mongoTemplate in a multi-tenancy environment
 */
@Slf4j
public class MultiTenantTokenStoreMongoDbFactory extends SimpleMongoDbFactory {
    private final ConcurrentMap<String, MongoClient> tenantClients = new ConcurrentHashMap<>();

    private MultiTenancyDataProperties multiTenancyDataProperties;

    /**
     * 当前服务名，用于自动拼接多租户数据库名
     */
    private String serviceName;

    /**
     * 租户数据库名称前缀，例如"dev-"或"prod-"
     */
    private String databasePrefix;

    /**
     * 租户数据库名称后缀，通常是服务名，例如"-uaa"或"-gateway"
     */
    private String databaseSuffix;

    /**
     * Master专用MongoClient
     */
    private MongoClient masterClient;

    /**
     * Master专用database
     */
    private String masterDatabase;

    /**
     *
     * @param defaultClient 由spring.data.mongodb.uri配置的默认MongoClient
     * @param mongoProperties
     * @param multiTenancyDataProperties
     */
    public MultiTenantTokenStoreMongoDbFactory(MongoClient defaultClient, TokenStoreMongoProperties mongoProperties, MultiTenancyDataProperties multiTenancyDataProperties, String serviceName) {
        // 将defaultClient传递给父类（SimpleMongoDbFactory)
        super(defaultClient, mongoProperties.getMongoClientDatabase());
        Validate.notNull(multiTenancyDataProperties, "MultiTenancyDataProperties must not be null!");
        Validate.notBlank(serviceName, "serviceName must not be blank!");
        this.masterClient = defaultClient;
        this.masterDatabase = mongoProperties.getMongoClientDatabase();
        this.multiTenancyDataProperties = multiTenancyDataProperties;
        this.serviceName = serviceName;
        loadDataConfiguration();
    }

    @Override
    public void destroy() throws Exception {
        super.destroy();

        // 关闭所有MongoClient
    }

    public void loadDataConfiguration() {
        this.databasePrefix = this.multiTenancyDataProperties.getDatabasePrefix();
        this.databaseSuffix = "-" + this.serviceName;
    }

    @Override
    public MongoDatabase getDb() {
        String currentTenantId = Tenant.getCurrentTenantId();
        // 没有租户信息时，或者是master租户时，直接返回SimpleMongoDbFactory的db
        if (!Tenant.isCurrentTenantIdSet() || Tenant.isMasterTenant()) {
            log.trace("[Multitenancy] Getting master database for tenant {}", currentTenantId);
            return super.getDb();
        }
        MongoClient mongoClient = getTenantMongoClient(currentTenantId);
        String databaseName = getDatabaseName(currentTenantId);
        log.trace("[Multitenancy] Getting database for tenant {}: {}", currentTenantId, databaseName);
        return mongoClient.getDatabase(databaseName);
    }

    /**
     * 获取当前租户的MongoClient
     * @param currentTenantId
     * @return
     */
    public MongoClient getTenantMongoClient(String currentTenantId) {
        if (Tenant.isMasterTenant(currentTenantId) || multiTenancyDataProperties.isAllTenantsUseMasterClient()) {
            return masterClient;
        }
        MongoClient mongoClient = tenantClients.get(currentTenantId);
        if (mongoClient == null) {
            String mongoUri = TenantInformationHelper.getMongoUri(currentTenantId);
            MongoClient client = (MongoClient) Mongo.Holder.singleton().connect(new MongoClientURI(mongoUri));
            tenantClients.put(currentTenantId, client);
            mongoClient = client;
        }
        return mongoClient;
    }

    /**
     * 获取租户的database name
     * @param currentTenantId
     * @return
     */
    public String getDatabaseName(String currentTenantId) {
        if (Tenant.isMasterTenant(currentTenantId)) {
            return masterDatabase;
        }
        // 尝试使用当前的serviceName获取租户的数据库配置
        String configuredDatabase = TenantInformationHelper.getTenantProperty(currentTenantId, serviceName);
        if (StringUtils.isNotBlank(configuredDatabase)) {
            return configuredDatabase;
        }
        configuredDatabase = TenantInformationHelper.getMongoDatabase(currentTenantId);
        if (StringUtils.isNotBlank(configuredDatabase)) {
            return configuredDatabase;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(databasePrefix).append(currentTenantId).append(databaseSuffix);
        return builder.toString();
    }
}
