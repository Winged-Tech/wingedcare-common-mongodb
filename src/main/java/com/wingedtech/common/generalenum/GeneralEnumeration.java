package com.wingedtech.common.generalenum;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2018/9/28.
 *
 * 修改调整字段后需要手动调整mapperImpl
 */

@Document(collection = GeneralEnumeration.COLLECTION_NAME)
@Getter
@Setter
public class GeneralEnumeration {

    private static final long serialVersionUID = -4360726745155091085L;

    public static final String COLLECTION_NAME = "general_enumeration";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_VALUE = "value";
    public static final String FIELD_SORT = "sort";
    public static final String FIELD_EXTENSION_INFO = "extension_info";

    @Id
    private String id;

    @Field(FIELD_TYPE)
    @NotNull
    @Indexed
    private String type;

    @Field(FIELD_VALUE)
    @NotNull
    private String value;

    @Field(FIELD_NAME)
    @NotNull
    private String name;

    @Field(FIELD_SORT)
    private Integer sort;

    @Field(FIELD_EXTENSION_INFO)
    private Map<String, Object> extensionInfo;

    @CreatedBy
    @Field("created_by")
    @JsonIgnore
    private String createdBy;

    @CreatedDate
    @Field("created_date")
    @JsonIgnore
    private Instant createdDate = Instant.now();

    @LastModifiedBy
    @Field("last_modified_by")
    @JsonIgnore
    private String lastModifiedBy;

    @LastModifiedDate
    @Field("last_modified_date")
    @JsonIgnore
    private Instant lastModifiedDate = Instant.now();

    public GeneralEnumeration setId(String id) {
        this.id = id;
        return this;
    }

    public GeneralEnumeration setType(String type) {
        this.type = type;
        return this;
    }

    public GeneralEnumeration setValue(String value) {
        this.value = value;
        return this;
    }

    public GeneralEnumeration setName(String name) {
        this.name = name;
        return this;
    }

    public GeneralEnumeration setExtensionInfo(Map<String, Object> extensionInfo) {
        this.extensionInfo = extensionInfo;
        return this;
    }

    public GeneralEnumeration addExtensionInfo(String key, Object value) {
        if (this.extensionInfo == null) {
            extensionInfo = new HashMap<>();
        }
        extensionInfo.put(key, value);
        return this;
    }
}
