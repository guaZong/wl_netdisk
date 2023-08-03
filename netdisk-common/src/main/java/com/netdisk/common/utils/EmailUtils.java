package com.netdisk.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
/**
 * @author lsj
 * @description  邮箱工具类
 *
 */
@Slf4j
@Component
public class EmailUtils {

    @Autowired
    JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sendUser;


    public  String sendEmail(String toUser,String temp,String subject){
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, false);
            mimeMessageHelper.setFrom(sendUser);
            mimeMessageHelper.setTo(toUser);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(temp,true);
            javaMailSender.send(message);
            return temp;
        }catch (Exception e){
            log.info("{}发送失败",toUser);
            e.printStackTrace();
            return null;
        }
    }


}
