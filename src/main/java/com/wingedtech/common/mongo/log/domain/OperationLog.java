package com.wingedtech.common.mongo.log.domain;

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
@Document(collection = "operation_log")
@Data
public class OperationLog implements Serializable {
    private static final long serialVersionUID = 5071141638149947889L;

    @Id
    private String id;

    /**
     * 创建人
     */
    @Indexed
    private String createdBy;

    /**
     * 创建时间
     */
    private Instant createdTime;

    /**
     * 客户端操作系统
     */
    private String osName;

    /**
     * 客户端操作系统版本
     */
    private String osVersion;

    /**
     * 客户端浏览器的版本类型
     */
    private String userAgent;

    /**
     * 请求ip
     */
    private String requestIp;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 请求方式
     */
    private String requestType;

    /**
     * 请求参数
     */
    private String requestParam;

    /**
     * 请求执行的类路径
     */
    private String classPath;

    /**
     * 请求执行的方法名
     */
    private String methodName;

    /**
     * 请求执行的方法描述
     */
    @Indexed
    private String description;

    /**
     * 请求开始时间
     */
    private Instant startTime;

    /**
     * 请求结束时间
     */
    private Instant endTime;

    /**
     * 请求时长(毫秒)
     */
    private Long duration;

    /**
     * 请求返回信息
     */
    private String responseData;

    /**
     * http请求状态
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String error;
}
