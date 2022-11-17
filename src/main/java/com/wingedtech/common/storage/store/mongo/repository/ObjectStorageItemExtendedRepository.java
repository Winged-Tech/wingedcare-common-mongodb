package com.wingedtech.common.storage.store.mongo.repository;

import com.wingedtech.common.storage.store.mongo.ObjectStorageItemDocument;

import java.util.Optional;

public interface ObjectStorageItemExtendedRepository {
    ObjectStorageItemDocument store(ObjectStorageItemDocument item);
    Optional<ObjectStorageItemDocument> findUniqueItem(ObjectStorageItemDocument item);
}
