package com.netdisk.search.service.impl;

import com.netdisk.search.pojo.EsData;
import com.netdisk.search.repository.EsDataRepository;
import com.netdisk.search.service.EsDataService;
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
    public EsData save(EsData esData) {
        return esDataRepository.save(esData);
    }
}
