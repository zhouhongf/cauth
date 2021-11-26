package com.myworld.cauth.secure.service;

import com.myworld.cauth.common.model.ApiResult;
import com.myworld.cauth.common.model.SimpleUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 配置okhttp后，基于ribbon的服务发现不管用了，需要定义url地址
 * url地址可以在配置文件中进行配置
 * 因此这里的name可以随便取，只要不重复就行
 */
@FeignClient(name = "schat-server", url = "${feign.schat.url}")
public interface ChatFeignService {

    @RequestMapping(value = "/syncPhoneUser", method = RequestMethod.POST)
    ApiResult syncPhoneUser(@RequestBody SimpleUser simpleUser);

    @RequestMapping(value = "/initPhoneUser", method = RequestMethod.POST)
    ApiResult initPhoneUser(@RequestBody SimpleUser simpleUser);
}
