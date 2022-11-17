package com.wingedtech.common.mongodb.autoconfigure.analytics;

import com.wingedtech.common.analytics.AnalyticsConstant;
import com.wingedtech.common.autoconfigure.analytics.AnalyticsAutoConfiguration;
import com.wingedtech.common.mongo.analytics.service.AnalyticsService;
import com.wingedtech.common.mongo.analytics.service.impl.AnalyticsServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author apple
 */
@Configuration
@ConditionalOnProperty(prefix = AnalyticsConstant.CONFIG_PREFIX, value = "enabled", havingValue = "mongo")
@Import(AnalyticsAutoConfiguration.class)
public class MongoAnalyticsAutoConfiguration {

    @Bean
    public AnalyticsService analyticsService(MongoTemplate mongoTemplate) {
        return new AnalyticsServiceImpl(mongoTemplate);
    }
}
