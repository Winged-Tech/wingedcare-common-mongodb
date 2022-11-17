package com.wingedtech.common.service.logging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackageClasses = {ObjectLogRepository.class})
public class ObjectLogServiceConfiguration {
    @Bean
    public ObjectLogMapper objectLogMapper() {
        return new ObjectLogMapperImpl();
    }
    @Bean
    public ObjectLogService objectLogService(ObjectLogRepository repository, ObjectLogMapper objectLogMapper) {
        return new ObjectLogServiceImpl(repository, objectLogMapper);
    }
}
