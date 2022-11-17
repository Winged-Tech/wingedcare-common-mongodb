package com.wingedtech.common.storage.store.mongo;

import com.wingedtech.common.service.mapper.EntityMapper;
import com.wingedtech.common.storage.ObjectStorageItem;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface ObjectStorageItemMapper extends EntityMapper<ObjectStorageItem, ObjectStorageItemDocument> {
    ObjectStorageItemMapper INSTANCE = Mappers.getMapper(ObjectStorageItemMapper.class);
}
