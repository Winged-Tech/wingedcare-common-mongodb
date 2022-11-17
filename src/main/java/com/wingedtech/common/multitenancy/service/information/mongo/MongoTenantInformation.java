package com.wingedtech.common.multitenancy.service.information.mongo;

import com.wingedtech.common.multitenancy.domain.TenantInformation;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author taozhou
 * @date 2021/4/22
 */
@Document(collection = Constants.COLLECTION)
@Data
public class MongoTenantInformation {

    @Id
    private String id;

    private TenantInformation tenant;
}
