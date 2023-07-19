package com.sk.netdisk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sk.netdisk.constant.RedisConstants;
import com.sk.netdisk.enums.TransferEnum;
import com.sk.netdisk.mapper.DataMapper;
import com.sk.netdisk.mapper.FileMapper;
import com.sk.netdisk.mapper.TransferMapper;
import com.sk.netdisk.pojo.Data;
import com.sk.netdisk.pojo.Transfer;
import com.sk.netdisk.pojo.dto.FileChunkDTO;
import com.sk.netdisk.pojo.dto.FileChunkResultDTO;
import com.sk.netdisk.service.FileService;
import com.sk.netdisk.service.IUploadService;
import com.sk.netdisk.util.CommonUtils;
import com.sk.netdisk.util.Redis.RedisUtil;
import com.sk.netdisk.util.UserUtil;
import com.sk.netdisk.util.upload.UploadUtil;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;


@Service
@SuppressWarnings("all")
public class UploadServiceImpl implements IUploadService {

    private Logger logger = LoggerFactory.getLogger(UploadServiceImpl.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    FileService fileService;

    @Autowired
    FileMapper fileMapper;

    @Autowired
    DataServiceImpl dataService;

    @Autowired
    TransferMapper transferMapper;

    @Autowired
    DataMapper dataMapper;


    /**
     * 检查文件是否存在，如果存在则跳过该文件的上传，如果不存在，返回需要上传的分片集合
     *
     * @param chunkDTO
     * @return FileChunkResultDTO 检查结果集
     */
    @Override
    public FileChunkResultDTO checkChunkExist(FileChunkDTO chunkDTO) {
        Integer userId = UserUtil.getLoginUserId();
        Integer parentDataId = 0;
        if (chunkDTO.getParentDataId() != null) {
            parentDataId = chunkDTO.getParentDataId();
        }
        String fileName = chunkDTO.getFilename();
        String fileMd5 = chunkDTO.getIdentifier();
        String redisKey = RedisConstants.FILE_KEY + chunkDTO.getIdentifier();

        //检查Redis中是否存在，并且所有分片已经上传完成。,如果redis内存占用过大可以删除redis然后用数据库存储合并成功后的文件
        Set<Integer> uploaded = (Set<Integer>) redisTemplate.opsForHash().get(redisKey, "uploaded");

        Transfer ts=transferMapper.selectOne(new QueryWrapper<Transfer>()
                .eq("md5",fileMd5).eq("create_by",userId)
                .eq("status",TransferEnum.UPLOAD_TRANSFER.getIndex()));
        if(!Objects.isNull(ts)){
            return new FileChunkResultDTO(false, uploaded);
        }
        // 根据文件的MD5值获取文件所在的目录 如: /opt/netdisk/video/zxhjklcjaosijdon5164564/
        String folderPath = getFileFolderPath(chunkDTO.getIdentifier(), chunkDTO.getFilename());
        // 获取文件的绝对路径，如: /opt/netdisk/video/zxhjklcjaosijdon5164564/数学知识.mp4
        String filePath = getFilePath(chunkDTO.getIdentifier(), chunkDTO.getFilename());

        File file = new File(filePath);
        boolean exists = file.exists();

        //如果要上传的文件已经存在于文件库
        if (uploaded != null && uploaded.size() == chunkDTO.getTotalChunks() && exists) {
            //创建transfer状态为上传完成的数据
            Transfer transfer = new Transfer(fileMd5, fileName,
                    TransferEnum.UPLOAD_COMPLETE.getIndex(), new Date(), userId);
            transferMapper.insert(transfer);

            //根据MD5查询file
            com.sk.netdisk.pojo.File sourceFile = fileMapper
                    .selectOne(new QueryWrapper<com.sk.netdisk.pojo.File>().eq("md5", fileMd5));

            //创建data
            Data data = new Data(fileName, UploadUtil.getFileType(fileName)
                    , parentDataId, new Date(), userId, sourceFile.getId());
            dataMapper.insert(data);

            return new FileChunkResultDTO(true);
        }
        //没有上传或者没有上传成功
        File fileFolder = new File(folderPath);
        if (!fileFolder.exists()) {
            // 准备工作，创建文件夹
            fileFolder.mkdirs();
        }
        //假如此时的文件没有被上传过
        if (uploaded.isEmpty()) {
            //创建一个新的transfer数据,状态为上传中
            Transfer newTransfer = new Transfer(fileMd5, fileName
                    , TransferEnum.UPLOAD_TRANSFER.getIndex(), new Date(), userId);
            transferMapper.insert(newTransfer);
        }
        return new FileChunkResultDTO(false, uploaded);
    }


    /**
     * 上传分片,多次调用,有多少分片就调用多少次
     *
     * @param chunkDTO
     */
    @Override
    public void uploadChunk(FileChunkDTO chunkDTO) {
        //获取分片路径
        String chunkFileFolderPath = getChunkFileFolderPath(chunkDTO.getIdentifier(), chunkDTO.getFilename());
        File chunkFileFolder = new File(chunkFileFolderPath);
        //检查分片文件夹存在不
        if (!chunkFileFolder.exists()) {
            // 创建分片文件夹
            chunkFileFolder.mkdirs();
        }
        // 写入分片
        try (
                InputStream inputStream = chunkDTO.getFile().getInputStream();
                FileOutputStream outputStream = new FileOutputStream(
                        new File(chunkFileFolderPath + chunkDTO.getChunkNumber()))
        ) {
            // 快速将字节流从inputStream写入outputStream
            IOUtils.copy(inputStream, outputStream);
            // 将该分片写入redis
            long size = saveToRedis(chunkDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean mergeChunk(String fileMd5, String fileName, Integer totalChunks, Long totalSize) throws IOException {
        Integer userId = UserUtil.getLoginUserId();
        if (mergeChunks(fileMd5, fileName, totalChunks)) {

            com.sk.netdisk.pojo.File sourceFile = new com.sk.netdisk.pojo.File(fileMd5, "url", userId, new Date(),
                    CommonUtils.getFileSize(totalSize), String.valueOf(totalSize));
            fileMapper.insert(sourceFile);
            //将文件添加到数据库,并且将文件id添加到redis
            redisUtil.hset(RedisConstants.FILE_KEY+fileMd5, "fileId", sourceFile.getId());
            //todo parentDataId
            Data data=new Data(fileName, UploadUtil.getFileType(fileName), 1, new Date(), userId, sourceFile.getId());
            dataService.save(data);

            Transfer transfer=transferMapper.selectOne(new QueryWrapper<Transfer>()
                    .eq("md5",fileMd5).eq("create_by",userId)
                    .eq("status",TransferEnum.UPLOAD_TRANSFER.getIndex()));
            //修改状态
            transfer.setStatus(TransferEnum.UPLOAD_COMPLETE.getIndex());
            transferMapper.updateById(transfer);
            //删除分块文件
            String chunkFileFolderPath = getChunkFileFolderPath(fileMd5, fileName);
            File chunFolder = new File(chunkFileFolderPath);
            deleteDirectory(chunFolder);
            //如果redis内存不够的话,可以尝试将redis中的数据删除,然后检查是否存在这个文件,可以用数据查询
            return true;
        } else {
            return false;
        }
    }

    /**
     * 合并分片
     *
     * @param fileMd5     fileMd5
     * @param fileName    fileName
     * @param totalChunks totalChunks
     * @return boolean
     */
    private boolean mergeChunks(String fileMd5, String fileName, Integer totalChunks) {
        //获取分片存储文件夹
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5, fileName);
        //获取文件地址
        String filePath = getFilePath(fileMd5, fileName);
        // 检查分片是否都存在于 分片存储文件夹
        if (!checkChunks(chunkFileFolderPath, totalChunks)) {
            return false;
        }
        //如果分片都存在,列出分片文件数组,根据名字排序,方便合并
        File chunkFileFolder = new File(chunkFileFolderPath);
        File[] chunks = chunkFileFolder.listFiles();
        //排序
        List fileList = Arrays.asList(chunks);
        Collections.sort(fileList, (Comparator<File>) (o1, o2) -> {
            return Integer.parseInt(o1.getName()) - (Integer.parseInt(o2.getName()));
        });
        //创建要合并的文件
        File mergeFile = new File(filePath);
        //合并过程
        try {
            RandomAccessFile randomAccessFileWriter = new RandomAccessFile(mergeFile, "rw");
            byte[] bytes = new byte[1024];
            for (File chunk : chunks) {
                RandomAccessFile randomAccessFileReader = new RandomAccessFile(chunk, "r");
                int len;
                while ((len = randomAccessFileReader.read(bytes)) != -1) {
                    randomAccessFileWriter.write(bytes, 0, len);
                }
                randomAccessFileReader.close();
            }
            randomAccessFileWriter.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 检查分片是否都存在,检查服务器里面的分片是否都存在
     *
     * @param chunkFileFolderPath 分片地址
     * @param totalChunks         总分片数量
     * @return boolean
     */
    private boolean checkChunks(String chunkFileFolderPath, Integer totalChunks) {
        try {
            for (int i = 1; i <= totalChunks + 1; i++) {
                File file = new File(chunkFileFolderPath + File.separator + i);
                if (file.exists()) {
                    continue;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 分片写入Redis
     *
     * @param chunkDTO
     */
    private synchronized long saveToRedis(FileChunkDTO chunkDTO) {
        String redisKey = RedisConstants.FILE_KEY + chunkDTO.getIdentifier();
        Set<Integer> uploaded = (Set<Integer>) redisTemplate.opsForHash().get(redisKey, "uploaded");
        if (uploaded == null) {
            uploaded = new HashSet<>(Arrays.asList(chunkDTO.getChunkNumber()));
            HashMap<String, Object> objectObjectHashMap = new HashMap<>();
            objectObjectHashMap.put("uploaded", uploaded);
            objectObjectHashMap.put("totalChunks", chunkDTO.getTotalChunks());
            objectObjectHashMap.put("totalSize", chunkDTO.getTotalSize());
            objectObjectHashMap.put("path", chunkDTO.getFilename());
            //设置文件的id
            redisTemplate.opsForHash().putAll(redisKey, objectObjectHashMap);
        } else {
            uploaded.add(chunkDTO.getChunkNumber());
            redisTemplate.opsForHash().put(redisKey, "uploaded", uploaded);
        }
        return uploaded.size();
    }

    /**
     * 递归删除文件夹下所有文件 包括文件夹
     *
     * @param directory
     */
    public static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file); // 递归删除子目录
                } else {
                    file.delete(); // 删除文件
                }
            }
        }
        directory.delete(); // 删除目录本身
    }

    /**
     * 得到文件的绝对路径
     *
     * @param filename 文件名称
     * @return String
     */
    private String getFilePath(String fileMd5, String filename) {
        //比如: /opt/netdisk/static/video/34420f6880b6e4467f1b0d36da3c5b44/aaa.mp4
        return getFileFolderPath(fileMd5, filename) + filename;
    }


    /**
     * 得到分块文件所属的目录
     *
     * @param fileMd5  fileMd5
     * @param fileName fileName
     * @return String
     */
    private String getChunkFileFolderPath(String fileMd5, String fileName) {
        //比如: /opt/netdisk/static/video/34420f6880b6e4467f1b0d36da3c5b44/chunks/
        return getFileFolderPath(fileMd5, fileName) + "chunks" + File.separator;
    }

    /**
     * 得到文件所属的目录
     *
     * @param fileMd5  fileMd5
     * @param fileName fileName
     * @return String
     */
    private String getFileFolderPath(String fileMd5, String fileName) {
        //比如: /opt/netdisk/static/video/34420f6880b6e4467f1b0d36da3c5b44/
        return UploadUtil.FILE_PATH + UploadUtil.getStringFileType(fileName) + File.separator
                + fileMd5 + File.separator;
    }
}
