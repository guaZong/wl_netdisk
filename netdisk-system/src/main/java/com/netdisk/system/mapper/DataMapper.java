package com.netdisk.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.netdisk.system.dto.DataDetInfoDto;
import com.netdisk.system.pojo.Data;
import com.netdisk.system.pojo.vo.DataDelInfoVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
* @author lsj
* @description 针对表【data】的数据库操作Mapper
* @createDate 2022-11-29 10:50:12
* @Entity com.netdisk.system.pojo.Data
*/
@Mapper
public interface DataMapper extends BaseMapper<Data> {
    /**
     * 删除文件
     * @param parentDataId 父文件id
     * @return Integer
     */
    Integer deleteData(Integer parentDataId);

    /**
     * 删除回收站文件
     * @param dataId 文件id
     * @return Integer
     */
    Integer finalDelData(Integer dataId);


    /**
     * 查找已经被删除的文件
     * @param dataId dataId
     * @return List<Data>
     */
    List<Data> findDelData(Integer dataId);

    /**
     * 通过id查找文件
     * @param dataId dataId
     * @return Data
     */
    Data findById(Integer dataId);

    /**
     * 通过创始人和文件id查找
     * @param dataId 文件id
     * @param userId 用户id
     * @return Data
     */
    Data findByCreateByAndId(Integer dataId, Integer userId);

    /**
     * 根据创始人和父文件id查找文件夹
     * @param parentDataId  文件id
     * @param userId 用户id
     * @return  List<Data>
     */
    List<Data> findAllByCreateByAndParentId(Integer parentDataId, Integer userId);

    /**
     * 遍历文件
     * @param parentDataId Integer父文件id
     * @param userId Integer用户id
     * @return List<DataDetInfoDto>
     */
    List<DataDetInfoDto> findListByCreateByAndParentIdInnerFileId(Integer parentDataId, Integer userId);


    /**
     * 根据创始人和父文件id查找已被删除的文件集合
     * @param parentDataId  文件id
     * @param userId 用户id
     * @return  List<Data>
     */
    List<Data> findDeleteListByCreateByAndParentId(Integer parentDataId,Integer userId);

    /**
     * 还原被删除的文件
     * @param dataId Integer dataId文件id
     * @param userId 用户id
     * @return Integer
     */
    Integer restoreDeleteData(Integer dataId,Integer userId);

    /**
     * 还原被删除的文件
     * @param parentDataId 父文件id
     * @param userId 用户id
     * @return Integer
     */
    Integer batchRestoreData(Integer parentDataId,Integer userId);

    /**
     * 批量添加文件
     * @param dataList 文件集合
     */
    void batchSaveData(List<Data> dataList);

    /**
     * 直接删除文件
     * @param dataId dataId
     * @return Integer
     */
    Integer finalDeleteData(Integer dataId);

    /**
     * 彻底批量删除父文件id是 parentDataId的所有文件
     * @param parentDataId 父文件id
     * @return Integer
     */
    Integer batchFinalDelData(Integer parentDataId);

    /**
     * 通过id集合查询名字集合
     * @param dataIds dataIds
     * @return List<String>
     */
    List<String> findNameByIds(List<Integer> dataIds);

    /**
     * 通过id集合查询名类型集合
     * @param dataIds dataIds
     * @return List<Integer>
     */
    List<Integer> findTypeByIds(List<Integer> dataIds);

    /**
     * 通过id集合查询文件集合
     * @param dataIds dataIds
     * @return List<Integer>
     */
    List<DataDetInfoDto> findDataByIds(List<Integer> dataIds);

    /**
     * 根据文件夹id遍历分享文件
     * @param parentDataId parentDataId
     * @return List<DataDetInfoDto>
     */
    List<DataDetInfoDto> visitorInfoData(Integer parentDataId);

    /**
     * 根据父级id查询子文件名字集合
     * @param parentDataId parentDataId
     * @param userId userId
     * @return List<String>
     */
    List<String> findNameByParentDataId(Integer parentDataId,Integer userId);

    /**
     * 根据父级id查询子文件id集合
     * @param parentDataId parentDataId
     * @return List<Integer>
     */
    List<Integer> findIdsByParentDataId(Integer parentDataId);

    /**
     * 通过类型查找
     * @param type type
     * @param userId userId
     * @return List<DataDetInfoDto>
     */
    List<DataDetInfoDto> findDataByType(Integer type,Integer userId);

    /**
     * 查找父级文件夹
     * @param dataId 文件id
     * @return 父级文件夹
     */
    Data findFatherDataById(Integer dataId);
}




