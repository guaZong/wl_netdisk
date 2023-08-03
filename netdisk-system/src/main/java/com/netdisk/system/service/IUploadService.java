package com.netdisk.system.service;



import com.netdisk.system.dto.FileChunkDTO;
import com.netdisk.system.dto.FileChunkResultDTO;

import java.io.IOException;

/**
 * @author lsj
 * @description 上传service
 *
 */
public interface IUploadService {

    /**
     * 检查文件是否存在，如果存在则跳过该文件的上传，如果不存在，返回需要上传的分片集合
     * @param chunkDTO
     * @return
     */
    FileChunkResultDTO checkChunkExist(FileChunkDTO chunkDTO) ;


    /**
     * 上传文件分片
     * @param chunkDTO
     * @throws IOException
     */
    void uploadChunk(FileChunkDTO chunkDTO) throws IOException;


    /**
     * 合并文件分片
     * @param identifier
     * @param fileName
     * @param totalChunks
     * @return
     * @throws IOException
     */
    boolean mergeChunk(String identifier,String fileName,Integer totalChunks,Long totalSize)throws  IOException;
}
