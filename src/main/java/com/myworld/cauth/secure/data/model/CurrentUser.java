package com.myworld.cauth.secure.data.model;


public class CurrentUser {

    private String wid;

    /**
     * 用户角色简单分类BORROWER, LENDER, HOUSEMANAGER, HOUSELOOKER
     */
    private String playerType;
    /**
     * jwtToken
     */
    private String token;
    /**
     * 用于设置localStorage中currentUser的有效时间
     */
    private String expireTime;


    public CurrentUser() {
    }

    public String getPlayerType() {
        return playerType;
    }

    public void setPlayerType(String playerType) {
        this.playerType = playerType;
    }

    public String getWid() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }
}
