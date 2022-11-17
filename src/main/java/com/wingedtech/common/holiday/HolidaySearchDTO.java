package com.wingedtech.common.holiday;

import com.wingedtech.common.util.PageableParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class HolidaySearchDTO {
    @ApiModelProperty("查询年")
    private Integer year;
    @ApiModelProperty("查询月")
    private Integer month;
    @ApiModelProperty("查询节假日名称")
    private String name;
    @ApiModelProperty("查询节假日类型")
    private List<HolidayType> holidayTypes;
    @ApiModelProperty("分页参数")
    private PageableParam pageableParam;
}
