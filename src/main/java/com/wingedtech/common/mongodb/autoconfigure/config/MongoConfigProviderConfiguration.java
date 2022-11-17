package com.wingedtech.common.mongodb.autoconfigure.config;

import com.wingedtech.common.autoconfigure.config.ConfigServiceConfiguration;
import com.wingedtech.common.config.ConfigProvider;
import com.wingedtech.common.config.Constants;
import com.wingedtech.common.config.providers.mongo.MongoConfigProvider;
import com.wingedtech.common.config.providers.mongo.MongoConfigProviderProperties;
import com.wingedtech.common.config.providers.mongo.repository.ConfigDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableConfigurationProperties(MongoConfigProviderProperties.class)
@ConditionalOnProperty(value = Constants.CONFIG_PROVIDER_KEY, havingValue = "mongo")
@ConditionalOnClass(ConfigDataRepository.class)
@AutoConfigureBefore(ConfigServiceConfiguration.class)
@EnableMongoRepositories(basePackageClasses = ConfigDataRepository.class)
@Slf4j
public class MongoConfigProviderConfiguration {
    @Bean
    ConfigProvider configProvider(ConfigDataRepository repository) {
        log.info("Initializing MongoConfigProviderConfiguration");
        return new MongoConfigProvider(repository);
    }
}
