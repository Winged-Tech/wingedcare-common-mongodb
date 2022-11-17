package com.wingedtech.common.generalenum;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author taozhou
 * @date 2021/3/30
 */
@Configuration
@EnableMongoRepositories("com.wingedtech.common.generalenum")
public class GeneralEnumerationDatabaseConfiguration {
}
