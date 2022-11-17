package com.wingedtech.common.service.logging;

import java.util.ArrayList;
import java.util.List;

public class ObjectLogMapperImpl implements ObjectLogMapper {

    @Override
    public ObjectLog toEntity(ObjectLogDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ObjectLog objectLog = new ObjectLog();

        objectLog.setCreatedBy( dto.getCreatedBy() );
        objectLog.setCreatedDate( dto.getCreatedDate() );
        objectLog.setLastModifiedBy( dto.getLastModifiedBy() );
        objectLog.setLastModifiedDate( dto.getLastModifiedDate() );
        objectLog.setId( dto.getId() );
        objectLog.setBusiness( dto.getBusiness() );
        objectLog.setObjectId( dto.getObjectId() );
        objectLog.setAction( dto.getAction() );
        objectLog.setIsException( dto.getIsException() );
        objectLog.setData( dto.getData() );
        objectLog.setMessage( dto.getMessage() );
        List<Object> list = dto.getTags();
        if ( list != null ) {
            objectLog.setTags( new ArrayList<Object>( list ) );
        }
        else {
            objectLog.setTags( null );
        }
        objectLog.setInferredWrongData( dto.getInferredWrongData() );

        return objectLog;
    }

    @Override
    public ObjectLogDTO toDto(ObjectLog entity) {
        if ( entity == null ) {
            return null;
        }

        ObjectLogDTO objectLogDTO = new ObjectLogDTO();

        objectLogDTO.setCreatedBy( entity.getCreatedBy() );
        objectLogDTO.setCreatedDate( entity.getCreatedDate() );
        objectLogDTO.setLastModifiedBy( entity.getLastModifiedBy() );
        objectLogDTO.setLastModifiedDate( entity.getLastModifiedDate() );
        objectLogDTO.setId( entity.getId() );
        objectLogDTO.setBusiness( entity.getBusiness() );
        objectLogDTO.setObjectId( entity.getObjectId() );
        objectLogDTO.setAction( entity.getAction() );
        objectLogDTO.setData( entity.getData() );
        objectLogDTO.setIsException( entity.getIsException() );
        objectLogDTO.setMessage( entity.getMessage() );
        List<Object> list = entity.getTags();
        if ( list != null ) {
            objectLogDTO.setTags( new ArrayList<Object>( list ) );
        }
        else {
            objectLogDTO.setTags( null );
        }
        objectLogDTO.setInferredWrongData( entity.getInferredWrongData() );

        return objectLogDTO;
    }

    @Override
    public List<ObjectLog> toEntity(List<ObjectLogDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<ObjectLog> list = new ArrayList<ObjectLog>( dtoList.size() );
        for ( ObjectLogDTO objectLogDTO : dtoList ) {
            list.add( toEntity( objectLogDTO ) );
        }

        return list;
    }

    @Override
    public List<ObjectLogDTO> toDto(List<ObjectLog> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<ObjectLogDTO> list = new ArrayList<ObjectLogDTO>( entityList.size() );
        for ( ObjectLog objectLog : entityList ) {
            list.add( toDto( objectLog ) );
        }

        return list;
    }
}
