package com.sk.netdisk.util.Regex;

/**
 * 正则表达式存储类
 * @author lsj
 */
public class RegexPatterns {
    /**
     * 手机号正则表达式
     */
    public static final String PHONE_REGEX="0?(13|14|15|17|18|19)[0-9]{9}";

    /**
     * 31天月份的正则表达式
     */
    public static final String MONTH_REGEX="(1|3|5|7|8|10|12)";

    /**
     * 邮箱正则表达式
     */
    public static final String EMAIL_REGEX="\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";

    /**
     * 密码正则表达式 6-22位字母,数字,下划线
     * ^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$ 这个是只能数字和字母组合并且6-16位
     */
    public static final String PASSWORD_REGEX="^\\w{6,22}$";
    //

    /**
     * 用户名正则表达式
     */
    public static final String USERNAME_REGEX="[A-Za-z0-9_\\-\\u4e00-\\u9fa5]+";

    /**
     * 验证码正则表达式 0-6位数字
     */
    public static final String CODE_REGEX="(?<![0-9])([0-9]{6})(?![0-9])";

    /**
     * 中文字符正则表达式
     */
    public static final String CHINA_REGEX="[\\u4e00-\\u9fa5]";
    /**
     * 域名正则表达式
     */
    public static final String YUMING_REGEX="/^(?=^.{3,255}$)(http(s)?:\\/\\/)?(www\\.)?[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+(:\\d+)*(\\/\\w+\\.\\w+)*([\\?&]\\w+=\\w*)*$/";
}
