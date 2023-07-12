package com.wingedtech.common.mongo.log.event;

import com.wingedtech.common.log.service.dto.OperationLogDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEvent;

import javax.annotation.Nullable;

/**
 * @author 6688SUN
 */
public class OperationLogEvent extends ApplicationEvent {

    private static final long serialVersionUID = -5159918254574227249L;
    /**
     * 创建人
     */
    private OperationLogDTO operationLogDTO;

    public OperationLogEvent(Object source, OperationLogDTO operationLogDTO) {
        super(source);
        this.operationLogDTO = operationLogDTO;
    }

    public void additional(String userName, @Nullable String desAppend) {
        this.operationLogDTO.setUserName(userName);
        if (StringUtils.isNotBlank(desAppend)) {
            this.operationLogDTO.setDescription(this.operationLogDTO.getDescription() + desAppend);
        }
    }

    public String getDescription() {
        return this.operationLogDTO.getDescription();
    }

    public String getRequestParam() {
        return this.operationLogDTO.getRequestParam();
    }

    public String getResponseData() {
        return this.operationLogDTO.getResponseData();
    }
}
