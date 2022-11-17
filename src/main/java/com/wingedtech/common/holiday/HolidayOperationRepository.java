package com.wingedtech.common.holiday;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Created on 2018/12/28.
 *
 * @author ssy
 */
public interface HolidayOperationRepository {
    Holiday findOrInsert(Holiday holiday);

    List<Holiday> getFutureHolidays(LocalDate startDay, Integer days);

    Page<Holiday> search(HolidaySearchDTO searchDTO);

    boolean isHoliday(HolidayDTO holiday);

    /**
     * 获取指定时间段的数据
     *
     * @param startTime 开始时间
     * @param endTime   截止时间
     * @param pageable  分页参数
     * @return the     Page<Holiday>
     */
    Page<Holiday> findOnlyHolidayOfTime(Instant startTime, Instant endTime, Pageable pageable);
}
