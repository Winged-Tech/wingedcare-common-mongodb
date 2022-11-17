package com.wingedtech.common.config.providers.mongo;

import com.wingedtech.common.config.Constants;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(Constants.CONFIG_PROVIDERS_ROOT + "mongo")
public class MongoConfigProviderProperties {
}
