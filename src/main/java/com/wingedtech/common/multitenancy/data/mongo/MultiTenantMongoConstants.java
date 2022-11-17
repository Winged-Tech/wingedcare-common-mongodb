package com.wingedtech.common.multitenancy.data.mongo;

public final class MultiTenantMongoConstants {
    public static final String TENANT_MONGO_TEMPLATE = "tenantMongoTemplate";
    public static final String MASTER_MONGO_TEMPLATE = "masterMongoTemplate";

    /**
     * @deprecated 该字段名称有拼写错误, 应替换为MASTER_MONGO_TEMPLATE
     */
    @Deprecated
    public static final String MASTER_MONGO_TEMPALTE = MASTER_MONGO_TEMPLATE;
    public static final String TENANT_MONGO_CONVERTER = "tenantMongoConverter";

    private MultiTenantMongoConstants() {

    }
}
