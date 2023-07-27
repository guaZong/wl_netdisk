package com.sk.netdisk.mapper;

import com.sk.netdisk.pojo.Data;
import com.sk.netdisk.pojo.QuickData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author lsj
* @description 针对表【quick_data】的数据库操作Mapper
* @createDate 2023-05-23 09:22:35
* @Entity com.sk.netdisk.pojo.QuickData
*/
@Mapper
public interface QuickDataMapper extends BaseMapper<QuickData> {
    /**
     * 通过用户查找文件
     * @param userId userId
     * @return List<Data>
     */
    List<Data> findDataByUserId(Integer userId);

    /**
     * 通过id集合查询快速遍历集合
     * @param dataIds dataIds
     * @param userId userId
     * @return List<QuickData>
     */
    List<QuickData> findUserIdAndDataIdByIdList(List<Integer> dataIds,Integer userId);


}




