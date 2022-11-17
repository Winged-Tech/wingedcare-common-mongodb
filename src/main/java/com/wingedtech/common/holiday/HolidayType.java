package com.wingedtech.common.holiday;

import org.apache.commons.lang3.StringUtils;

public enum HolidayType {
    /**
     * 调休
     */
    LEAVE_IN_LIEU("调休"),
    /**
     * 法定节假日
     */
    LEGAL_HOLIDAYS("法定节假日"),
    /**
     * 补班
     */
    MAKE_UP_FOR_WORK("补班");

    private String chineseName;

    HolidayType(String chineseName) {
        this.chineseName = chineseName;
    }

    public static HolidayType parse(String name) {
        for (HolidayType value : HolidayType.values()) {
            if (StringUtils.equals(value.chineseName, name)) {
                return value;
            }
        }
        return null;
    }
}
