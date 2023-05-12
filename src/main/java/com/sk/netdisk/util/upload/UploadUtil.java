package com.sk.netdisk.util.upload;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 上传工具类
 * @author lsj
 */
public class UploadUtil {
    private static Map<String, Integer> FILE_TYPE_MAP = new HashMap<String, Integer>();

    static {
        // 图片
        FILE_TYPE_MAP.put("jpg", 1);
        FILE_TYPE_MAP.put("jpeg", 1);
        FILE_TYPE_MAP.put("png", 1);
        FILE_TYPE_MAP.put("bmp", 1);
        FILE_TYPE_MAP.put("gif", 1);
        FILE_TYPE_MAP.put("ico", 1);
        FILE_TYPE_MAP.put("webp", 1);
        FILE_TYPE_MAP.put("svg", 1);
        // 视频
        FILE_TYPE_MAP.put("mp4", 2);
        FILE_TYPE_MAP.put("avi", 2);
        FILE_TYPE_MAP.put("mkv", 2);
        FILE_TYPE_MAP.put("mov", 2);
        FILE_TYPE_MAP.put("wmv", 2);
        FILE_TYPE_MAP.put("flv", 2);
        FILE_TYPE_MAP.put("rmvb", 2);
        FILE_TYPE_MAP.put("3gp", 2);
        FILE_TYPE_MAP.put("mpg", 2);
        FILE_TYPE_MAP.put("mpeg", 2);
        FILE_TYPE_MAP.put("m4v", 2);
        FILE_TYPE_MAP.put("vob", 2);
        // 文档
        FILE_TYPE_MAP.put("doc", 3);
        FILE_TYPE_MAP.put("docx", 3);
        FILE_TYPE_MAP.put("pdf", 3);
        FILE_TYPE_MAP.put("txt", 3);
        FILE_TYPE_MAP.put("ppt", 3);
        FILE_TYPE_MAP.put("pptx", 3);
        FILE_TYPE_MAP.put("xls", 3);
        FILE_TYPE_MAP.put("xlsx", 3);
        FILE_TYPE_MAP.put("rtf", 3);
        FILE_TYPE_MAP.put("odt", 3);
        // 音乐
        FILE_TYPE_MAP.put("mp3", 4);
        FILE_TYPE_MAP.put("wav", 4);
        FILE_TYPE_MAP.put("flac", 4);
        FILE_TYPE_MAP.put("ape", 4);
        FILE_TYPE_MAP.put("aac", 4);
        FILE_TYPE_MAP.put("m4a", 4);
        FILE_TYPE_MAP.put("wma", 4);
        FILE_TYPE_MAP.put("ogg", 4);
        FILE_TYPE_MAP.put("alac", 4);
        // 种子
        FILE_TYPE_MAP.put("torrent", 5);
        // 压缩包
        FILE_TYPE_MAP.put("zip", 6);
        FILE_TYPE_MAP.put("rar", 6);
        FILE_TYPE_MAP.put("7z", 6);
        FILE_TYPE_MAP.put("gz", 6);
        FILE_TYPE_MAP.put("tar", 6);
        FILE_TYPE_MAP.put("iso", 6);
    }

    public static int getFileType(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        int index = fileName.lastIndexOf(".");
        if (index > 0) {
            String suffix = fileName.substring(index + 1).toLowerCase();
            Integer type = FILE_TYPE_MAP.get(suffix);
            if (type != null) {
                return type;
            }
        }
        // 其他
        return 7;
    }
}
