package com.myworld.cauth.secure.data.model;


public class CurrentAdmin {
    /**
     * 主要用于即时通讯
     */
    private String wid;

    /**
     * 管理员权限，分为：
     * 借款人审核员（AdminOfBorrower），
     * 信贷经理审核员（AdminOfLender），
     * 业务审核员(AdminOfProject)，
     * 积分审核员(AdminOfCredit)，
     * 文章管理员(AdminOfWritings)
     */
    private String adminPower;
    /**
     * jwtToken
     */
    private String token;
    /**
     * 用于设置localStorage中currentUser的有效时间
     */
    private String expireTime;


    public CurrentAdmin() {
    }

    public String getWid() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }

    public String getAdminPower() {
        return adminPower;
    }

    public void setAdminPower(String adminPower) {
        this.adminPower = adminPower;
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
