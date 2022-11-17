package com.wingedtech.common.holiday;

import com.google.common.collect.Lists;
import com.wingedtech.common.errors.BusinessException;
import com.wingedtech.common.service.GenericServiceTemplateImpl;
import com.wingedtech.common.time.DateTimeUtils;
import com.wingedtech.common.time.DayType;
import com.wingedtech.common.util.PageableParam;
import com.wingedtech.common.util.excel.ExcelImport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2018/9/28.
 *
 * @author ssy
 */
@Service
@Slf4j
public class HolidayServiceImpl extends GenericServiceTemplateImpl<HolidayDTO, Holiday> implements HolidayService {

    private final HolidayRepository repository;

    private final HolidayMapper mapper;

    public HolidayServiceImpl(HolidayRepository repository, HolidayMapper mapper) {
        super(repository, mapper);
        this.repository = repository;
        this.mapper = mapper;
    }


    @Override
    public HolidayDTO save(final HolidayDTO holidayDTO) {
        final Holiday entity = mapper.toEntity(holidayDTO);
        final String id = entity.getId();
        if (id != null) {
            repository.findById(id).ifPresent(existingEntity -> {
                entity.setCreatedBy(existingEntity.getCreatedBy());
                entity.setCreatedDate(existingEntity.getCreatedDate());
            });
        }
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public boolean exist(HolidayDTO holidayDTO) {
        Example<Holiday> HolidayExample = buildExample(mapper.toEntity(holidayDTO));
        return repository.exists(HolidayExample);
    }

    @Override
    public HolidayDTO findOrInsert(HolidayDTO holidayDTO) {
        validate(holidayDTO);
        return findOrInsertWithoutCheck(holidayDTO);
    }

    private void validate(HolidayDTO holidayDTO) {
        if (holidayDTO == null || holidayDTO.hasNullValue()) {
            throw new BusinessException("年，月，日不能为空");
        }
        initTime(holidayDTO);
    }

    private HolidayDTO findOrInsertWithoutCheck(HolidayDTO holidayDTO) {
        return mapper.toDto(repository.findOrInsert(mapper.toEntity(holidayDTO)));
    }

    @Override
    public List<HolidayDTO> insertHolidays(List<HolidayDTO> holidays) {
        if (CollectionUtils.isEmpty(holidays)) {
            throw new BusinessException("保存节假日为空!");
        }
        holidays.forEach(this::validate);
        List<HolidayDTO> results = Lists.newArrayList();
        holidays.forEach(holiday -> {
            HolidayDTO result = findOrInsertWithoutCheck(holiday);
            results.add(result);
        });
        return results;
    }

    @Override
    public HolidayDTO edit(HolidayDTO holidayDTO) {
        validate(holidayDTO);
        try {
            return save(holidayDTO);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("已录入相同节假日!请检查输入日期是否正确");
        }
    }

    // 默认初始化节假日为正午
    private void initTime(HolidayDTO holidayDTO) {
        try {
            Instant instant = LocalDateTime.of(holidayDTO.getYear(), holidayDTO.getMonth(), holidayDTO.getDay(), 12, 0).atZone(DateTimeUtils.SYSTEM_ZONE_ID).toInstant();
            holidayDTO.setTime(instant);
        } catch (DateTimeException e) {
            throw new BusinessException("当前输入日期不合法!请检查输入日期是否正确");
        }
    }

    @Override
    public List<HolidayDTO> getFutureHolidays(LocalDate startDay, Integer days) {
        List<HolidayDTO> futureHolidays = mapper.toDto(repository.getFutureHolidays(startDay, days));
        if (CollectionUtils.isNotEmpty(futureHolidays)) {
            futureHolidays.forEach(holiday -> holiday.setDate(holidayToLocalDate(holiday)));
            return futureHolidays;
        }
        return Lists.newArrayList();
    }

    private LocalDate holidayToLocalDate(HolidayDTO holiday) {
        return holiday.getTime().atZone(DateTimeUtils.SYSTEM_ZONE_ID).toLocalDate();
    }

    @Override
    public void importHolidays(ExcelImport excel) {
        List<HolidayImport> dataListByTitle;
        try {
            dataListByTitle = excel.getDataListByTitle(HolidayImport.class, true);
        } catch (Exception e) {
            throw new BusinessException("表格解析失败!");
        }
        List<HolidayDTO> holidays = dataListByTitle.stream().filter(holidayImport -> StringUtils.isNotEmpty(holidayImport.getIndex())).map(this::toHolidayDTO).collect(Collectors.toList());
        insertHolidays(holidays);
    }

    @Override
    public void deleteByIds(List<String> ids) {
        log.debug("delete holidays by:{}", ids);
        repository.deleteByIdIn(ids);
    }

    private HolidayDTO toHolidayDTO(HolidayImport holidayImport) {
        String index = holidayImport.getIndex();
        String message = "序号" + index + "的记录";
        String year = holidayImport.getYear();
        String month = holidayImport.getMonth();
        String day = holidayImport.getDay();
        String strHolidayType = holidayImport.getHolidayType();
        HolidayType holidayType = HolidayType.parse(strHolidayType);
        if (holidayType == null) {
            throw new BusinessException(message + "节假日类型错误!");
        }
        if (holidayImport.getName() == null) {
            throw new BusinessException(message + "节假日名称不能为空!");
        }
        Integer intYear = ExcelImport.parseIntIfNotEmpty(year, message, true);
        Integer intMonth = ExcelImport.parseIntIfNotEmpty(month, message, true);
        Integer intDay = ExcelImport.parseIntIfNotEmpty(day, message, true);
        HolidayDTO holidayDTO = new HolidayDTO();
        holidayDTO.setDay(intDay);
        holidayDTO.setMonth(intMonth);
        holidayDTO.setYear(intYear);
        holidayDTO.setHolidayType(holidayType);
        holidayDTO.setName(holidayImport.getName());
        return holidayDTO;
    }

    @Override
    public LocalDate calculateDaysByHoliday(DayType dayType, Integer days, LocalDate localDate) {
        List<HolidayDTO> futureHolidays = getFutureHolidays(localDate, days + 40);
        if (futureHolidays.isEmpty()) {
            return DateTimeUtils.calculateDays(dayType, days, localDate);
        }
        while (days > 0) {
            boolean notHasHoliday = true;
            if (!futureHolidays.isEmpty() && !DayType.NATURAL_DAY.equals(dayType)) {
                if (localDate.isEqual((futureHolidays.get(0).getDate()))) {
                    HolidayDTO currHoliday = futureHolidays.remove(0);
                    if (!HolidayType.MAKE_UP_FOR_WORK.equals(currHoliday.getHolidayType())) {
                        notHasHoliday = false;
                    }
                }
            }
            localDate = localDate.plusDays(1L);
            if (notHasHoliday) {
                --days;
            }
            if (futureHolidays.isEmpty()) {
                futureHolidays = getFutureHolidays(localDate, 40);
            }
        }
        return localDate;
    }

    @Override
    @Async
    public void initWeekend(Integer year, List<Integer> month) {
        List<Integer> monthSeed;
        if (CollectionUtils.isNotEmpty(month)) {
            monthSeed = month;
        } else {
            monthSeed = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        }
        monthSeed.forEach(integer -> initWeekend(year, integer));
    }

    private void initWeekend(Integer year, Integer month) {
        log.debug("初始化[" + year + "]年[" + month + "]月双修节假日");
        LocalDate seed;
        try {
            seed = LocalDate.of(year, month, 1);
        } catch (DateTimeException e) {
            throw new BusinessException("当前输入日期合法!请检查输入日期是否正确");
        }
        List<HolidayDTO> weekends = Lists.newArrayList();
        while (seed.getMonthValue() == month) {
            DayOfWeek dayOfWeek = seed.getDayOfWeek();
            if (DayOfWeek.SATURDAY.equals(dayOfWeek) || DayOfWeek.SUNDAY.equals(dayOfWeek)) {
                HolidayDTO holidayDTO = new HolidayDTO();
                holidayDTO.setYear(year);
                holidayDTO.setMonth(month);
                holidayDTO.setDay(seed.getDayOfMonth());
                holidayDTO.setName("周末");
                holidayDTO.setHolidayType(HolidayType.LEGAL_HOLIDAYS);
                weekends.add(holidayDTO);
            }
            seed = seed.plusDays(1L);
        }
        insertHolidays(weekends);
    }

    @Override
    public Integer calculateRemainDays(List<HolidayDTO> holidays, LocalDate startDay, LocalDate endDay, DayType dayType) {
        LocalDate seed;
        LocalDate target;
        boolean isExpired;
        if (startDay.isEqual(endDay)) {
            return 0;
        }
        if (startDay.isBefore(endDay)) {
            seed = startDay;
            target = endDay;
            isExpired = false;
        } else {
            seed = endDay;
            target = startDay;
            isExpired = true;
        }
        int holidayIndex = 0;
        if (CollectionUtils.isNotEmpty(holidays)) {
            for (int i = 0; i < holidays.size(); i++) {
                HolidayDTO holiday = holidays.get(i);
                if (holiday.getDate().isAfter(seed) || holiday.getDate().isEqual(seed)) {
                    holidayIndex = i;
                    break;
                }
            }
        } else {
            holidayIndex = -1;
        }
        Integer days = 0;
        boolean hasMoreHoliday = true;
        while (seed.isBefore(target)) {
            if (holidayIndex != -1 && holidayIndex < holidays.size() && seed.isEqual(holidays.get(holidayIndex).getDate())) {
                if (HolidayType.MAKE_UP_FOR_WORK.equals(holidays.get(holidayIndex).getHolidayType())) {
                    days++;
                } else if (DayType.NATURAL_DAY.equals(dayType)) {
                    days++;
                }
                holidayIndex++;
            } else {
                days++;
            }
            seed = seed.plusDays(1);
            if (holidayIndex == holidays.size() && hasMoreHoliday) {
                List<HolidayDTO> futureHolidays = getFutureHolidays(seed, 30);
                if (CollectionUtils.isNotEmpty(futureHolidays)) {
                    holidays.addAll(futureHolidays);
                } else {
                    hasMoreHoliday = false;
                }
            }
        }
        if (isExpired) {
            return -days;
        }
        return days;
    }

    @Override
    public Page<HolidayDTO> search(HolidaySearchDTO searchDTO) {
        return repository.search(searchDTO).map(mapper::toDto);
    }

    @Override
    public Float calculateWorkDays(Instant start, Instant end) {
        final Page<HolidayDTO> periodOfTime = findPeriodOfTime(start, end, PageableParam.unpaged());
        final Instant instant = end.minusSeconds(start.getEpochSecond());
        float days = instant.getEpochSecond() * 1.0f / 86400;;
        if (CollectionUtils.isNotEmpty(periodOfTime.getContent())) {
            days = days - periodOfTime.getContent().size();
        }
        return days;
    }

    @Override
    public boolean isHoliday(HolidayDTO holiday) {
        return repository.isHoliday(holiday);
    }

    /**
     * 获取指定时间段的数据
     *
     * @param startTime     开始时间
     * @param endTime       截止时间
     * @param pageableParam 分页参数
     * @return the     Page<HolidayDTO>
     */
    @Override
    public Page<HolidayDTO> findPeriodOfTime(Instant startTime, Instant endTime, PageableParam pageableParam) {
        return repository.findOnlyHolidayOfTime(startTime, endTime, pageableParam.toPageable()).map(mapper::toDto);
    }
}
