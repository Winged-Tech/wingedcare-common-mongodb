package com.wingedtech.common.config.providers.mongo.repository;

import com.wingedtech.common.config.providers.mongo.ConfigData;

import javax.validation.constraints.NotNull;

public interface ConfigDataOperation {
    ConfigData saveConfigData(@NotNull ConfigData data);
}
