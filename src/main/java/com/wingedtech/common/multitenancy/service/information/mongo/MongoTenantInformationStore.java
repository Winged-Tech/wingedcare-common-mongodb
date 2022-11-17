package com.wingedtech.common.multitenancy.service.information.mongo;

import com.wingedtech.common.multitenancy.domain.TenantInformation;
import com.wingedtech.common.multitenancy.service.information.TenantInformationStore;
import com.wingedtech.common.multitenancy.service.information.mongo.repository.MongoTenantInformationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author taozhou
 * @date 2021/4/22
 */
@Slf4j
@AllArgsConstructor
public class MongoTenantInformationStore implements TenantInformationStore, InitializingBean {

    private final MongoTenantInformationRepository repository;

    @Override
    public List<TenantInformation> getAll() {
        List<MongoTenantInformation> tenantInformation = repository.findAll();
        if (CollectionUtils.isNotEmpty(tenantInformation)) {
            return Collections.unmodifiableList(
                tenantInformation.stream()
                    .map(MongoTenantInformation::getTenant)
                    .collect(Collectors.toList())
            );
        }
        return Collections.emptyList();
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
