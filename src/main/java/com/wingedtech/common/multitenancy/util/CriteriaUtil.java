package com.wingedtech.common.multitenancy.util;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author ssy
 * @date 2019/4/2 18:22
 */
public class CriteriaUtil {

    public static Criteria buildCriteriaByExample(Example example) {
        Criteria criteria;
        if (example != null) {
            criteria = Criteria.byExample(example);
        } else {
            criteria = new Criteria();
        }
        return criteria;
    }

    public static Criteria appendsAndOperators(Criteria criteria, List<Criteria> andOperator) {
        Criteria[] andOperatorArray = new Criteria[andOperator.size()];
        criteria = criteria.andOperator(andOperator.toArray(andOperatorArray));
        return criteria;
    }

    public static Criteria buildOrOperators(List<Criteria> orOperator) {
        Criteria[] andOperatorArray = new Criteria[orOperator.size()];
        return new Criteria().orOperator(orOperator.toArray(andOperatorArray));
    }

    public static <T> Page<T> queryPage(Pageable pageable, Query query, Class<T> tClass, MongoTemplate mongoTemplate) {
        final long count = mongoTemplate.count(query, tClass);
        List<T> content;
        if (count > 0L) {
            content = mongoTemplate.find(query.with(pageable), tClass);
        } else {
            content = Collections.emptyList();
        }
        return PageableExecutionUtils.getPage(content, pageable, () -> count);
    }

    public static <T> Page<T> queryPage(Pageable pageable, Criteria criteria, Class<T> tClass, MongoTemplate mongoTemplate) {
        Query query = Query.query(criteria);
        return queryPage(pageable, query, tClass, mongoTemplate);
    }
}

