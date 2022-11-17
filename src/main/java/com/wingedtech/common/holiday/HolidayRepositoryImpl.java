package com.wingedtech.common.holiday;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.wingedtech.common.multitenancy.util.CriteriaUtil;
import com.wingedtech.common.time.DateTimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.support.PageableExecutionUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static com.wingedtech.common.holiday.Holiday.*;

/**
 * Created on 2018/12/28.
 *
 * @author ssy
 */
public class HolidayRepositoryImpl implements HolidayOperationRepository {

    private final MongoTemplate mongoTemplate;

    public HolidayRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Holiday findOrInsert(Holiday holiday) {
        Criteria criteriaDefinition = Criteria.where(YEAR).is(holiday.getYear())
            .and(MONTH).is(holiday.getMonth())
            .and(DAY).is(holiday.getDay())
            .and(TIME).is(holiday.getTime());
        Query query = new Query(criteriaDefinition);
        Update update = new Update()
            .setOnInsert(YEAR, holiday.getYear())
            .setOnInsert(MONTH, holiday.getMonth())
            .setOnInsert(DAY, holiday.getDay())
            .set(TIME, holiday.getTime())
            .set(HOLIDAY_TYPE, holiday.getHolidayType())
            .set(NAME, holiday.getName())
            .set("_class", holiday.getClass().getName());
        return mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true).upsert(true), Holiday.class);
    }

    @Override
    public List<Holiday> getFutureHolidays(LocalDate startDay, Integer days) {
        Instant time;
        if (startDay != null) {
            time = startDay.atStartOfDay(DateTimeUtils.SYSTEM_ZONE_ID).toInstant();
        } else {
            time = Instant.now();
        }
        Criteria criteria = Criteria.where(TIME).gte(time);
        List<Criteria> andCriteria = Lists.newArrayList();
        andCriteria.add(Criteria.where(TIME).gte(time));
        if (days != null) {
            LocalDate localDate = DateTimeUtils.instantToLocalDate(time).plusDays(days);
            andCriteria.add(Criteria.where(TIME).lte(localDate.atStartOfDay(DateTimeUtils.SYSTEM_ZONE_ID).toInstant()));
        }
        criteria = CriteriaUtil.appendsAndOperators(criteria, andCriteria);
        Query query = Query.query(criteria).with(getSort());
        return mongoTemplate.find(query, Holiday.class);
    }

    private Sort getSort() {
        return Sort.by(new Sort.Order(Sort.Direction.ASC, TIME));
    }

    @Override
    public Page<Holiday> search(HolidaySearchDTO searchDTO) {
        Criteria criteria = new Criteria();
        if (StringUtils.isNotEmpty(searchDTO.getName())) {
            criteria = criteria.and(NAME).regex(searchDTO.getName());
        }
        if (CollectionUtils.isNotEmpty(searchDTO.getHolidayTypes())) {
            criteria = criteria.and(HOLIDAY_TYPE).in(searchDTO.getHolidayTypes());
        }
        if (searchDTO.getYear() != null) {
            criteria = criteria.and(YEAR).is(searchDTO.getYear());
        }
        if (searchDTO.getMonth() != null) {
            criteria = criteria.and(MONTH).is(searchDTO.getMonth());
        }
        Query query = Query.query(criteria).with(getSort());
        Pageable pageable;
        if (searchDTO.getPageableParam() != null) {
            pageable = searchDTO.getPageableParam().toPageable();
        } else {
            pageable = Pageable.unpaged();
        }
        return CriteriaUtil.queryPage(pageable, query, Holiday.class, mongoTemplate);
    }

    @Override
    public boolean isHoliday(HolidayDTO holiday) {
        Criteria criteria = Criteria.where(HOLIDAY_TYPE).in(Lists.newArrayList(HolidayType.LEAVE_IN_LIEU, HolidayType.LEGAL_HOLIDAYS))
            .and(YEAR).is(holiday.getYear())
            .and(MONTH).is(holiday.getMonth())
            .and(DAY).is(holiday.getDay());
        return mongoTemplate.exists(Query.query(criteria), Holiday.class);
    }

    /**
     * 获取指定时间段的数据
     *
     * @param startTime 开始时间
     * @param endTime   截止时间
     * @param pageable  分页参数
     * @return the     Page<Holiday>
     */
    @Override
    public Page<Holiday> findOnlyHolidayOfTime(Instant startTime, Instant endTime, Pageable pageable) {
        Criteria criteria = Criteria.where(HOLIDAY_TYPE).ne(HolidayType.MAKE_UP_FOR_WORK);
        if (startTime != null && endTime != null) {
            criteria = criteria.and(TIME).gte(startTime).lte(endTime);
        } else if (startTime != null) {
            criteria = criteria.and(TIME).gte(startTime);
        } else if (endTime != null) {
            criteria = criteria.and(TIME).lte(endTime);
        }
        long count = mongoTemplate.count(Query.query(criteria), Holiday.class);
        List<Holiday> content = mongoTemplate.find(Query.query(criteria).with(pageable), Holiday.class);
        return PageableExecutionUtils.getPage(content, pageable, () -> count);
    }
}
