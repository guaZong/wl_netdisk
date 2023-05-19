package com.sk.netdisk.constant;

/**
 * 存放一些rabbitmq的交换机与队列
 * @author lsj
 */
public class RabbitmqConstants {

    /**
     * 手机验证码交换机 rabbitmq exchange
     */
    public static final String CODE_EXCHANGE ="CodeExchange";

    /**
     * 交换机绑定登录 rabbitmq routing
     */
    public static final String BIND_LOGIN_KEY ="phoneCode.login";

    /**
     * 交换机绑定手机找回密码 rabbitmq routing
     */
    public static final String BIND_FIND_PWD_KEY="phoneCode.findPwd";

    /**
     * 交换机绑定邮箱找回密码 rabbitmq routing
     */
    public static final String BIND_BIND_EMAIL_KEY="emailCode.bind";

    /**
     * 交换机绑定手机发送删除回收站验证码  rabbitmq routing
     */
    public static final String BIND_FINAL_DEL_KEY="finalDel.bind";

    /**
     * 文件处理交换机 rabbitmq exchange
     */
    public static final String FILE_EXCHANGE ="FileExchange";

    /**
     * 交换机绑定手机发送删除回收站验证码  rabbitmq routing
     */
    public static final String BIND_ADD_FILE_MD5="addMd5.bind";


    public static final String EXCHANGE_DLX="dlx_exchange";

    public static final String QUEUE_DLX="dlx_queue";

    public static final String KEY_DLX="dlx.#";

    public static final String EXCHANGE_DEL="del_exchange";

    public static final String QUEUE_DEL="del_queue";

    public static final String KEY_DEL="del.#";

}
