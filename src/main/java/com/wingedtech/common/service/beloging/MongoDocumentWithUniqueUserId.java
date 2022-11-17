package com.wingedtech.common.service.beloging;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 实现{@link ObjectWithUserId}的Mongo entity文档对象
 *
 * @author Jason
 * @since 2019-04-01 10:48
 */
@Data
public class MongoDocumentWithUniqueUserId implements ObjectWithUserId, Serializable {

    private static final long serialVersionUID = 7302713554166675430L;

    /**
     * 该记录所属用户的id，默认情况下是userLogin
     */
    @NotNull
    @Indexed(unique = true)
    private String userId;
}
