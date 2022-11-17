package com.wingedtech.common.mongo.log.service;

import com.wingedtech.common.log.service.dto.OperationLogDTO;
import com.wingedtech.common.mongo.log.domain.OperationLog;
import com.wingedtech.common.service.mapper.EntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author taozhou
 * @date 2021/8/11
 */
@Mapper
public interface OperationLogMapper extends EntityMapper<OperationLogDTO, OperationLog> {
    OperationLogMapper INSTANCE = Mappers.getMapper(OperationLogMapper.class);
}
