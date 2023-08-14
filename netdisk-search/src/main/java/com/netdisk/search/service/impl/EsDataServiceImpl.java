package com.netdisk.search.service.impl;

import com.netdisk.common.utils.UserUtil;
import com.netdisk.search.pojo.EsData;
import com.netdisk.search.repository.EsDataRepository;
import com.netdisk.search.service.EsDataService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author lsj
 * @description EsDataServiceImpl
 * @createDate 2023/8/8 10:47
 */
@Service
public class EsDataServiceImpl implements EsDataService {

    private final EsDataRepository esDataRepository;

    public EsDataServiceImpl(EsDataRepository esDataRepository){
        this.esDataRepository=esDataRepository;
    }


    @Override
    public Page<EsData> findByName(String name, Integer pageNum, Integer pageSize) {
        Integer userId= UserUtil.getLoginUserId();
        Pageable pageable= PageRequest.of(pageNum,pageSize);
        return esDataRepository.findByNameAndCreateByAndIsDelete(name,userId,0,pageable);
    }
}
