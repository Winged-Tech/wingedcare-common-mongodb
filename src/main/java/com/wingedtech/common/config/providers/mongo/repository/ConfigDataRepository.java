package com.wingedtech.common.config.providers.mongo.repository;

import com.wingedtech.common.config.providers.mongo.ConfigData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("common_config_data_repository")
public interface ConfigDataRepository extends MongoRepository<ConfigData, String>, ConfigDataOperation {

    Optional<ConfigData> findByObjectIdAndKey(String objectId, String key);

    List<ConfigData> findAllByKey(String key);
}
