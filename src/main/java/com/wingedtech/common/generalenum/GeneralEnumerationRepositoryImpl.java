package com.wingedtech.common.generalenum;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

import static com.wingedtech.common.generalenum.GeneralEnumeration.*;

/**
 * Created on 2018/12/28.
 *
 * @author ssy
 */
public class GeneralEnumerationRepositoryImpl implements GeneralEnumerationOperationRepository {

    private final MongoTemplate mongoTemplate;

    public GeneralEnumerationRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public GeneralEnumeration findOrInsert(GeneralEnumeration enumeration) {
        Criteria criteriaDefinition = Criteria.where(FIELD_TYPE).is(enumeration.getType())
            .and(FIELD_NAME).is(enumeration.getName())
            .and(FIELD_VALUE).is(enumeration.getValue());
        if (MapUtils.isNotEmpty(enumeration.getExtensionInfo())) {
            criteriaDefinition.and(FIELD_EXTENSION_INFO).is(enumeration.getExtensionInfo());
        }
        Query query = new Query(criteriaDefinition);
        Update update = new Update().setOnInsert(FIELD_TYPE, enumeration.getType())
            .setOnInsert(FIELD_NAME, enumeration.getName())
            .setOnInsert(FIELD_VALUE, enumeration.getValue())
            .setOnInsert(FIELD_EXTENSION_INFO, enumeration.getExtensionInfo())
            .set("_class", enumeration.getClass().getName());
        return mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true).upsert(true), GeneralEnumeration.class);
    }

    @Override
    public List<GeneralEnumeration> findByTypes(List<String> type) {
        Criteria criteriaDefinition = Criteria.where(FIELD_TYPE).in(type);
        return mongoTemplate.find(Query.query(criteriaDefinition), GeneralEnumeration.class);
    }

    @Override
    public boolean existsTypeByCodeOrCode(String id, String type, String name, String value) {
        Criteria criteria = Criteria.where(FIELD_TYPE).is(type);
        if (StringUtils.isNotBlank(id)) {
            criteria.and("_id").ne(id);
        }

        if (StringUtils.isNotBlank(name)) {
            criteria.and(FIELD_NAME).is(name);
        }

        if (StringUtils.isNotBlank(value)) {
            criteria.and(FIELD_VALUE).is(value);
        }
        return mongoTemplate.exists(Query.query(criteria), GeneralEnumeration.class);
    }
}
