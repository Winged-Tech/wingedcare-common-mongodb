package com.wingedtech.common.storage.store.mongo.repository;

import com.wingedtech.common.storage.store.mongo.ObjectStorageItemDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ObjectStorageItemRepository extends MongoRepository<ObjectStorageItemDocument, String>, ObjectStorageItemExtendedRepository {
    Optional<ObjectStorageItemDocument> findByPathIs(String path);
    Optional<ObjectStorageItemDocument> findByPathIsAndUserIdIs(String path, String userId);
    Optional<ObjectStorageItemDocument> findByResourceConfigAndName(String resource, String name);
    Optional<ObjectStorageItemDocument> findByResourceConfigAndNameAndObjectId(String resource, String name, String objectId);
}
