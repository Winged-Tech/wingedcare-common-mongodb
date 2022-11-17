package com.wingedtech.common.storage.store.mongo.repository;

import com.mongodb.MongoClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories("com.wingedtech.common.storage.store.mongo.repository")
@ConditionalOnBean(MongoClient.class)
public class MongoObjectStorageItemRepositoryConfiguration {
}
