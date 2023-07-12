package com.wingedtech.common.mongo.log.repository.impl;

import com.wingedtech.common.log.service.dto.OperationLogQueryDTO;
import com.wingedtech.common.mongo.log.domain.OperationLog;
import com.wingedtech.common.mongo.log.repository.OperationLogExtendRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;

import java.time.Instant;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author 6688SUN
 */
public class OperationLogExtendRepositoryImpl implements OperationLogExtendRepository {
    private static final String START_TIME_FIELD = "startTime";
    private final MongoTemplate mongoTemplate;

    public OperationLogExtendRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * 通过操作记录描述信息获取操作日志
     *
     * @param queryDTO 查询参数
     * @param pageable 分页参数
     * @return the data
     */
    @Override
    public Page<OperationLog> findByQuery(OperationLogQueryDTO queryDTO, Pageable pageable) {
        Instant startTime = queryDTO.getStartTime();
        Instant endTime = queryDTO.getEndTime();
        String searchKey = queryDTO.getDescription();

        Criteria criteria = new Criteria();
        if (startTime != null && endTime != null) {
            criteria.and(START_TIME_FIELD).gte(startTime).lte(endTime);
        } else if (startTime != null) {
            criteria.and(START_TIME_FIELD).gte(startTime);
        } else if (endTime != null) {
            criteria.and(START_TIME_FIELD).lte(endTime);
        }

        if (StringUtils.isNotBlank(searchKey)) {
            Pattern searchPattern = Pattern.compile("^.*" + searchKey + ".*$", Pattern.CASE_INSENSITIVE);
            criteria.orOperator(
                Criteria.where("description").regex(searchPattern),
                Criteria.where("userName").regex(searchPattern)
            );
        }

        long count = mongoTemplate.count(Query.query(criteria), OperationLog.class);
        List<OperationLog> content = mongoTemplate.find(Query.query(criteria).with(pageable), OperationLog.class);
        return PageableExecutionUtils.getPage(content, pageable, () -> count);
    }
}
