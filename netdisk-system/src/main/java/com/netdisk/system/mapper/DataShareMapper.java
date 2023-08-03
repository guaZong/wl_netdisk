package com.netdisk.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.netdisk.system.dto.ShareInfoDto;
import com.netdisk.system.pojo.DataShare;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
* @author lsj
* @description 针对表【data_share】的数据库操作Mapper
* @createDate 2022-11-29 10:50:12
* @Entity com.netdisk.system.pojo.DataShare
*/
@Mapper
public interface DataShareMapper extends BaseMapper<DataShare> {

    /**
     * 遍历我的分享
     * @param userId userId
     * @return List<ShareInfoDto>
     */
    List<ShareInfoDto> traverseShares(Integer userId);

    /**
     * 根据id查询
     * @param shareId shareId
     * @return
     */
    DataShare selectById(Integer shareId);

    /**
     * 根据id删除id
     * @param shareId shareId
     * @return boolean boolean
     */
    boolean deleteById(Integer shareId);

}




