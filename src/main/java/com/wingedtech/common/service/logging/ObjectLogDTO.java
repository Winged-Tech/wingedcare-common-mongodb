package com.wingedtech.common.service.logging;

import com.google.common.collect.Lists;
import com.wingedtech.common.dto.AbstractAuditingEntityDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 通用的业务对象日志
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ObjectLogDTO extends AbstractAuditingEntityDTO {

    private static final long serialVersionUID = 4121620278379158201L;
    private String id;

    /**
     * 用于区分不同业务或不同数据模块的key
     */
    @NotBlank
    private String business;

    /**
     * 关联的业务数据id
     */
    private String objectId;

    /**
     * 当前的操作名称
     */
    private String action;

    /**
     * 数据对象
     */
    private Object data;

    /**
     * 是否是异常记录
     */
    private Boolean isException;

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

    public static ObjectLogDTO success(Object data, String business, String objectId) {
        ObjectLogDTO objectLogDTO = new ObjectLogDTO();
        objectLogDTO.setData(data);
        objectLogDTO.setIsException(false);
        objectLogDTO.setBusiness(business);
        objectLogDTO.setObjectId(objectId);
        return objectLogDTO;
    }

    public static ObjectLogDTO withException(Object data, String business, String objectId) {
        ObjectLogDTO objectLogDTO = new ObjectLogDTO();
        objectLogDTO.setData(data);
        objectLogDTO.setBusiness(business);
        objectLogDTO.setIsException(true);
        objectLogDTO.setObjectId(objectId);
        return objectLogDTO;
    }

    public ObjectLogDTO withAction(String action) {
        setAction(action);
        return this;
    }

    public ObjectLogDTO addTags(Object... tag) {
        if (tag != null && tag.length > 0) {
            for (Object o : tag) {
                if (o != null) {
                    addTag(o);
                }
            }
        }
        return this;
    }

    public ObjectLogDTO addTag(Object tag) {
        if (this.tags == null) {
            this.tags = Lists.newArrayList();
        }
        this.tags.add(tag);
        return this;
    }

    public ObjectLogDTO withBusiness(String business) {
        setBusiness(business);
        return this;
    }

    public ObjectLogDTO withMessage(String message) {
        setMessage(message);
        return this;
    }

    public ObjectLogDTO withInferredWrongData(Object data) {
        setInferredWrongData(data);
        return this;
    }
}
