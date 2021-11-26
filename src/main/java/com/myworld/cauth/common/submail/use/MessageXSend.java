package com.myworld.cauth.common.submail.use;


import com.myworld.cauth.common.submail.config.AppConfig;
import com.myworld.cauth.common.submail.lib.MESSAGEXsend;
import com.myworld.cauth.common.submail.utils.ConfigLoader;

public class MessageXSend {

    public static void main(String[] args) {
        AppConfig config = ConfigLoader.load(ConfigLoader.ConfigType.Message);
        MESSAGEXsend submail = new MESSAGEXsend(config);
        submail.addTo("13771880835");
        submail.setProject("RUCsa1");
        submail.addVar("code", "158955");
        String response=submail.xsend();
        System.out.println("接口返回数据：" + response);
    }
}
