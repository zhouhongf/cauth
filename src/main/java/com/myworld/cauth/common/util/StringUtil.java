package com.myworld.cauth.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *字符串处理类
 * submail中可能需要使用第二个方法
 */
@Slf4j
public class StringUtil {

    private static String[] placeSuffix = {"自治州", "自治县", "自治旗", "联合旗", "市辖区", "地区", "辖区", "左旗", "右旗", "前旗", "后旗", "中旗", "街道", "新区", "高新区", "开发区", "省", "市", "区", "县"};

    public static boolean isNotNull(String string){
        return StringUtils.isNotEmpty(string)&&!"null".equals(string);
    }

    public static boolean isNullOrEmpty(String text){
        return text == null || text.trim().isEmpty();
    }

    /**
     *  两种过滤匹配方式
     *  1、去掉特定后缀名称
     *  2、正则表达式匹配
     */
    public static String filterPlaceName(String name) {
        String originName = name;
        // 去掉特定后缀，如果剩余name字数大于等于2的，则返回
        for (String suffix: placeSuffix) {
            if (name.contains(suffix)) {
                name = name.replace(suffix, "");
            }
        }
        if (name.length() > 1) {
            log.info("去掉后缀后的城市名字是：" + name);
            return name;
        }

        // 如果去掉后缀后，name只剩下1个字了，则使用正则表达式重新匹配
        Pattern pattern = Pattern.compile("([\\u4e00-\\u9fa5]{2,})[市|区|县|盟|旗]");
        Matcher matcher = pattern.matcher(originName);
        return matcher.find() ? matcher.group(1) : originName;
    }

    public static boolean isChineseLetters(String word) {
        Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]+");
        Matcher matcher = pattern.matcher(word);
        return matcher.find();
    }

    public static boolean hasWhiteSpace(String word) {
        Pattern pattern = Pattern.compile("\\s+");
        Matcher matcher = pattern.matcher(word.trim());
        return matcher.find();
    }
}
