package com.sk.netdisk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sk.netdisk.pojo.Data;
import com.sk.netdisk.pojo.dto.DataDetInfoDto;
import com.sk.netdisk.pojo.dto.DataPathDto;
import com.sk.netdisk.pojo.vo.DataInfoVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * @author Administrator
 * @description 针对表【data】的数据库操作Service
 * @createDate 2022-11-29 10:50:12
 */
public interface DataService extends IService<Data> {

    /**
     * 根据父id遍历文件夹
     *
     * @param parentDataId 父文件夹id
     * @return List<DataDetInfoDto>
     */
    List<DataDetInfoDto> traverseDataByParentId(Integer parentDataId);

    /**
     * 根据type类型遍历文件
     *
     * @param type type类型
     * @return List<DataDetInfoDto>
     */
    List<DataDetInfoDto> traverseDataByType(Integer type);

    /**
     * 获取文件详细信息
     *
     * @param dataId 文件id
     * @return DataInfoVo
     */
    DataInfoVo getDataDetail(Integer dataId);

    /**
     * 通过当前文件id获取上一个文件id
     *
     * @param nowDataId 当前文件id
     * @return Integer
     */
    Integer getParentDataId(Integer nowDataId);

    /**
     * 返回文件目录
     *
     * @param folderId 目录id
     * @return List<Data>
     */
    List<Data> getDataFolder(Integer folderId);


    /**
     * 获取文件路径
     *
     * @param dataId 当前文件id
     * @return List<DataPathDto>
     */
    List<DataPathDto> getDataPath(Integer dataId);


    /**
     * 无权限获取当前路径
     *
     * @param dataId   dataId
     * @param shareId  shareId
     * @param passCode passCode
     * @return List<DataPathDto>
     */
    List<DataPathDto> getDataPath(Integer dataId, Integer shareId, String passCode);

    /**
     * 排序文件
     *
     * @param sortType  排序类型
     * @param sortOrder 排序方式
     */
    void setSortNum(Integer sortType, Integer sortOrder);

    /**
     * 创建文件夹
     *
     * @param parentDataId 父文件夹id
     * @param folderName   文件夹名字
     * @return Data
     */
    Data createFolder(Integer parentDataId, String folderName);

    /**
     * 上传小文件
     *
     * @param files        MultipartFile[] 文件数组
     * @param parentDataId Integer 父文件夹id
     * @return Integer
     * @throws Exception 异常
     */
    Integer uploadMinData(MultipartFile[] files, Integer parentDataId) throws Exception;

    /**
     * 删除文件--逻辑删除
     *
     * @param dataId 文件id
     */
    void delData(Integer dataId);

    /**
     * 批量删除文件
     *
     * @param dataIds List<Integer>文件id集合
     */
    void batchDelData(List<Integer> dataIds);


    /**
     * 修改文件名字
     *
     * @param dataId 文件id
     * @param name   文件名字
     * @return Data
     */
    Data updateDataName(Integer dataId, String name);


    /**
     * 复制操作
     *
     * @param dataIds            要复制的文件id
     * @param targetFolderDataId 复制到的文件夹id
     * @return List<List < Data>> 返回重名的集合和已经存在的集合
     */
    List<List<Data>> copyToNewFolder(List<Integer> dataIds, Integer targetFolderDataId);

    /**
     * 移动文件到另一个文件夹--剪切操作
     *
     * @param dataIds   List<Integer> 要操作的文件id集合
     * @param newDataId Integer 目标文件id
     * @return List<List < Data>>
     * @throws InterruptedException InterruptedException
     */
    List<List<Data>> shearToNewFolder(List<Integer> dataIds, Integer newDataId) throws InterruptedException;

    /**
     * 批量覆盖原有文件,分为好多种批量覆盖,复制同名-->覆盖,剪切同名-->覆盖,还原同名-->覆盖
     *
     * @param ids       将要进行覆盖的文件id
     * @param newDataId 某个文件夹id下进行
     * @param sourceIds 将要被覆盖的id
     * @param status    指标
     */
    void batchOverrideFiles(List<Integer> ids, Integer newDataId, List<Integer> sourceIds, Integer status);

    /**
     * 批量生成副本
     *
     * @param ids       将要进行生成副本的文件id
     * @param newDataId 某个文件夹id下进行
     * @throws InterruptedException InterruptedException
     */
    void batchGenerateDuplicates(List<Integer> ids, Integer newDataId) throws InterruptedException;

    /**
     * 还原回收站的文件
     *
     * @param dataDelIds List<Integer>回收站文件id集合
     */
    void restoreData(List<Integer> dataDelIds);

    /**
     * 获取排序规则
     *
     * @return List<Integer>
     */
    List<Integer> getSortNum();

    /**
     * 判断当前文件id上级是否存在fatherDataId
     *
     * @param nowDataId     当前文件id
     * @param fatherDataIds 父辈文件id集合
     * @return boolean
     */

    boolean judgeDataFather(Integer nowDataId, List<Integer> fatherDataIds);

    /**
     * 无权限获取路径和文件
     *
     * @param dataId   dataId
     * @param shareId  shareId
     * @param passCode passCode
     * @return List<Object>
     */
    List<Object> getDataPathAndData(Integer dataId, Integer shareId, String passCode);
}
