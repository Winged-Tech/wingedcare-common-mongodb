package com.wingedtech.common.storage.store.mongo;


import com.wingedtech.common.LoggingConfiguration;
import com.wingedtech.common.autoconfigure.storage.ObjectStorageServiceConfiguration;
import com.wingedtech.common.storage.ObjectStorageItem;
import com.wingedtech.common.storage.ObjectStorageItemStates;
import com.wingedtech.common.storage.ObjectStorageService;
import com.wingedtech.common.storage.StorageTestUtils;
import com.wingedtech.common.storage.store.mongo.ObjectStorageItemMapper;
import com.wingedtech.common.storage.store.ObjectStorageItemStore;
import com.wingedtech.common.storage.store.mongo.repository.ObjectStorageItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LoggingConfiguration.class, ObjectStorageItemMapper.class, MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, EmbeddedMongoAutoConfiguration.class, MongoObjectStorageItemStoreConfiguration.class, ObjectStorageServiceConfiguration.class})
@ActiveProfiles("storage-store")
@Slf4j
public class ObjectStorageStoreTest {

    private static final String RESOURCE = "store";

    @Autowired
    ObjectStorageItemStore storageItemStore;

    @Autowired
    ObjectStorageService objectStorageService;

    @Autowired
    ObjectStorageItemRepository objectStorageItemRepository;

    @Test
    public void testCreateAndPut() throws IOException {
        final String objectId = generateRandomObjectId();
        final String name = "test.txt";

        // 测试创建一个文件
        final String payload1 = "object1";
        final ObjectStorageItem item1 = ObjectStorageItem.put(RESOURCE, objectId, name);
        final ObjectStorageItem object = objectStorageService.createAndPutObject(item1, new DefaultPayloadSupplier(payload1));
        assertThat(object).isNotNull();
        assertThat(object.getId()).isNotBlank();
        Assertions.assertThat(StorageTestUtils.readStreamContentOneLine(objectStorageService.getObject(object))).isEqualTo(payload1);

        // 测试不存在时创建文件 - 应该不会被创建新文件
        final String payload2 = "object2";
        final ObjectStorageItem item2 = ObjectStorageItem.put(RESOURCE, objectId, name);
        final ObjectStorageItem object2 = objectStorageService.createAndPutObjectIfNotExists(item2, new DefaultPayloadSupplier(payload2));
        assertThat(object2).isNotNull();
        assertThat(object2.getId()).isEqualTo(object.getId());
        Assertions.assertThat(StorageTestUtils.readStreamContentOneLine(objectStorageService.getObject(object2))).isEqualTo(payload1);

        final Optional<ObjectStorageItem> uniqueItem = storageItemStore.findUniqueItem(ObjectStorageItem.put(RESOURCE, objectId, name));
        assertThat(uniqueItem).isPresent();
    }

    private String generateRandomObjectId() {
        return String.valueOf(Instant.now().getNano());
    }

    @Test
    public void testCreateAndPutWithException() throws IOException {
        final String objectId = generateRandomObjectId();
        final ObjectStorageItem item = ObjectStorageItem.put(RESOURCE, objectId, "test.txt");
        ObjectStorageItem object = null;
        try {
            object = objectStorageService.createAndPutObject(item, new ExceptionPayloadSupplier());
        }
        catch (Exception e) {
            object = storageItemStore.findByResourceAndName(item.getResourceConfig(), item.getName(), item.getObjectId()).orElse(null);
        }
        assertThat(object).isNotNull();
        assertThat(object.getState()).isEqualTo(ObjectStorageItemStates.FAILED);
    }

    class ExceptionPayloadSupplier implements Supplier<InputStream> {

        @Override
        public InputStream get() {
            throw new NullPointerException("Exception on purpose");
        }
    }

    class DefaultPayloadSupplier implements Supplier<InputStream> {

        String payload;

        public DefaultPayloadSupplier(String payload) {
            this.payload = payload;
        }

        @Override
        public InputStream get() {
            try {
                return StorageTestUtils.createTempFileStream(payload);
            } catch (Exception e) {
                System.err.println(e);
                return null;
            }
        }
    }
}
