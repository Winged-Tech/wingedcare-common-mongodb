package com.wingedtech.common.service.logging;

import com.wingedtech.common.service.GenericServiceTemplateImpl;
import com.wingedtech.common.service.mapper.EntityMapper;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public class ObjectLogServiceImpl extends GenericServiceTemplateImpl<ObjectLogDTO, ObjectLog> implements ObjectLogService {

    private final ObjectLogRepository repository;

    public ObjectLogServiceImpl(ObjectLogRepository repository, EntityMapper<ObjectLogDTO, ObjectLog> mapper) {
        super(repository, mapper);
        this.repository = repository;
    }

    @Override
    public List<ObjectLogDTO> findAllByExample(ObjectLogDTO example) {
        return mapper.toDto(repository.findAll(buildExampleFromDto(example)));
    }

    @Async
    @Override
    public void withSuccessLog(Object data, String business, String objectId, Object... tag) {
        save(ObjectLogDTO.success(data, business, objectId).addTags(tag));
    }

    @Async
    @Override
    public void withErrorLog(Object data, String business, String objectId, Object inferredWrongData, Object... tag) {
        save(ObjectLogDTO.withException(data, business, objectId).addTags(tag).withInferredWrongData(inferredWrongData));
    }

    @Async
    @Override
    public void withLog(ObjectLogDTO objectLogDTO) {
        save(objectLogDTO);
    }

    @Override
    public ObjectLogDTO findLatestLogByBusiness(String business, Boolean isException) {
        return mapper.toDto(repository.findLatestLogByBusiness(business, isException));
    }

}
