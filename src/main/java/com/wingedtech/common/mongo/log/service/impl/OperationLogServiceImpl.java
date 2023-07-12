package com.wingedtech.common.mongo.log.service.impl;

import com.wingedtech.common.log.service.OperationLogService;
import com.wingedtech.common.log.service.dto.OperationLogDTO;
import com.wingedtech.common.log.service.dto.OperationLogQueryDTO;
import com.wingedtech.common.mongo.log.event.OperationLogEvent;
import com.wingedtech.common.mongo.log.repository.OperationLogRepository;
import com.wingedtech.common.mongo.log.service.OperationLogMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author apple
 */
@Service
@AllArgsConstructor
public class OperationLogServiceImpl implements OperationLogService, ApplicationEventPublisherAware {

    private final OperationLogRepository repository;
    private final OperationLogMapper mapper;
    private ApplicationEventPublisher applicationEventPublisher;


    /**
     * 添加操作日志
     *
     * @param operationLogDTO the operationLogDTO
     */
    @Override
    public void save(OperationLogDTO operationLogDTO) {
        applicationEventPublisher.publishEvent(new OperationLogEvent(this, operationLogDTO));
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

    /**
     * 通过操作记录描述信息获取操作日志
     *
     * @param queryDTO 查询参数
     * @param pageable 分页参数
     * @return the data
     */
    @Override
    public Page<OperationLogDTO> findByQuery(OperationLogQueryDTO queryDTO, Pageable pageable) {
        return repository.findByQuery(queryDTO, pageable).map(mapper::toDto);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
