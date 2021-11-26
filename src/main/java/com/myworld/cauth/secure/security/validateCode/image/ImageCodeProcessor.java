package com.myworld.cauth.secure.security.validateCode.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myworld.cauth.secure.security.validateCode.common.entity.ImageCode;
import com.myworld.cauth.secure.security.validateCode.common.service.impl.AbstractValidateCodeProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import javax.imageio.ImageIO;


/**
 * Created on 2018/1/10.
 */
@Component("imageValidateCodeProcessor")
public class ImageCodeProcessor extends AbstractValidateCodeProcessor<ImageCode> {
    private static Logger log = LogManager.getRootLogger();
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 发送图形验证码，将其写到相应中
     */

    @Override
    protected void send(ServletWebRequest request, ImageCode imageCode) throws Exception {
        log.info("马上要发送验证码图片了");
        ImageIO.write(imageCode.getImage(), "JPEG", request.getResponse().getOutputStream());
        //HttpServletResponse response = request.getResponse();
        //response.setContentType("application/json;charset=UTF-8");
        //response.getWriter().write(objectMapper.writeValueAsString(ResultUtil.success(imageCode.getImage())));
        //response.getWriter().write(imageCode.getCode());
    }


}
