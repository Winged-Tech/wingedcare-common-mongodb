package com.wingedtech.common.storage.store.mongo;

import com.wingedtech.common.storage.ObjectStorageConfigService;
import com.wingedtech.common.storage.config.ObjectStorageResourceConfigProperties;
import com.wingedtech.common.storage.store.ObjectStorageItemStore;
import com.wingedtech.common.storage.store.ObjectStorageItemStoreConfiguration;
import com.wingedtech.common.storage.store.ObjectStorageItemStoreProperties;
import com.wingedtech.common.storage.store.mongo.repository.MongoObjectStorageItemRepositoryConfiguration;
import com.wingedtech.common.storage.store.mongo.repository.ObjectStorageItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(name = ObjectStorageItemStoreProperties.CONFIG_PROVIDER, prefix = ObjectStorageItemStoreProperties.CONFIG_ROOT, havingValue = "mongo", matchIfMissing = true)
@Import({MongoObjectStorageItemRepositoryConfiguration.class})
@AutoConfigureBefore({ObjectStorageItemStoreConfiguration.class})
public class MongoObjectStorageItemStoreConfiguration {
    @Autowired
    ObjectStorageItemStoreProperties objectStorageItemStoreProperties;
    @Autowired
    ObjectStorageResourceConfigProperties configProperties;
    @Autowired
    ObjectStorageConfigService objectStorageConfigService;

    @Bean
    ObjectStorageItemStore mongoObjectStorageItemStore(ObjectStorageItemRepository repository) {
        return new MongoObjectStorageItemStore(repository, ObjectStorageItemMapper.INSTANCE, objectStorageItemStoreProperties, objectStorageConfigService);
    }
}
