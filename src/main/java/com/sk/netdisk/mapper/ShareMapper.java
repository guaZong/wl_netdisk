package com.sk.netdisk.mapper;

import com.sk.netdisk.pojo.Share;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author Administrator
* @description 针对表【share】的数据库操作Mapper
* @createDate 2023-05-14 08:46:56
* @Entity com.sk.netdisk.pojo.Share
*/
@Mapper
public interface ShareMapper extends BaseMapper<Share> {

    /**
     * 批量插入
     * @param shareList shareList
     */
    void batchSaveShare(List<Share> shareList);

    /**
     * 删除shareId下的所有记录
     * @param shareId shareId
     */
    void deleteByShareId(Integer shareId);

    /**
     * 通过shareId查找存在多少id
     * @param shareId
     * @return
     */
    List<Integer> selectIdsByShareId(Integer shareId);
}




