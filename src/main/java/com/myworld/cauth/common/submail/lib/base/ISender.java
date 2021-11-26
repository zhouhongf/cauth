package com.myworld.cauth.common.submail.lib.base;

import java.util.Map;

public interface ISender {

    /**
     * 发送请求数据
     * @param data{@link HashMap}
     * @return 如果发送成功,返回true，发生错误,返回false。
     */
    public String send(Map<String, Object> data);

    public String xsend(Map<String, Object> data);

    public String subscribe(Map<String, Object> data);

    public String unsubscribe(Map<String, Object> data);

}
