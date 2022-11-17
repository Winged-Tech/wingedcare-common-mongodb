package com.wingedtech.common.holiday;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wingedtech.common.dto.AbstractAuditingEntityDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class HolidayDTO extends AbstractAuditingEntityDTO {
    private String id;
    @ApiModelProperty("年")
    private Integer year;
    @ApiModelProperty("月")
    private Integer month;
    @ApiModelProperty("日")
    private Integer day;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant time;
    @ApiModelProperty("节假日类型")
    private HolidayType holidayType;
    @ApiModelProperty("节假日名称")
    private String name;
    @JsonIgnore
    private LocalDate date;


    public boolean hasNullValue() {
        return null == year || null == month || null == day;
    }
}
