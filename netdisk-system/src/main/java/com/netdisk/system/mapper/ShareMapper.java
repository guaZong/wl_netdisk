package com.netdisk.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.netdisk.system.pojo.Share;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author lsj
* @description 针对表【share】的数据库操作Mapper
* @createDate 2023-05-14 08:46:56
* @Entity com.netdisk.system.pojo.Share
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
     * 通过shareId查找存在多少DataId
     * @param shareId shareId
     * @return List<Integer>
     */
    List<Integer> selectIdsByShareId(Integer shareId);

    /**
     * 根据文件id删除分享数据
     * @param dataId
     */
    void deleteByDataId(Integer dataId);


}




