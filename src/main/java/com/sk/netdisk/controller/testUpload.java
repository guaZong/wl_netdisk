package com.sk.netdisk.controller;

import com.sk.netdisk.pojo.dto.FileChunkDTO;
import com.sk.netdisk.pojo.dto.FileChunkResultDTO;
import com.sk.netdisk.service.IUploadService;
import com.sk.netdisk.util.RestApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sysVisitor")
public class testUpload {

    @Autowired
    IUploadService uploadService;

    /**
     * 检查分片是否存在--检查分片上传的可能性
     *
     * @return
     */
    @GetMapping("chunk")
    public RestApiResponse<Object> checkChunkExist(FileChunkDTO chunkDTO) {
        FileChunkResultDTO fileChunkCheckDTO;
        try {
            fileChunkCheckDTO = uploadService.checkChunkExist(chunkDTO);
            return RestApiResponse.success(fileChunkCheckDTO);
        } catch (Exception e) {
            return RestApiResponse.error(e.getMessage());
        }
    }


    /**
     * 上传文件分片
     *
     * @param chunkDTO
     * @return
     */
    @PostMapping("chunk")
    public RestApiResponse<Object> uploadChunk(FileChunkDTO chunkDTO) {
        try {
            uploadService.uploadChunk(chunkDTO);
            return RestApiResponse.success(chunkDTO.getIdentifier());
        } catch (Exception e) {
            return RestApiResponse.error(e.getMessage());
        }
    }

    /**
     * 请求合并文件分片
     *
     * @param chunkDTO
     * @return
     */
    @PostMapping("merge")
    public RestApiResponse<Object> mergeChunks(@RequestBody FileChunkDTO chunkDTO) {
        try {
            boolean success = uploadService.mergeChunk(chunkDTO.getIdentifier(), chunkDTO.getFilename(),
                    chunkDTO.getTotalChunks(), chunkDTO.getTotalSize());
            return RestApiResponse.flag(success);
        } catch (Exception e) {
            return RestApiResponse.error(e.getMessage());
        }
    }
}
