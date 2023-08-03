package com.netdisk.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.netdisk.system.pojo.DataDel;
import com.netdisk.system.pojo.vo.DataDelInfoVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author lsj
* @description 针对表【data_del】的数据库操作Mapper
* @createDate 2023-05-03 15:28:35
* @Entity com.netdisk.system.pojo.DataDel
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




