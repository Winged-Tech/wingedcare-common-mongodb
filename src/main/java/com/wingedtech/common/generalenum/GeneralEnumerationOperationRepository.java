package com.wingedtech.common.generalenum;

import java.util.List;

/**
 * Created on 2018/12/28.
 *
 * @author ssy
 */
public interface GeneralEnumerationOperationRepository {
    GeneralEnumeration findOrInsert(GeneralEnumeration enumeration);

    List<GeneralEnumeration> findByTypes(List<String> type);

    boolean existsTypeByCodeOrCode(String id, String type, String name, String value);
}
