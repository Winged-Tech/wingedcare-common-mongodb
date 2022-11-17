package com.wingedtech.common.config.providers.mongo;

import com.google.common.collect.Lists;
import com.wingedtech.common.config.ConfigProperties;
import com.wingedtech.common.config.ConfigProvider;
import com.wingedtech.common.config.providers.mongo.repository.ConfigDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

/**
 * 使用MongoDB存储/读取配置数据的provider
 */
@Slf4j
public class MongoConfigProvider implements ConfigProvider {

    private final ConfigDataRepository repository;

    public MongoConfigProvider(ConfigDataRepository repository) {
        this.repository = repository;
    }


    /**
     * 从配置缓存中读取指定key的配置信息
     *
     * @param key   配置项唯一key
     * @param clazz
     * @return 配置项类型集合
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends ConfigProperties> List<T> getAllConfig(String key, Class<T> clazz) {
        List<ConfigData> configDataList = repository.findAllByKey(key);
        if (CollectionUtils.isNotEmpty(configDataList)) {
            List<T> result = Lists.newArrayListWithCapacity(configDataList.size());
            configDataList.forEach(configData -> {
                if (clazz.isAssignableFrom(configData.getClass())) {
                    result.add((T) configData);
                } else {
                    log.error("Config [{} - {}] is of type {}, but type {} is requested", configData.getObjectId(), key, configData.getClass().getName(), clazz.getName());
                    throw new IllegalStateException("Config data type mismatches!");
                }
            });
            return result;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ConfigProperties> T getConfig(String objectId, String key, Class<T> clazz) {
        final Optional<ConfigData> configData = repository.findByObjectIdAndKey(objectId, key);
        if (configData.isPresent()) {
            @NotNull final ConfigProperties data = configData.get().getData();
            if (clazz.isAssignableFrom(data.getClass())) {
                return (T) data;
            } else {
                log.error("Config [{} - {}] is of type {}, but type {} is requested", objectId, key, data.getClass().getName(), clazz.getName());
                throw new IllegalStateException("Config data type mismatches!");
            }
        }
        return null;
    }

    @Override
    public <T extends ConfigProperties> void putConfig(String objectId, String key, T properties) {
        repository.saveConfigData(ConfigData.of(objectId, key, properties));
    }
}
