package com.wingedtech.common.mongo.log.service.impl;

import com.wingedtech.common.log.service.dto.OperationLogDTO;
import com.wingedtech.common.mongo.log.domain.OperationLog;
import com.wingedtech.common.mongo.log.repository.OperationLogRepository;
import com.wingedtech.common.mongo.log.service.IOperationLogService;
import com.wingedtech.common.mongo.log.service.OperationLogMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author apple
 */
@Service
@AllArgsConstructor
public class OperationLogServiceImpl implements IOperationLogService {

    private final OperationLogRepository repository;
    private final OperationLogMapper mapper;

    /**
     * 添加操作日志
     *
     * @param operationLogDTO the operationLogDTO
     */
    @Override
    public void save(OperationLogDTO operationLogDTO) {
        repository.save(mapper.toEntity(operationLogDTO));
    }

    /**
     * 获取详情
     *
     * @param id 操作日志ID
     * @return the OperationLogDTO
     */
    @Override
    public Optional<OperationLogDTO> findOne(String id) {
        return repository.findById(id).map(mapper::toDto);
    }
}
