package com.sk.netdisk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sk.netdisk.pojo.Data;
import com.sk.netdisk.pojo.dto.DataDetInfoDto;
import com.sk.netdisk.pojo.dto.DataPathDto;
import com.sk.netdisk.pojo.vo.DataDelInfoVo;
import com.sk.netdisk.pojo.vo.DataInfoVo;
import com.sun.istack.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;


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
    List<DataDetInfoDto> infoData(Integer parentDataId);

    /**
     * 获取文件详细信息
     *
     * @param dataId 文件id
     * @return DataInfoVo
     */
    DataInfoVo getDataInfo(Integer dataId);

    /**
     * 遍历回收站
     *
     * @return List<DataDelInfoVo>
     */
    List<DataDelInfoVo> infoDataDel();

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
     * 遍历快捷访问
     *
     * @return List<Data>
     */
    List<Data> traverseQuickAccess();

    /**
     * 获取文件路径
     * @param dataId 当前文件id
     * @return List<DataPathDto>
     */
    List<DataPathDto> getDataPath(Integer dataId);

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
     * 删除回收站文件-彻底删除
     *
     * @param dataDelId 回收站文件id
     * @param code      验证码
     * @param result    判断结果->是否需要验证码
     */
    void finalDelData(Integer dataDelId, @Nullable String code, Integer result);

    /**
     * 批量删除回收站文件
     *
     * @param dataDelIds List<Integer> 回收站文件id集合
     * @param code       验证码
     * @param result     判断结果->是否需要验证码
     */
    void batchFinalDelData(List<Integer> dataDelIds, @Nullable String code, Integer result);


    /**
     * 判断是否发送验证码
     *
     * @return Integer
     */
    Integer judgeSendDelCode();

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
     * @throws InterruptedException InterruptedException
     */
    List<List<Data>> copyToNewFolder(List<Integer> dataIds, Integer targetFolderDataId) throws InterruptedException;

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
     * 批量覆盖原有文件
     *
     * @param ids       将要进行覆盖的文件id
     * @param newDataId 某个文件夹id下进行
     * @throws InterruptedException InterruptedException
     */
    void batchOverrideFiles(List<Integer> ids, Integer newDataId) throws InterruptedException;

    /**
     * 批量生成副本
     *
     * @param ids       将要进行生成副本的文件id
     * @param newDataId 某个文件夹id下进行
     * @throws  InterruptedException InterruptedException
     */
    void batchGenerateDuplicates(List<Integer> ids, Integer newDataId) throws InterruptedException;

    /**
     * 添加文件到快捷访问
     *
     * @param dataIds 文件id集合
     */
    void addToQuickAccess(Set<Integer> dataIds);

    /**
     * 还原回收站的文件
     *
     * @param dataDelIds List<Integer>回收站文件id集合
     */
    void restoreData(List<Integer> dataDelIds);


}
