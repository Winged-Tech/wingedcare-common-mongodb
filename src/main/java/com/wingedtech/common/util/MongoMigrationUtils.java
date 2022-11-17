package com.wingedtech.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.wingedtech.common.util.ClassLoaderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * mongo相关db migration工具
 */
@Slf4j
public class MongoMigrationUtils {

    public static final String RESOURCE_PATH = "dbmigrations/";

    /**
     * 从指定的json资源文件中加载对象数据，并且将所有对象存入数据库
     *
     * @param mongoTemplate
     * @param objectMapper
     * @param resourceName
     * @param <E>           对象entity类
     * @throws IOException
     */
    public static <E> void importObjectsFromJson(MongoTemplate mongoTemplate, ObjectMapper objectMapper, TypeReference<List<E>> listTypeReference, String resourceName) throws IOException {
        List<E> objects = loadObjectsFromJson(objectMapper, listTypeReference, resourceName);
        for (E object : objects) {
            log.info("Saving object: {}", object);
            mongoTemplate.save(object);
        }
    }

    /**
     * 从指定的json资源文件中加载对象list并返回
     *
     * @param objectMapper
     * @param resourceName
     * @param <E>          对象entity类
     * @return
     * @throws IOException
     */
    public static <E> List<E> loadObjectsFromJson(ObjectMapper objectMapper, TypeReference<List<E>> listTypeReference, String resourceName) throws IOException {
        URL url = ClassLoaderUtils.findResource(RESOURCE_PATH + resourceName);
        if (url != null) {
            List<E> objects = objectMapper.readValue(url, listTypeReference);
            return Lists.newArrayList(objects);
        }
        return null;
    }
}
