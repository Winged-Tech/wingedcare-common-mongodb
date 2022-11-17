package com.wingedtech.common.service.logging;

import com.wingedtech.common.domain.AbstractAuditingEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 通用的业务对象日志 - MongoDB entity
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "object_logs")
public class ObjectLog extends AbstractAuditingEntity {

    private static final long serialVersionUID = 5965970734925506943L;
    @Id
    private String id;

    /**
     * 用于区分不同业务或不同数据模块的key
     */
    @NotBlank
    @Indexed
    private String business;

    /**
     * 关联的业务数据id
     */
    @Indexed
    private String objectId;

    /**
     * 当前的操作名称
     */
    @Indexed
    private String action;

    /**
     * 是否出现异常
     */
    @Indexed
    private Boolean isException;

    /**
     * 数据对象
     */
    private Object data;

    /**
     * 与当前日志相关联的消息
     */
    private String message;

    /**
     * 其他任何相关的数据标签
     */
    private List<Object> tags;

    /**
     * 可能导致错误的数据
     */
    private Object inferredWrongData;
}
