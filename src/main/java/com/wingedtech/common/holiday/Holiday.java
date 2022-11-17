package com.wingedtech.common.holiday;

import com.wingedtech.common.domain.AbstractAuditingEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = Holiday.COLLECTION_NAME)
@Getter
@Setter
@CompoundIndexes({
    @CompoundIndex(name = "time_holiday_type", def = "{'time' : -1, 'holidayType': 1}")
})
public class Holiday extends AbstractAuditingEntity {

    public static final String COLLECTION_NAME = "holiday";
    public static final String YEAR = "year";
    public static final String MONTH = "month";
    public static final String DAY = "day";
    public static final String TIME = "time";
    public static final String NAME = "name";
    public static final String HOLIDAY_TYPE = "holidayType";

    @Id
    private String id;

    @Field(value = YEAR)
    @Indexed
    private Integer year;

    @Field(value = MONTH)
    @Indexed
    private Integer month;

    @Field(value = DAY)
    @Indexed
    private Integer day;

    @Indexed(unique = true)
    @Field(value = TIME)
    private Instant time;

    @Field(value = HOLIDAY_TYPE)
    private HolidayType holidayType;

    @Field(value = NAME)
    private String name;
}
