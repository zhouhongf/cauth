package com.myworld.cauth.common.submail.utils;


import com.myworld.cauth.common.submail.config.AppConfig;
import com.myworld.cauth.common.submail.config.MessageConfig;

import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {

    private static Properties pros = null;
    /**
     * 加载文件时，类载入，静态块内部的操作将被运行一次
     * */
    static {
        pros = new Properties();
        try {
            pros.load(ConfigLoader.class.getResourceAsStream("/app_config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * enum define two kinds of configuration.
     * */
    public static enum ConfigType {
        Mail, Message,Voice,Internationalsms,Mobiledata
    };

    /**
     * 外部类的静态方法，可以通过加载文件创建配置。
     * */
    public static AppConfig load(ConfigType type) {
        switch (type) {
            case Message:
                return createMessageConfig();
            default:
                return null;
        }
    }

    private static AppConfig createMessageConfig() {
        AppConfig config = new MessageConfig();
        config.setAppId(pros.getProperty(MessageConfig.APP_ID));
        config.setAppKey(pros.getProperty(MessageConfig.APP_KEY));
        config.setSignType(pros.getProperty(MessageConfig.APP_SIGNTYPE));
        return config;
    }
}
