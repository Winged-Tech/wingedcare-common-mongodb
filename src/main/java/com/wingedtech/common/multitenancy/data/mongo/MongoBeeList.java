package com.wingedtech.common.multitenancy.data.mongo;

import com.github.mongobee.Mongobee;
import com.github.mongobee.exception.MongobeeChangeSetException;
import com.github.mongobee.exception.MongobeeConfigurationException;
import com.mongodb.MongoClient;
import com.wingedtech.common.multitenancy.Tenant;
import com.wingedtech.common.multitenancy.TenantInformationService;
import com.wingedtech.common.multitenancy.config.MultiTenancyDataProperties;
import com.wingedtech.common.multitenancy.config.MultiTenancyProperties;
import com.wingedtech.common.multitenancy.domain.TenantInformation;
import com.wingedtech.common.multitenancy.util.TemporaryTenantContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;

/**
 * @author taozhou
 * @date 2021/4/21
 */
@Slf4j
public class MongoBeeList implements InitializingBean {
    private List<Mongobee> bees = new ArrayList<>();
    private List<String> tenantIds = new ArrayList<>();

    private boolean triggerExceptionWhenError = true;

    public MongoBeeList(boolean triggerExceptionWhenError) {
        this.triggerExceptionWhenError = triggerExceptionWhenError;
    }

    public static MongoBeeList createMongoBeeList(MongoTemplate mongoTemplate, MultiTenancyProperties properties, Environment environment) throws MongobeeConfigurationException {
        log.info("Initializing multi tenant mongo bee");
        TenantInformationService tenantInformationService = Tenant.getTenantInformationService();
        final MultiTenancyDataProperties dataProperties = properties.getData();
        MongoBeeList mongoBeeList = new MongoBeeList(dataProperties.isStopWhenMigrationFails());

        if (StringUtils.isBlank(dataProperties.getMigrationScanPackage())) {
            throw new MongobeeConfigurationException("Property migrationScanPackage is empty, unable to configure multi tenant mongobee!");
        }

        MongoDbFactory mongoDbFactory = mongoTemplate.getMongoDbFactory();
        if (mongoDbFactory instanceof MultiTenantMongoDbFactory) {
            String migrationScanPackage = dataProperties.getMigrationScanPackage();
            log.info("migrationScanPackage: {}", migrationScanPackage);
            MultiTenantMongoDbFactory multiTenantMongoDbFactory = (MultiTenantMongoDbFactory) mongoDbFactory;
            List<TenantInformation> tenants = tenantInformationService.getTenants();
            if (!CollectionUtils.isEmpty(tenants)) {
                tenants.forEach(tenantInformation -> {
                    String tenantId = tenantInformation.getId();
                    MongoClient multiClient = multiTenantMongoDbFactory.getTenantMongoClient(tenantId);
                    Mongobee mongobee = new Mongobee(multiClient);
                    mongobee.setDbName(multiTenantMongoDbFactory.getDatabaseName(tenantId));
                    mongobee.setMongoTemplate(mongoTemplate);
                    mongobee.setChangeLogsScanPackage(migrationScanPackage);
                    mongobee.setSpringEnvironment(environment);
                    mongobee.setEnabled(true);
                    mongoBeeList.addMongobee(mongobee, tenantId);
                });
            }
        }
        else {
            log.error("mongoDbFactory is not MultiTenantMongoDbFactory, something must be wrong (tenantMongoTemplate might be not correctly configured.)!!");
            if (dataProperties.isStopWhenMigrationFails()) {
                throw new MongobeeConfigurationException("mongoDbFactory is not MultiTenantMongoDbFactory, tenantMongoTemplate might be not correctly configured.");
            }
        }
        return mongoBeeList;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final StopWatch watch = execute();
        if (watch != null) {
            log.info("DbMigration summary: {}", watch.prettyPrint());
        }
    }

    public StopWatch execute() throws MongobeeChangeSetException {
        boolean hasError = false;
        StopWatch stopWatch = new StopWatch();
        for (int i = 0; i < bees.size(); i++) {
            Mongobee bee = bees.get(i);
            final String tenantId = tenantIds.get(i);
            stopWatch.start(tenantId);
            try (TemporaryTenantContext ignored = new TemporaryTenantContext(tenantId)) {
                log.info("Running Mongobee for {}", tenantId);
                bee.execute();
            } catch (Exception e) {
                log.error("Failed to execute Mongobee", e);
                hasError = true;
            }
            stopWatch.stop();
        }
        if (hasError && triggerExceptionWhenError) {
            throw new MongobeeChangeSetException("Failed to execute Mongobee for one or more tenants!");
        }
        return stopWatch;
    }

    public void addMongobee(Mongobee mongobee, String tenantId) {
        if (bees == null) {
            bees = new ArrayList<>();
        }
        bees.add(mongobee);
        tenantIds.add(tenantId);
    }
}
