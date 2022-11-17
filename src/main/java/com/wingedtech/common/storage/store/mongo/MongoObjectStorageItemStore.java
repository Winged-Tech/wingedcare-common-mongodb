package com.wingedtech.common.storage.store.mongo;

import com.wingedtech.common.security.SecurityUtils;
import com.wingedtech.common.service.beloging.ObjectWithUserIdServiceTemplateImpl;
import com.wingedtech.common.storage.ObjectStorageConfigService;
import com.wingedtech.common.storage.ObjectStorageItem;
import com.wingedtech.common.storage.store.ObjectStorageItemStore;
import com.wingedtech.common.storage.store.ObjectStorageItemStoreProperties;
import com.wingedtech.common.storage.store.mongo.repository.ObjectStorageItemRepository;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Optional;

@Slf4j
public class MongoObjectStorageItemStore extends ObjectWithUserIdServiceTemplateImpl<ObjectStorageItem, ObjectStorageItemDocument> implements ObjectStorageItemStore {

    private final ObjectStorageItemRepository repository;
    private final ObjectStorageItemMapper mapper;
    private final ObjectStorageItemStoreProperties objectStorageItemStoreProperties;
    private final ObjectStorageConfigService objectStorageConfigService;
    private final boolean forceLogin;

    public MongoObjectStorageItemStore(ObjectStorageItemRepository repository, ObjectStorageItemMapper mapper, ObjectStorageItemStoreProperties objectStorageItemStoreProperties, ObjectStorageConfigService objectStorageConfigService) {
        super(repository, mapper);
        this.repository = repository;
        this.mapper = mapper;
        this.objectStorageItemStoreProperties = objectStorageItemStoreProperties;
        this.forceLogin = objectStorageItemStoreProperties.isForceLogin();
        this.objectStorageConfigService = objectStorageConfigService;
    }

    @Override
    public Optional<ObjectStorageItem> findByPath(@NotBlank String path) {
        return repository.findByPathIs(path).map(mapper::toDto);
    }

    @Override
    public Optional<ObjectStorageItem> findByPathForCurrentUser(@NotBlank String path) {
        return repository.findByPathIsAndUserIdIs(path, getCurrentUserId()).map(mapper::toDto);
    }

    @Override
    public ObjectStorageItem store(ObjectStorageItem item) {
        final String currentUserId = getCurrentUserIdWithDefault();
        if (item.getUserId() == null) {
            item.setUserId(currentUserId);
        }
        item.setCreatedBy(currentUserId != null ? currentUserId : objectStorageItemStoreProperties.getDefaultLogin()) ;
        item.setLastModifiedBy(currentUserId != null ? currentUserId : objectStorageItemStoreProperties.getDefaultLogin());
        final Instant now = Instant.now();
        item.setCreatedDate(now);
        item.setLastModifiedDate(now);
        final ObjectStorageItem objectStorageItem = mapper.toDto(repository.store(mapper.toEntity(item)));
        log.debug("ObjectStorageItem stored: {}", objectStorageItem);
        return objectStorageItem;
    }

    @Override
    public boolean existsByPath(@NotBlank String path) {
        ObjectStorageItem example = new ObjectStorageItem();
        example.setPath(path);
        return this.existsByExample(example);
    }

    @Override
    public boolean existsByResourceAndName(@NotBlank String resource, @NotBlank String name, String objectId) {
        return this.existsByExample(buildExample(resource, name, objectId));
    }

    private ObjectStorageItem buildExample(@NotBlank String resource, @NotBlank String name, String objectId) {
        ObjectStorageItem example = new ObjectStorageItem();
        example.setResourceConfig(resource);
        example.setName(name);
        example.setObjectId(objectId);
        return example;
    }

    @Override
    public Optional<ObjectStorageItem> findByResourceAndName(@NotBlank String resource, @NotBlank String name, String objectId) {
        return (objectId != null ? repository.findByResourceConfigAndNameAndObjectId(resource, name, objectId) : repository.findByResourceConfigAndName(resource, name)).map(mapper::toDto);
    }

    @Override
    public Optional<ObjectStorageItem> findUniqueItem(@NotNull ObjectStorageItem item) {
        objectStorageConfigService.preprocessItemType(item);
        return repository.findUniqueItem(mapper.toEntity(item)).map(mapper::toDto);
    }

    private String getCurrentUserIdWithDefault() {
        String currentUserId = getCurrentUserId();
        return currentUserId != null ? currentUserId : objectStorageItemStoreProperties.getDefaultLogin();
    }

    @Override
    public String getCurrentUserId() {
        return SecurityUtils.getCurrentUserLoginWithException(forceLogin);
    }

    @Override
    protected ObjectStorageItem buildDefaultExample() {
        return new ObjectStorageItem();
    }
}
