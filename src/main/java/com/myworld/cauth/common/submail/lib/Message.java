package com.myworld.cauth.common.submail.lib;



import com.myworld.cauth.common.submail.config.AppConfig;
import com.myworld.cauth.common.submail.lib.base.Sender;

import java.util.Map;

public class Message extends Sender {

    private static final String API_SEND = "http://api.submail.cn/message/send.json";
    private static final String API_XSEND = "http://api.submail.cn/message/xsend.json";
    private static final String API_SUBSCRIBE = "http://api.submail.cn/addressbook/message/subscribe.json";
    private static final String API_UNSUBSCRIBE = "http://api.submail.cn/addressbook/message/unsubscribe.json";

    public Message(AppConfig config) {
        this.config = config;
    }

    /**
     * 发送请求数据到服务器,数据由两部分组成,其中一个是原始数据，另一个是签名
     */
    @Override
    public String send(Map<String, Object> data) {
        return request(API_SEND, data);
    }

    @Override
    public String xsend(Map<String, Object> data) {
        return request(API_XSEND, data);
    }


    @Override
    public String subscribe(Map<String, Object> data) {
        // TODO Auto-generated method stub
        return request(API_SUBSCRIBE, data);
    }

    @Override
    public String unsubscribe(Map<String, Object> data) {
        // TODO Auto-generated method stub
        return request(API_UNSUBSCRIBE, data);
    }

}
