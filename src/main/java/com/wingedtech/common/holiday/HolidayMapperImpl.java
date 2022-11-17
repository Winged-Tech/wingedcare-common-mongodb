package com.wingedtech.common.holiday;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HolidayMapperImpl implements HolidayMapper {

    @Override
    public Holiday toEntity(HolidayDTO dto) {
        if (dto == null) {
            return null;
        }
        Holiday holiday = new Holiday();
        holiday.setId(dto.getId());
        holiday.setYear(dto.getYear());
        holiday.setMonth(dto.getMonth());
        holiday.setDay(dto.getDay());
        holiday.setTime(dto.getTime());
        holiday.setName(dto.getName());
        holiday.setHolidayType(dto.getHolidayType());
        return holiday;
    }

    @Override
    public HolidayDTO toDto(Holiday entity) {
        if (entity == null) {
            return null;
        }
        HolidayDTO holidayDTO = new HolidayDTO();
        holidayDTO.setId(entity.getId());
        holidayDTO.setYear(entity.getYear());
        holidayDTO.setMonth(entity.getMonth());
        holidayDTO.setDay(entity.getDay());
        holidayDTO.setLastModifiedBy(entity.getLastModifiedBy());
        holidayDTO.setLastModifiedDate(entity.getLastModifiedDate());
        holidayDTO.setTime(entity.getTime());
        holidayDTO.setCreatedBy(entity.getCreatedBy());
        holidayDTO.setCreatedDate(entity.getCreatedDate());
        holidayDTO.setHolidayType(entity.getHolidayType());
        holidayDTO.setName(entity.getName());
        return holidayDTO;
    }

    @Override
    public List<Holiday> toEntity(List<HolidayDTO> dtoList) {
        if (dtoList == null) {
            return null;
        }

        List<Holiday> list = new ArrayList<>(dtoList.size());
        for (HolidayDTO holidayDTO : dtoList) {
            list.add(toEntity(holidayDTO));
        }

        return list;
    }

    @Override
    public List<HolidayDTO> toDto(List<Holiday> entityList) {
        if (entityList == null) {
            return null;
        }

        List<HolidayDTO> list = new ArrayList<>(entityList.size());
        for (Holiday holiday : entityList) {
            list.add(toDto(holiday));
        }

        return list;
    }
}
