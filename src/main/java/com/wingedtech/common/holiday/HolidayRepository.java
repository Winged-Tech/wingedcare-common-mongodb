package com.wingedtech.common.holiday;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created on 2018/9/28.
 *
 * @author ssy
 */

@Repository(value = HolidayRepository.HOLIDAY_REPOSITORY_NAME)
public interface HolidayRepository extends MongoRepository<Holiday, String>, HolidayOperationRepository {
    String HOLIDAY_REPOSITORY_NAME = "common_holiday_repository";

    void deleteByIdIn(List<String> ids);
}
