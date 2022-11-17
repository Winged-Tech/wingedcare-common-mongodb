package com.wingedtech.common.mongo.analytics.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.Instant;

/**
 * 操作日志数据结构
 *
 * @author apple
 */
@Document(collection = "event_data")
@Data
public class EventData implements Serializable {

    private static final long serialVersionUID = -8258487092299365849L;

    @Id
    private String id;

    /**
     * login
     */
    @Indexed
    private String userId;

    /**
     * 创建时间
     */
    private Instant createdTime;

    /**
     * 事件名称
     */
    private String name;

    /**
     * 请求ip
     */
    private String requestIp;

    /**
     * 请求URL
     */
    private String requestUrl;
}
