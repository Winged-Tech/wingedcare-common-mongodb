package com.wingedtech.common.mongo.analytics.service.impl;

import com.wingedtech.common.analytics.service.dto.EventDataDTO;
import com.wingedtech.common.mongo.analytics.domain.EventData;
import com.wingedtech.common.mongo.analytics.service.AnalyticsService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final MongoTemplate mongoTemplate;

    public AnalyticsServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * 保存
     *
     * @param eventDataDTO 源数据
     */
    @Override
    public void save(EventDataDTO eventDataDTO) {
        EventData entity = new EventData();
        BeanUtils.copyProperties(eventDataDTO, entity);
        mongoTemplate.save(entity);
    }
}
