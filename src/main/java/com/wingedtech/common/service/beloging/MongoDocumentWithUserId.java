package com.wingedtech.common.service.beloging;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import java.io.Serializable;

/**
 * 实现ObjectWithUserId的Mongo entity文档对象
 */
@Data
public class MongoDocumentWithUserId implements ObjectWithUserId, Serializable {

    private static final long serialVersionUID = 1L;

    public static final String FIELD_USER_ID = "userId";

    /**
     * 该记录所属用户的id，默认情况下是userLogin
     */
    @Indexed
    private String userId;
}
