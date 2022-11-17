package com.wingedtech.common.multitenancy.service.information.mongo;

import com.wingedtech.common.multitenancy.Constants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author taozhou
 * @date 2021/4/22
 */
@Data
@ConfigurationProperties(prefix = Constants.CONFIG_MULTITENANCY_ROOT + ".information.mongo")
public class MongoTenantInformationStoreProperties {

}
