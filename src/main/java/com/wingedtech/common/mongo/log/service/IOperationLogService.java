package com.wingedtech.common.mongo.log.service;

import com.wingedtech.common.log.service.OperationLogService;
import com.wingedtech.common.log.service.dto.OperationLogDTO;

import java.util.Optional;

/**
 * @author apple
 */
public interface IOperationLogService extends OperationLogService {

    /**
     * 获取详情
     *
     * @param id 操作日志ID
     * @return the OperationLogDTO
     */
    Optional<OperationLogDTO> findOne(String id);
}
