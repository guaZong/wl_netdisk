package com.netdisk.system.service.impl;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.netdisk.common.constant.RabbitmqConstants;
import com.netdisk.common.constant.RedisConstants;
import com.netdisk.common.enums.AppExceptionCodeMsg;
import com.netdisk.common.enums.DataEnum;
import com.netdisk.common.enums.FilePathEnum;
import com.netdisk.common.exception.AppException;
import com.netdisk.common.utils.CommonUtils;
import com.netdisk.common.utils.Redis.RedisUtil;
import com.netdisk.common.utils.Regex.RegexUtils;
import com.netdisk.common.core.ResponseResult;
import com.netdisk.common.utils.UserUtil;
import com.netdisk.common.utils.upload.OSSUtil;
import com.netdisk.system.dto.UserInfoDto;
import com.netdisk.system.mapper.DataMapper;
import com.netdisk.system.mapper.UserMapper;
import com.netdisk.system.pojo.Data;
import com.netdisk.system.pojo.User;
import com.netdisk.system.pojo.vo.RabbitCodeVO;
import com.netdisk.system.pojo.vo.RecurCountSizeInfo;
import com.netdisk.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;


/**
 * @author lsj
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2022-11-29 10:50:12
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    private final PasswordEncoder passwordEncoder;

    private final RedisUtil redisUtil;

    private final UserMapper userMapper;

    private final RabbitTemplate rabbitTemplate;

    private final OSSUtil ossUtil;

    private final DataServiceImpl dataService;

    private final DataMapper dataMapper;

    private final ExecutorService nowServiceThreadPool;

    @Autowired
    public UserServiceImpl(PasswordEncoder passwordEncoder, RedisUtil redisUtil,
                           UserMapper userMapper, RabbitTemplate rabbitTemplate,
                           OSSUtil ossUtil, DataServiceImpl dataService, DataMapper dataMapper) {
        this.dataService = dataService;
        this.dataMapper = dataMapper;
        ThreadFactory namedThreadFactory = new NamedThreadFactory("UserfServiceImpl", false);
        nowServiceThreadPool = new ThreadPoolExecutor(24, 24, 0,
                TimeUnit.SECONDS, new LinkedBlockingDeque<>(), namedThreadFactory);
        this.passwordEncoder = passwordEncoder;
        this.redisUtil = redisUtil;
        this.userMapper = userMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.ossUtil = ossUtil;
    }


    @Override
    public String sendPhoneCode(String username) {
        //判断手机号格式是否正确
        if (!RegexUtils.isPhoneInvalid(username)) {
            throw new AppException(AppExceptionCodeMsg.PHONE_FORMAT_INVALID);
        }
        return checkAndSendCode(username, RabbitmqConstants.CODE_EXCHANGE,
                RabbitmqConstants.BIND_LOGIN_KEY, RedisConstants.PHONE_LOGIN_KEY);
    }

    @Override
    public void makePwd(String newPassword, String repeatPassword) {
        Integer userId = UserUtil.getLoginUserId();
        userMapper.update(new User()
                , new UpdateWrapper<User>().set("password"
                        , passwordEncoder.encode(newPassword)).eq("user_id", userId));
    }

    @Override
    public String sendFindPwdPhoneCode(String phoneNumber) {
        if (!RegexUtils.isPhoneInvalid(phoneNumber)) {
            throw new AppException(AppExceptionCodeMsg.PHONE_FORMAT_INVALID);
        }
        return checkAndSendCode(phoneNumber, RabbitmqConstants.CODE_EXCHANGE,
                RabbitmqConstants.BIND_FIND_PWD_KEY, RedisConstants.FIND_PWD_P_CODE);
    }


    @Override
    public ResponseResult pSetNewPwd(String phoneNumber, String code, String newPassword, String repeatPassword) {
        User existUser = this.getOne(new QueryWrapper<User>().eq("username", phoneNumber));
        if (Objects.isNull(existUser)) {
            throw new AppException(AppExceptionCodeMsg.PHONE_FORMAT_INVALID);
        }
        Integer userId = existUser.getUserId();
        User user = this.getById(userId);
        if (!code.equals(redisUtil.get(RedisConstants.FIND_PWD_P_CODE + user.getUsername()))) {
            throw new AppException(AppExceptionCodeMsg.INVALID_CODE);
        }
        userMapper.update(new User(), new UpdateWrapper<User>().set("password"
                , passwordEncoder.encode(newPassword)).eq("user_id", userId));
        return ResponseResult.success("修改成功");
    }

    @Override
    public UserInfoDto bindEmail(String code, String email) {
        Integer userId = UserUtil.getLoginUserId();
        User user = this.getById(userId);
        if (email.equals(user.getEmail())) {
            throw new AppException(AppExceptionCodeMsg.EMAIL_REBIND_ERR);
        }
        if (!code.equals(redisUtil.get(RedisConstants.BIND_EMAIL_E_CODE + email))) {
            throw new AppException(AppExceptionCodeMsg.INVALID_CODE);
        }
        userMapper.update(new User(), new UpdateWrapper<User>().set("email"
                , email).eq("user_id", userId));
        return userMapper.findUserById(userId);
    }

    @Override
    public String sendBingEmailCode(String email) {
        if (!RegexUtils.isEmailInvalid(email)) {
            throw new AppException(AppExceptionCodeMsg.EMAIL_FORMAT_INVALID);
        }
        return checkAndSendCode(email, RabbitmqConstants.CODE_EXCHANGE,
                RabbitmqConstants.BIND_BIND_EMAIL_KEY, RedisConstants.BIND_EMAIL_E_CODE);
    }

    @Override
    public UserInfoDto updateAvatar(MultipartFile avatar) {
        Integer userId = UserUtil.getLoginUserId();
        String avatarPath = ossUtil.upload(avatar, FilePathEnum.AVATAR.getPath());
        userMapper.update(new User(), new UpdateWrapper<User>().set("avatar"
                , avatarPath).eq("user_id", userId));
        return userMapper.findUserById(userId);
    }

    @Override
    public UserInfoDto updateNickname(String nickname) {
        Integer userId = UserUtil.getLoginUserId();
        userMapper.update(new User(), new UpdateWrapper<User>().set("nickname"
                , nickname).eq("user_id", userId));
        return userMapper.findUserById(userId);
    }

    @Override
    public String senFinalDelCode(String phoneNumber) {
        return checkAndSendCode(phoneNumber, RabbitmqConstants.CODE_EXCHANGE,
                RabbitmqConstants.BIND_FINAL_DEL_KEY, RedisConstants.PHONE_FINAL_DEL_KEY);
    }

    @Override
    public String getStorage() {
        Integer userId = UserUtil.getLoginUserId();
        Object storage = redisUtil.get(RedisConstants.USER_STORAGE + userId);
        RecurCountSizeInfo recurCountSizeInfo = new RecurCountSizeInfo(0, 0, 0);
        if (Objects.isNull(storage)) {
            getUseSize(userId,recurCountSizeInfo);
            String fileSize = CommonUtils.getFileSize(recurCountSizeInfo.getDataSize());
            redisUtil.set(RedisConstants.USER_STORAGE + userId, fileSize,60);
            return fileSize;
        }else{
            return (String)storage;
        }
    }

    @Override
    public UserInfoDto infoUser() {
        Integer userId=UserUtil.getLoginUserId();
        return userMapper.findUserById(userId);
    }

    private void getUseSize(Integer userId, RecurCountSizeInfo recurCountSizeInfo) {
        List<Data> dataList = dataMapper.selectList(new QueryWrapper<Data>()
                .eq("parent_data_id", DataEnum.ZERO_FOLDER).eq("create_by", userId));
        CountDownLatch countDownLatch = new CountDownLatch(dataList.size());
        for (Data data : dataList) {
            nowServiceThreadPool.submit(() -> {
                try {
                    dataService.recurCountSize(data.getId(), recurCountSizeInfo);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        try {
            countDownLatch.await(1,TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新用户的存储空间大小
     * @param userId
     */
    public void reFreshSize(Integer userId){
        RecurCountSizeInfo recurCountSizeInfo = new RecurCountSizeInfo(0, 0, 0);
            getUseSize(userId,recurCountSizeInfo);
            String fileSize = CommonUtils.getFileSize(recurCountSizeInfo.getDataSize());
            redisUtil.set(RedisConstants.USER_STORAGE + userId, fileSize,60);
    }


    /**
     * 封装redis检查与rabbitmq异步发送信息的方法
     *
     * @param account    账户->邮箱或手机号
     * @param exchange   rabbitmq交换机
     * @param routingKey rabbitmq交换机绑定的routingKey
     * @param redisKey   redisKey
     * @return ResponseResult
     */
    public String checkAndSendCode(String account, String exchange, String routingKey, String redisKey) {
        //生成6位数字验证码-用到 cn.hutool工具类
        String code = RandomUtil.randomNumbers(6);
        //定义mq短信类,用于封装账号与验证码
        RabbitCodeVO rabbitCodeVO = new RabbitCodeVO();
        //先查询redis里面是否存在这个验证码,如果存在就返回
        Object oldCode = redisUtil.get(redisKey + account);
        if (oldCode != null) {
            //将旧的验证码添加进redis
            redisUtil.set(redisKey + account, oldCode, 300);
            //填写账号与验证码
            rabbitCodeVO.setAccount(account);
            rabbitCodeVO.setCode((String) oldCode);
            //rabbitmq异步发送验证码
            rabbitTemplate.convertAndSend(exchange, routingKey, rabbitCodeVO);
            return (String) oldCode;
        }
        //  保存验证码到redis-五分钟过期
        redisUtil.set(redisKey + account, code, 300);
        rabbitCodeVO.setAccount(account);
        rabbitCodeVO.setCode(code);
        rabbitTemplate.convertAndSend(exchange, routingKey, rabbitCodeVO);
        return code;
    }
}





