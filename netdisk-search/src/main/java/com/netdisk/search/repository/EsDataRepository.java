package com.netdisk.search.repository;

import com.netdisk.search.pojo.EsData;
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
     * 通过名字查询
     * @param name name
     * @return List<EsData>
     */
    List<EsData> findByName(String name);
}
