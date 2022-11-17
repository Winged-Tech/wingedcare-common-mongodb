package com.wingedtech.common.holiday;

import com.wingedtech.common.service.GenericServiceTemplate;
import com.wingedtech.common.time.DayType;
import com.wingedtech.common.util.PageableParam;
import com.wingedtech.common.util.excel.ExcelImport;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Created on 2018/9/28.
 *
 * @author ssy
 */

public interface HolidayService extends GenericServiceTemplate<HolidayDTO> {

    boolean exist(HolidayDTO HolidayDTO);

    HolidayDTO findOrInsert(HolidayDTO HolidayDTO);

    List<HolidayDTO> insertHolidays(List<HolidayDTO> holidays);

    HolidayDTO edit(HolidayDTO holidayDTO);

    List<HolidayDTO> getFutureHolidays(LocalDate startDay, Integer days);

    void importHolidays(ExcelImport excel);

    void deleteByIds(List<String> ids);

    LocalDate calculateDaysByHoliday(DayType dayType, Integer days, LocalDate localDate);

    @Async
    void initWeekend(Integer year, List<Integer> month);

    Integer calculateRemainDays(List<HolidayDTO> holidays, LocalDate startDay, LocalDate endDay, DayType dayType);

    Page<HolidayDTO> search(HolidaySearchDTO searchDTO);

    Float calculateWorkDays(Instant start, Instant end);

    boolean isHoliday(HolidayDTO holidayDTO);

    /**
     * 获取指定时间段的数据
     *
     * @param startTime     开始时间
     * @param endTime       截止时间
     * @param pageableParam 分页参数
     * @return the     Page<HolidayDTO>
     */
    Page<HolidayDTO> findPeriodOfTime(Instant startTime, Instant endTime, PageableParam pageableParam);
}

