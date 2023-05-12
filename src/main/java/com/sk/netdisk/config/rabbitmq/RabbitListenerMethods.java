package com.sk.netdisk.config.rabbitmq;


import com.sk.netdisk.constant.RedisConstants;
import com.sk.netdisk.mapper.FileMapper;
import com.sk.netdisk.pojo.vo.RabbitCodeVO;
import com.sk.netdisk.util.EmailUtils;
import com.sk.netdisk.util.Redis.RedisUtil;
import com.sk.netdisk.util.SendSmsUtil;
import com.sk.netdisk.constant.RabbitmqConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * rabbitmq消费者监听队列
 *
 * @author lsj
 */
@Component
@Slf4j
public class RabbitListenerMethods {
    @Autowired
    SendSmsUtil sendSmsUtil;
    @Autowired
    EmailUtils emailUtils;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    FileMapper fileMapper;

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

}
