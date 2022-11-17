package com.wingedtech.common.holiday;

import com.wingedtech.common.util.excel.annotation.ExcelField;
import lombok.Data;

@Data
public class HolidayImport {
    @ExcelField(title = "序号")
    private String index;
    @ExcelField(title = "年")
    private String year;
    @ExcelField(title = "月")
    private String month;
    @ExcelField(title = "日")
    private String day;
    @ExcelField(title = "节假日类型")
    private String holidayType;
    @ExcelField(title = "名称")
    private String name;
}
