package com.wingedtech.common.generalenum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneralEnumerationMapperImpl implements GeneralEnumerationMapper {

    @Override
    public GeneralEnumeration toEntity(GeneralEnumerationDTO dto) {
        if ( dto == null ) {
            return null;
        }

        GeneralEnumeration generalEnumeration = new GeneralEnumeration();

        generalEnumeration.setId( dto.getId() );
        generalEnumeration.setType( dto.getType() );
        generalEnumeration.setValue( dto.getValue() );
        generalEnumeration.setName( dto.getName() );
        generalEnumeration.setSort( dto.getSort() );
        Map<String, Object> map = dto.getExtensionInfo();
        if ( map != null ) {
            generalEnumeration.setExtensionInfo( new HashMap<String, Object>( map ) );
        }
        else {
            generalEnumeration.setExtensionInfo( null );
        }

        return generalEnumeration;
    }

    @Override
    public GeneralEnumerationDTO toDto(GeneralEnumeration entity) {
        if ( entity == null ) {
            return null;
        }

        GeneralEnumerationDTO generalEnumerationDTO = new GeneralEnumerationDTO();

        generalEnumerationDTO.setId( entity.getId() );
        generalEnumerationDTO.setType( entity.getType() );
        generalEnumerationDTO.setName( entity.getName() );
        generalEnumerationDTO.setValue( entity.getValue() );
        generalEnumerationDTO.setSort( entity.getSort() );
        Map<String, Object> map = entity.getExtensionInfo();
        if ( map != null ) {
            generalEnumerationDTO.setExtensionInfo( new HashMap<String, Object>( map ) );
        }
        else {
            generalEnumerationDTO.setExtensionInfo( null );
        }
        generalEnumerationDTO.setLastModifiedBy(entity.getLastModifiedBy());
        generalEnumerationDTO.setLastModifiedDate(entity.getLastModifiedDate());
        generalEnumerationDTO.setCreatedBy(entity.getCreatedBy());
        generalEnumerationDTO.setCreatedDate(entity.getCreatedDate());

        return generalEnumerationDTO;
    }

    @Override
    public List<GeneralEnumeration> toEntity(List<GeneralEnumerationDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<GeneralEnumeration> list = new ArrayList<GeneralEnumeration>( dtoList.size() );
        for ( GeneralEnumerationDTO generalEnumerationDTO : dtoList ) {
            list.add( toEntity( generalEnumerationDTO ) );
        }

        return list;
    }

    @Override
    public List<GeneralEnumerationDTO> toDto(List<GeneralEnumeration> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<GeneralEnumerationDTO> list = new ArrayList<GeneralEnumerationDTO>( entityList.size() );
        for ( GeneralEnumeration generalEnumeration : entityList ) {
            list.add( toDto( generalEnumeration ) );
        }

        return list;
    }
}
