package com.wingedtech.common.mongo.log.repository;

import com.wingedtech.common.log.service.dto.OperationLogQueryDTO;
import com.wingedtech.common.mongo.log.domain.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author 6688SUN
 */
public interface OperationLogExtendRepository {
    /**
     * 通过操作记录描述信息获取操作日志
     *
     * @param queryDTO 查询参数
     * @param pageable 分页参数
     * @return the data
     */
    Page<OperationLog> findByQuery(OperationLogQueryDTO queryDTO, Pageable pageable);
}
