package com.wingedtech.common.multitenancy.data.mongo;

import com.github.mongobee.exception.MongobeeChangeSetException;
import com.github.mongobee.exception.MongobeeConfigurationException;
import com.wingedtech.common.constant.Requests;
import com.wingedtech.common.multitenancy.config.MultiTenancyProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.wingedtech.common.multitenancy.data.mongo.MultiTenantMongoConstants.TENANT_MONGO_TEMPLATE;

/**
 * @author taozhou
 * @date 2021/4/21
 */
@RestController
@RequestMapping(Requests.API_DEVOPS + "/datamigrations")
public class MongoMultiTenantMigrationResource {

    private MongoTemplate tenantMongoTemplate;
    private MultiTenancyProperties properties;
    private Environment environment;

    public MongoMultiTenantMigrationResource(@Qualifier(TENANT_MONGO_TEMPLATE) MongoTemplate tenantMongoTemplate, MultiTenancyProperties properties, Environment environment) {
        this.tenantMongoTemplate = tenantMongoTemplate;
        this.properties = properties;
        this.environment = environment;
    }

    @PostMapping("/all-tenants")
    public String runMigrationsForTenants() throws MongobeeConfigurationException, MongobeeChangeSetException {
        final MongoBeeList mongoBeeList = MongoBeeList.createMongoBeeList(tenantMongoTemplate, properties, environment);
        mongoBeeList.execute();
        return "migration executed";
    }
}
