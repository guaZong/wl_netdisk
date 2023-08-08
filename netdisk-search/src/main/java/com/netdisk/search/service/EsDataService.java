package com.netdisk.search.service;

import com.netdisk.search.pojo.EsData;

/**
 * @author lsj
 * @description EsDataService
 * @createDate 2023/8/8 10:46
 */

public interface EsDataService {
    /**
     * 将数据存入es中
     * @param esData 数据体
     * @return EsData
     */
    EsData save(EsData esData);
}
