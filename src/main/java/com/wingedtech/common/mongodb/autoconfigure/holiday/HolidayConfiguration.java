package com.wingedtech.common.mongodb.autoconfigure.holiday;

import com.wingedtech.common.holiday.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.wingedtech.common.holiday.HolidayRepository.HOLIDAY_REPOSITORY_NAME;


/**
 * Created on 2018/12/27.
 *
 * @author ssy
 */

@Configuration
@ConditionalOnBean(name = HOLIDAY_REPOSITORY_NAME)
public class HolidayConfiguration {

    @Bean
    public HolidayMapper HolidayMapper() {
        return new HolidayMapperImpl();
    }

    @Bean
    public HolidayService HolidayService(HolidayMapper mapper, HolidayRepository HolidayRepository) {
        return new HolidayServiceImpl(HolidayRepository, mapper);
    }

    @Bean
    public HolidayResource HolidayResource(HolidayService service) {
        return new HolidayResource(service);
    }
}
