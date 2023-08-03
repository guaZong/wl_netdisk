package com.netdisk.common.utils.upload;


import com.netdisk.common.enums.AppExceptionCodeMsg;
import com.netdisk.common.exception.AppException;
import com.netdisk.common.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * 上传工具类
 *
 * @author lsj
 */
@Slf4j
@Component
public class UploadUtil {
    public final static Map<String, Integer> FILE_TYPE_MAP = new HashMap<String, Integer>();
    public final static Map<Integer, String> FILE_PATH_TYPE = new HashMap<>();
    public final static String FILE_PATH = System.getProperty("user.dir") + File.separator
            + "static" + File.separator;


    static {
        //定义目录
        String[] subDirectories = {"view", "img", "video", "doc", "music", "torrent", "zip", "other"};
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

        FILE_PATH_TYPE.put(0, subDirectories[0]);
        FILE_PATH_TYPE.put(1, subDirectories[1]);
        FILE_PATH_TYPE.put(2, subDirectories[2]);
        FILE_PATH_TYPE.put(3, subDirectories[3]);
        FILE_PATH_TYPE.put(4, subDirectories[4]);
        FILE_PATH_TYPE.put(5, subDirectories[5]);
        FILE_PATH_TYPE.put(6, subDirectories[6]);
        FILE_PATH_TYPE.put(7, subDirectories[7]);

        File rootDirectory = new File(FILE_PATH);

        // 创建根目录
        if (!rootDirectory.exists()) {
            boolean success = rootDirectory.mkdirs();
            if (success) {
                log.info("根目录创建成功");
            }
        }

        // 创建子目录

        for (String subDirectory : subDirectories) {
            File directory = new File(FILE_PATH + subDirectory);
            if (!directory.exists()) {
                boolean success = directory.mkdir();
                if (success) {
                    log.info("子目录 " + subDirectory + " 创建成功");
                }
            }
        }

    }

    private String domainName;

    /**
     * 获取域名
     *
     * @param domainName 域名
     */
    @Value("${domainName}")
    private void getDomainName(String domainName) {
        this.domainName = domainName;
    }

    public static int getFileType(String fileName) {
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

    public static String getStringFileType(String fileName) {
        return FILE_PATH_TYPE.get(getFileType(fileName));
    }

    public String uploadFile(MultipartFile file) {
        // 返回存储路径
        String url = "";
        String domain = domainName + "netDisk/";
        // 获取文件名加后缀
        String fileName = file.getOriginalFilename();
        try {
            //获取文件md5值
            String fileMd5 = CommonUtils.getFileMd5(file);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        if (fileName != null && !fileName.isEmpty()) {
            String userPath = getStringFileType(file.getOriginalFilename());
            // 获取文件夹路径
            String realPath = FILE_PATH + userPath;
            File file1 = new File(realPath);
            // 将图片存入文件夹
            File targetFile = new File(file1, fileName);
            try {
                InputStream inputStream = file.getInputStream();
                OutputStream outputStream = new FileOutputStream(targetFile);
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();
                // 生成文件地址
                url = domain + "view/" + fileName;
                System.out.println("文件上传成功 url: " + url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if ("".equals(url)) {
            throw new AppException(AppExceptionCodeMsg.BUSY);
        }
        return url;
    }

    // 缓冲区大小
    private static final int BUFFER_SIZE = 8192;
    // 更新间隔，单位：毫秒
    private static final int UPDATE_INTERVAL = 1000;

    public static void main(String[] args) {
        // 替换为要上传的文件路径
        String filePath = System.getProperty("user.dir") + "/static/view/";
        File file = new File(filePath);
        // 文件大小
        long fileSize = file.length();
        // 已上传的字节数
        long uploadedBytes = 0;
        // 开始上传时间
        long startTime = System.currentTimeMillis();

        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                // 这里模拟上传操作，实际应根据你的上传方式进行调整
                // upload(buffer, bytesRead);

                uploadedBytes += bytesRead;

                // 计算上传速度和进度
                long elapsedTime = System.currentTimeMillis() - startTime;
                // 上传速度，单位：字节/秒
                double uploadSpeed = uploadedBytes / (elapsedTime / 1000.0);
                // 上传进度，单位：百分比
                double progress = (double) uploadedBytes / fileSize * 100;

                // 显示上传速度和进度条
                System.out.printf("上传速度: %.2f KB/s, 进度: %.2f%%%n", uploadSpeed / 1024, progress);
                drawProgressBar(progress);

                // 暂停1秒钟，以减缓循环速度，模拟实际上传过程
                Thread.sleep(UPDATE_INTERVAL);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void drawProgressBar(double progress) {
        StringBuilder progressBar = new StringBuilder();
        int filledLength = (int) (progress / 5);
        int emptyLength = 20 - filledLength;

        progressBar.append("[");

        for (int i = 0; i < filledLength; i++) {
            progressBar.append("=");
        }

        for (int i = 0; i < emptyLength; i++) {
            progressBar.append(" ");
        }

        progressBar.append("]");

        System.out.print("\r" + progressBar.toString());
    }
}
