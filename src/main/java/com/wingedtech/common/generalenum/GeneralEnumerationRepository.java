package com.wingedtech.common.generalenum;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import static com.wingedtech.common.generalenum.GeneralEnumerationRepository.GENERAL_ENUMERATION_REPOSITORY_NAME;

/**
 * Created on 2018/9/28.
 *
 * @author ssy
 */

@Repository(value = GENERAL_ENUMERATION_REPOSITORY_NAME)
public interface GeneralEnumerationRepository extends MongoRepository<GeneralEnumeration, String>, GeneralEnumerationOperationRepository {
    String GENERAL_ENUMERATION_REPOSITORY_NAME = "common_general_enumeration_repository";

    void deleteByIdAndType(String id, String type);
}
