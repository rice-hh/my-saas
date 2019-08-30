package com.my.saas.common.utils;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.math.NumberUtils;

public class StringUtils {

    public static boolean isLongNumber(String str) {
        try {
            Long.parseLong(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static boolean isNumber(String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static String replace(String str, String[] args) {
        String result = str;
        for (int i = 0; i < args.length; i++) {
            result = result.replaceAll("\\{" + i + "\\}", args[i]);
        }

        return result;
    }

    /**
     * 验收数字是否为正数
     *
     * @param str 验证对象
     * @return 正数:true,其他:false
     */
    public static boolean isPositiveNum(String str) {
    	String regex = "^\\\\d+$";
    	boolean b = Pattern.matches(regex, str);
		return b;
    }
    
    /**
     * 验证对象不为空也不为NULL
     *
     * @param str 验证对象
     * @return 处理结果 空/Null:true,否则:false
     */
    public static boolean isNotEmptyOrNull(Object str) {
        if (!"".equals(str) && null != str) {
            return true;
        }
        return false;
    }
    
    /**
     * 判断字符串是否为纯数字组成(也不含小数点和负号)
     *
     * @param str
     * @return
     * @date 2013-11-26 下午4:14:43
     */
    public static boolean isDigits(String str) {
        return NumberUtils.isDigits(str);
    }


    /**
     * 自定义正则表达式进行各类验证
     *
     * @param str   待验证字符串
     * @param regex
     * @return 验证通过：true, 不通过：false
     */
    public static boolean regularValid(String str, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        if (!m.matches()) {
            return false;
        }
        return true;
    }
    
    /**
     * ADD BY WANGTAO_BJ
     */
    private static String replaceCommon(String modelContent, Map<String, Object> map, String before, String after) {
        Set<Entry<String, Object>> entrys = map.entrySet();
        for (Entry<String, Object> entry : entrys) {
            String key = entry.getKey();
            if (modelContent.contains(key)) {
                modelContent = modelContent.replaceAll("\\{" + key + "\\}", (before == null ? "" : before) + entry.getValue().toString() + (after == null ? "" : after));
            }
        }
        return modelContent;
    }

    public static String replace(String modelContent, List<Map<String, Object>> maps, String... params) {
        for (Map<String, Object> map : maps) {
            modelContent = replace(modelContent, map, params);
        }
        return modelContent;
    }

    public static String replace(String modelContent, Map<String, Object> map, String... params) {
        if (params.length == 0) {
            return replaceCommon(modelContent, map, null, null);
        } else if (params.length == 1) {
            return replaceCommon(modelContent, map, params[0], params[0]);
        } else if (params.length == 2) {
            return replaceCommon(modelContent, map, params[0], params[1]);
        } else {
            return null;
        }
    }

    /**
     * 解码
     * @param para
     * @param decode
     * @return para
     * @author
     */
    @SuppressWarnings("deprecation")
    public static String decode(String para, String decode) {
        try {
            if ("".equals(decode)) {
                return URLDecoder.decode(para);
            }
            return URLDecoder.decode(para, decode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return para;
    }

    /**
     * 验证输入的电话是否符合规则
     * @param num
     * @return
     */
    public static boolean checkTelePhoneNum(String num) {
        // 手机号码校验规则
        String regex = "^((0\\d{2,3}-?)?\\d{7,8})(-\\d{1,9})?$";
    	boolean b = Pattern.matches(regex, num);
        return b;
    }
    
    /**
     * 验证输入的手机号是否符合规则
     *
     * @param num
     * @return
     */
    public static boolean checkPhoneNum(String num) {
        // 手机号码校验规则
        String regex = "^1[3|4|5|6|7|8|9][0-9]{9}$";
    	boolean b = Pattern.matches(regex, num);
        return b;
    }

    /**
     * 验证邮箱的规则
     *
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        // 邮箱校验规则
    	String regex = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    	boolean b = Pattern.matches(regex, email);
        return b;
    }
    
	/**
	 * 循环遍历map，将map中的value值拼接成用,分割的字符串
	 * @param map
	 * @return
	 */
	public static String mapValueToString(Map map) {
		String values = "";
		for(Object value : map.values()) {
			values +=value+",";
		}
		if(values.lastIndexOf(",")>0) {
			values = values.substring(0,values.length()-1);
		}
		return values;
	}
	
}
