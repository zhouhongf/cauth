package com.myworld.cauth.secure.security.validateCode.common.entity;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

/**
 * 因父类ValidateCode已添加了serializable，所以子类就不用再添加了
 * 但是序列化时，子类必须要有一个自己的空的构造函数
 */
public class ImageCode extends ValidateCode {

    private BufferedImage image;

    public ImageCode() {
    }

    public ImageCode(BufferedImage image, String code, LocalDateTime expireTime, String sessionId){
        super(code, expireTime, sessionId);
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }


}
