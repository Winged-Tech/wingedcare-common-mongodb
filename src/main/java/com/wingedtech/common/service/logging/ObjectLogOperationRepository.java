package com.wingedtech.common.service.logging;

/**
 * @author ssy
 * @date 2019/8/9 15:47
 */
public interface ObjectLogOperationRepository {
    ObjectLog findLatestLogByBusiness(String business, Boolean isException);
}
