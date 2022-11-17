package com.wingedtech.common.mongodb.autoconfigure.log;

import com.wingedtech.common.autoconfigure.log.OperationLogAutoConfiguration;
import com.wingedtech.common.log.LogConstant;
import com.wingedtech.common.log.service.OperationLogService;
import com.wingedtech.common.mongo.log.repository.OperationLogRepository;
import com.wingedtech.common.mongo.log.service.OperationLogMapper;
import com.wingedtech.common.mongo.log.service.impl.OperationLogServiceImpl;
import com.wingedtech.common.service.logging.ObjectLogRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author apple
 */
@Configuration
@ConditionalOnProperty(prefix = LogConstant.CONFIG_PREFIX, value = "enabled", havingValue = "mongo")
@EnableMongoRepositories(basePackageClasses = {OperationLogRepository.class})
@Import(OperationLogAutoConfiguration.class)
public class MongoOperationLogAutoConfiguration {

    @Bean
    public OperationLogService operationLogService(OperationLogRepository repository) {
        return new OperationLogServiceImpl(repository, OperationLogMapper.INSTANCE);
    }
}
