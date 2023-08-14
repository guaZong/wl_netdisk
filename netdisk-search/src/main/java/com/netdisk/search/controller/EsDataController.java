package com.netdisk.search.controller;

import com.netdisk.common.core.ResponseResult;
import com.netdisk.common.enums.AppExceptionCodeMsg;
import com.netdisk.common.exception.AppException;
import com.netdisk.search.pojo.EsData;
import com.netdisk.search.repository.EsDataRepository;
import com.netdisk.search.service.EsDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * @author lsj
 * @description EsDataController
 * @createDate 2023/8/8 13:44
 */
@RestController
@RequestMapping("/sysSearch")
public class EsDataController {

    private final EsDataService esDataService;

    public EsDataController(EsDataService esDataService){
        this.esDataService=esDataService;
    }


    @GetMapping("/findByName")
    public ResponseResult findByName(String name,Integer pageNum,Integer pageSize){
        if(Objects.isNull(name)){
            throw new AppException(AppExceptionCodeMsg.NULL_VALUE);
        }
        return ResponseResult.success(esDataService.findByName(name, pageNum, pageSize));
    }


}

