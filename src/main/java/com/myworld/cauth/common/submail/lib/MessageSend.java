package com.myworld.cauth.common.submail.lib;


import com.myworld.cauth.common.submail.config.AppConfig;
import com.myworld.cauth.common.submail.lib.base.ISender;
import com.myworld.cauth.common.submail.lib.base.SenderWapper;

/**
 * message/send API 不仅提供强大的短信发送功能,
 * 并在API中集成了地址薄发送功能。你可以通过设定一些参数来确定 API 以哪种模式发送。
 */
public class MessageSend extends SenderWapper {

    protected AppConfig config = null;
    public static final String TO = "to";
    public static final String CONTENT = "content";

    public MessageSend(AppConfig config) {
        this.config = config;
    }

    public void addTo(String to) {
        requestData.addWithComma(TO, to);;
    }

    public void addContent(String content){
        requestData.addWithComma(CONTENT, content);
    }


    @Override
    public ISender getSender() {
        return new  Message(this.config);
    }

    public String send(){
        return getSender().send(requestData);
    }


}
