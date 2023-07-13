package com.sk.netdisk.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
/**
 * @author lsj
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileChunkDTO {
    /**
     * 文件md5 ok
     */
    private String identifier;
    /**
     * 分块文件 ok
     */
    MultipartFile file;
    /**
     * 当前分块序号  ok
     *
     */
    private Integer chunkNumber;
    /**
     * 分块大小 ok
     */
    private Long chunkSize;
    /**
     * 当前分块大小 ok
     */
    private Long currentChunkSize;
    /**
     * 文件总大小 ok
     */
    private Long totalSize;
    /**
     * 分块总数 ok
     */
    private Integer totalChunks;
    /**
     * 文件名 ok
     */
    private String filename;

}
