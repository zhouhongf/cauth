package com.myworld.cauth.common.properties;


public class ImageCodeProperties extends SmsCodeProperties {

    /**
     * 图片宽
     */
    private int width = 90;
    /**
     * 图片高
     */
    private int height = 25;

    private int length;

    public ImageCodeProperties() {
        setLength(6);
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
