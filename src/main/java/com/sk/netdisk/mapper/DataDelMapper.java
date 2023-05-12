package com.sk.netdisk.mapper;

import com.sk.netdisk.pojo.DataDel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sk.netdisk.pojo.vo.DataDelInfoVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author Administrator
* @description 针对表【data_del】的数据库操作Mapper
* @createDate 2023-05-03 15:28:35
* @Entity com.sk.netdisk.pojo.DataDel
*/
@Mapper
public interface DataDelMapper extends BaseMapper<DataDel> {
    /**
     * 遍历回收站信息
     * @param userId 用户id
     * @return List<DataDelInfoVo>
     */
    List<DataDelInfoVo> infoAllDataDel(Integer userId);
}




