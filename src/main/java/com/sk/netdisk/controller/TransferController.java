package com.sk.netdisk.controller;

import cn.hutool.core.util.ObjectUtil;
import com.sk.netdisk.controller.request.GeneralRequest;
import com.sk.netdisk.enums.AppExceptionCodeMsg;
import com.sk.netdisk.enums.DataEnum;
import com.sk.netdisk.exception.AppException;
import com.sk.netdisk.pojo.Data;
import com.sk.netdisk.pojo.dto.DataDetInfoDto;
import com.sk.netdisk.pojo.dto.DataPathDto;
import com.sk.netdisk.pojo.vo.DataDelInfoVo;
import com.sk.netdisk.pojo.vo.DataInfoVo;
import com.sk.netdisk.service.DataService;
import com.sk.netdisk.util.ResponseResult;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 传输接口
 * @author lsj
 */
@RestController
public class TransferController {



}
