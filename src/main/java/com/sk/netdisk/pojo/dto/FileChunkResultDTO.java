package com.sk.netdisk.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


/**
 * @author lsj
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileChunkResultDTO {
    /**
     * 是否跳过上传
     */
    private Boolean skipUpload;

    /**
     * 已上传分片的集合
     */
    private Set<Integer> uploaded;

    public FileChunkResultDTO(Boolean skipUpload){
        this.skipUpload=skipUpload;
    }


}
