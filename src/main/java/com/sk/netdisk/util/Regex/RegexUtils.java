package com.sk.netdisk.util.Regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达工具类
 * @author lsj
 */
public class RegexUtils {
    /**
     * 判断手机号格式是否正确
     * @param phone
     * @return true:符合，false：不符合
     */
    public static boolean isPhoneInvalid(String phone){
        return mismatch(phone,RegexPatterns.PHONE_REGEX);
    }


    /**
     * 判断月份是否正确
     * @param month
     * @return true:符合，false：不符合
     */
    public static boolean isMonthInvalid(Integer month){
        return mismatch(String.valueOf(month),RegexPatterns.MONTH_REGEX);
    }

    /**
     * 判断邮箱格式是否正确
     * @param email
     * @return true:符合，false：不符合
     */
    public static boolean isEmailInvalid(String email){
        return mismatch(email,RegexPatterns.EMAIL_REGEX);
    }


    /**
     * 判断验证码格式是否正确
     * @param code
     * @return true:符合，false：不符合
     */
    public static boolean isCodeInvalid(String code){
        return mismatch(code,RegexPatterns.CODE_REGEX);
    }


    /**
     * 判断密码格式是否正确
     * @param password
     * @return true:符合，false：不符合
     */
    public static boolean isPasswordInvalid(String password){
        return mismatch(password,RegexPatterns.PASSWORD_REGEX);
    }


    /**
     * 判断账号格式是否正确
     * @param username
     * @return true:符合，false：不符合
     */
    public static boolean isUsernameInvalid(String username){
        return mismatch(username,RegexPatterns.USERNAME_REGEX);
    }


    /**
     * 判断一段字符串中是否存在中文
     * @param str
     * @return true:存在，false：不存在
     */
    public static boolean isExistChinese(String str){
        return isExist(str,RegexPatterns.CHINA_REGEX);
    }


    /**
     * 判断域名是否正确
     * @param str
     * @return true:存在，false：不存在
     */
    public static boolean isExistYuMing(String str){
        return mismatch(str,RegexPatterns.YUMING_REGEX);
    }


    /**
     * 校验是否不符合正则格式
     * @param str
     * @param regex
     * @return
     */
    public static boolean mismatch(String str,String regex){
        return str.matches(regex);
    }

    /**
     * 校验是否存在
     * @param str
     * @param regex
     * @return
     */
    public static boolean isExist(String str,String regex){
        Pattern r = Pattern.compile(regex);
        Matcher m = r.matcher(str);
        if(m.find()){
            return true;
        }
        return false;
    }


    public static boolean test(String str,String regex){
        //正则规则
        Pattern pattern=Pattern.compile(regex);
        //被校验的字符串
        Matcher match=pattern.matcher(str);
        if(match.find()){
           return true;
        }else{
            return false;
        }
    }

}
