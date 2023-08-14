package com.netdisk.search.service;

import com.netdisk.search.pojo.EsData;
import com.netdisk.search.repository.EsDataRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author lsj
 * @description EsDataService
 * @createDate 2023/8/8 10:46
 */

public interface EsDataService {

    /**
     * 通过名字查询文件
     * @param name 文件名字
     * @param pageNum 当前页数
     * @param pageSize 每页大小
     * @return Page<EsData>
     */
    Page<EsData> findByName(String name, Integer pageNum,Integer pageSize);
}
