package com.wingedtech.common.service.logging;

import com.wingedtech.common.service.GenericServiceTemplate;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface ObjectLogService extends GenericServiceTemplate<ObjectLogDTO> {
    List<ObjectLogDTO> findAllByExample(ObjectLogDTO example);

    void withSuccessLog(Object data, String business, String objectId, Object... tag);

    void withErrorLog(Object data, String business, String objectId, Object inferredWrongData, Object... tag);

    void withLog(ObjectLogDTO objectLogDTO);

    /**
     * 根据不同业务(非空)查询最新一条的记录
     * @param isException 是否为有异常记录,可以为空 true为异常记录
     */
    ObjectLogDTO findLatestLogByBusiness(String business, Boolean isException);
}
