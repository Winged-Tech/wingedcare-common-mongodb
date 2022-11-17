package com.wingedtech.common.generalenum;

import com.wingedtech.common.service.GenericServiceTemplate;

import java.util.List;
import java.util.Optional;

/**
 * Created on 2018/9/28.
 *
 * @author ssy
 */
public interface GeneralEnumerationService extends GenericServiceTemplate<GeneralEnumerationDTO> {

    List<GeneralEnumerationDTO> findEnumerationItemsByType(String type);

    GeneralEnumerationDTO save(GeneralEnumerationDTO generalEnumerationDTO, String type);

    boolean exist(GeneralEnumerationDTO generalEnumerationDTO, String type);

    GeneralEnumerationDTO findOrInsert(GeneralEnumerationDTO generalEnumerationDTO, String type);

    Optional<GeneralEnumerationDTO> findOne(GeneralEnumerationDTO generalEnumerationDTO, String type);

    void delete(String id, String type);

    Optional<GeneralEnumerationDTO> findOneByExample(GeneralEnumerationDTO generalEnumerationDTO, String type);

    List<GeneralEnumerationDTO> findByExample(GeneralEnumerationDTO example, String type);

    List<GeneralEnumerationDTO> findByTypes(String... types);
}

