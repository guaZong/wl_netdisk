package com.netdisk.search.repository;

import com.netdisk.search.pojo.EsData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lsj
 * @description EsDataRepository
 * @createDate 2023/8/8 9:29
 */

@Repository
public interface EsDataRepository extends ElasticsearchRepository<EsData,Long> {
    /**
     * 分页通过名字查询文件
     * @param name 文件名
     * @param userId 当前操作用户
     * @param pageable 分页
     * @param isDelete 是否删除,0未删除,1已删除
     * @return Page<EsData>
     */
    Page<EsData> findByNameAndCreateByAndIsDelete(String name, Integer userId,Integer isDelete, Pageable pageable);
}
