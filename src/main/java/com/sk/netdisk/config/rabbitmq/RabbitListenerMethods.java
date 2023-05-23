package com.sk.netdisk.config.rabbitmq;


import com.sk.netdisk.constant.RedisConstants;
import com.sk.netdisk.enums.AppExceptionCodeMsg;
import com.sk.netdisk.exception.AppException;
import com.sk.netdisk.mapper.DataDelMapper;
import com.sk.netdisk.mapper.DataMapper;
import com.sk.netdisk.mapper.FileMapper;
import com.sk.netdisk.pojo.Data;
import com.sk.netdisk.pojo.DataDel;
import com.sk.netdisk.pojo.vo.RabbitCodeVO;
import com.sk.netdisk.service.DataDelService;
import com.sk.netdisk.service.DataService;
import com.sk.netdisk.service.impl.DataDelServiceImpl;
import com.sk.netdisk.service.impl.DataServiceImpl;
import com.sk.netdisk.util.EmailUtils;
import com.sk.netdisk.util.Redis.RedisUtil;
import com.sk.netdisk.util.SendSmsUtil;
import com.sk.netdisk.constant.RabbitmqConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.MessageConstraintException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * rabbitmq消费者监听队列
 *
 * @author lsj
 */
@Component
@Slf4j
public class RabbitListenerMethods {

    SendSmsUtil sendSmsUtil;

    EmailUtils emailUtils;

    RedisUtil redisUtil;

    FileMapper fileMapper;

    DataDelServiceImpl dataDelService;

    DataDelMapper dataDelMapper;

    DataServiceImpl dataService;

    DataMapper dataMapper;

    @Autowired
    public RabbitListenerMethods(SendSmsUtil sendSmsUtil, EmailUtils emailUtils, RedisUtil redisUtil,
                                 FileMapper fileMapper, DataDelServiceImpl dataDelService, DataDelMapper dataDelMapper,
                                 DataServiceImpl dataService, DataMapper dataMapper) {
        this.sendSmsUtil = sendSmsUtil;
        this.emailUtils = emailUtils;
        this.redisUtil = redisUtil;
        this.fileMapper = fileMapper;
        this.dataDelService = dataDelService;
        this.dataDelMapper = dataDelMapper;
        this.dataService = dataService;
        this.dataMapper = dataMapper;
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "loginPhoneCodeQueue"),
            exchange = @Exchange(name = RabbitmqConstants.CODE_EXCHANGE, type = "topic"),
            key = {RabbitmqConstants.BIND_LOGIN_KEY})
    )
    public void loginPhoneCodeListener(RabbitCodeVO rabbitCodeVO) {
        String phoneNumber= rabbitCodeVO.getAccount();
        String code= rabbitCodeVO.getCode();
        sendSmsUtil.sendSms(phoneNumber, code);
        log.info("{} 登录验证码: {}",phoneNumber,code);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "findPwdCodeQueue"),
            exchange = @Exchange(name = RabbitmqConstants.CODE_EXCHANGE, type = "topic"),
            key = {RabbitmqConstants.BIND_FIND_PWD_KEY})
    )
    public void findPwdCodeListener(RabbitCodeVO rabbitCodeVO) {
        String phoneNumber= rabbitCodeVO.getAccount();
        String code= rabbitCodeVO.getCode();
        sendSmsUtil.sendSms(phoneNumber, code);
        log.info("{} 找回密码验证码: {}",phoneNumber,code);
    }



    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "bindEmailCodeQueue"),
            exchange = @Exchange(name = RabbitmqConstants.CODE_EXCHANGE, type = "topic"),
            key = {RabbitmqConstants.BIND_BIND_EMAIL_KEY})
    )
    public void bindEmailCodeListener(RabbitCodeVO rabbitCodeVO) {
        String email= rabbitCodeVO.getAccount();
        String code= rabbitCodeVO.getCode();
        String tmp="[未来网盘] 您的绑定邮箱验证码为: "+code+",有效期五分钟";
        String sub="未来网盘 | 绑定邮箱验证码";
        emailUtils.sendEmail(email,tmp,sub);
        log.info("{} 邮箱绑定验证码: {}",email,code);
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "bindPhoneDelCodeQueue"),
            exchange = @Exchange(name = RabbitmqConstants.CODE_EXCHANGE, type = "topic"),
            key = {RabbitmqConstants.BIND_FINAL_DEL_KEY})
    )
    public void bindPhoneDelCodeListener(RabbitCodeVO rabbitCodeVO) {
        String phoneNumber= rabbitCodeVO.getAccount();
        String code= rabbitCodeVO.getCode();
        sendSmsUtil.sendSms(phoneNumber, code);
        log.info("{} 删除回收站验证码: {}",phoneNumber,code);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "bindAddFileMd5Queue"),
            exchange = @Exchange(name = RabbitmqConstants.FILE_EXCHANGE, type = "topic"),
            key = {RabbitmqConstants.BIND_ADD_FILE_MD5})
    )
    public void bindAddFileMd5(Integer fileId) {
        String md5 = fileMapper.selectById(fileId).getMd5();
        redisUtil.hincr(RedisConstants.FILE_KEY + md5, "useNum", 1);
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = RabbitmqConstants.QUEUE_DLX),
            exchange = @Exchange(name = RabbitmqConstants.EXCHANGE_DLX, type = "topic"),
            key = {RabbitmqConstants.KEY_DLX})
    )
    public void bindDlxFinalDelData(Integer dataDelId) {
        try {
            DataDel dataDel = dataDelMapper.selectById(dataDelId);
            if(!Objects.isNull(dataDel)){
                Integer dataId = dataDel.getDataId();
                Data data = dataMapper.findById(dataId);
                if(data==null){
                    return;
                }
                dataMapper.finalDelData(dataId);
                dataDelService.recurCountFinalDelete(data);
                dataDelService.removeById(dataDelId);
            }
        }catch (Exception e){
            throw new MessageConversionException("消息消费失败，移出消息队列，不再试错");
        }

    }

}
