package com.myworld.cauth.secure.security.validateCode.image;

import com.myworld.cauth.config.SecurityProperties;
import com.myworld.cauth.secure.security.validateCode.common.entity.ImageCode;
import com.myworld.cauth.secure.security.validateCode.common.service.ValidateCodeGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * 图片验证码生成器
 */
public class ImageCodeGenerator implements ValidateCodeGenerator {
    private static Logger log = LogManager.getRootLogger();
    @Autowired
    private SecurityProperties securityProperties;

    private static char[] codeSequence = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

    /**
     * 浏览器覆盖配置文件，覆盖默认
     */
    @Override
    public ImageCode generate(ServletWebRequest request){
        int width = ServletRequestUtils.getIntParameter(request.getRequest(), "width", securityProperties.getCode().getImage().getWidth());
        int height = ServletRequestUtils.getIntParameter(request.getRequest(), "height", securityProperties.getCode().getImage().getHeight());

        // 定义图像buffer
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        //Graphics g = image.getGraphics();
        // 创建一个随机数生成器类
        Random random = new Random();
        //填充图像颜色
        g.setColor(getRandColor(200, 250));
        g.fillRect(0, 0, width, height);

        // 创建字体，字体的大小应该根据图片的高度来定。
        g.setFont(new Font("Times New Roman", Font.ITALIC, 20));
        // 画边框。
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, width-1, height-1);

        // 随机产生155条干扰线，使图象中的认证码不易被其它程序探测到。
        g.setColor(getRandColor(160, 200));
        for (int i = 0; i < 155; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }

        //随机产生数字验证码。
        String sRand = "";
        for (int i = 0; i < securityProperties.getCode().getImage().getLength(); i++) {
            String rand = String.valueOf(codeSequence[random.nextInt(35)]);
            sRand += rand;

            // 用随机产生的颜色将验证码绘制到图像中。
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            g.drawString(rand, 13 * i + 6, 16);
        }
        g.dispose();

        log.info("制作的图形验证码是：{}", sRand);
        String theSessionId = request.getSessionId();
        int expireIn = securityProperties.getCode().getImage().getExpireIn();
        LocalDateTime expireTime = LocalDateTime.now().plusSeconds(expireIn);
        return new ImageCode(image, sRand, expireTime, theSessionId);
    }

    /**
     * 生成随机背景条纹
     */
    private Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }


    public SecurityProperties getSecurityProperties() {
        return securityProperties;
    }

    public void setSecurityProperties(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

}
