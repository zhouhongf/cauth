package com.myworld.cauth.secure.data.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;

import java.sql.Timestamp;


@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "userinfo")
public class UserInfo implements Serializable {

    /**
     * 系统主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 平台主键
     */
    private String idDetail;
    /**
     * 用户自己取的独一无二的id
     */
    private String wid;

    /**
     * 创建人
     */
    private String creator;
    /**
     * 更新人
     */
    private String updater;
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "create_time", columnDefinition = "DATETIME COMMENT '创建时间'")
    private Timestamp createTime;
    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "update_time", columnDefinition = "DATETIME COMMENT '更新时间'")
    private Timestamp updateTime;


    /**
     * 用户角色简单分类BORROWER, LENDER
     */
    private String playerType;
    /**
     * 账户状态：待完善，待认证，认证中，已认证，已冻结，已关闭
     */
    private String accountStatus;
    /**
     * 用户账号，即手机号
     */
    private String username;
    /**
     * 用户名nickname
     */
    private String nickname;
    /**
     * 用户头像
     */
    private String userAvatar = "/avatar/" + idDetail;
    /**
     * 信用等级，仅适用于信贷经理，借款人数值为普通
     * 金牌、银牌、铜牌、普通、次级
     */
    private String creditLevel;
    /**
     * 信贷经理，信用等级 详情，譬如 金牌AABB
     * 定时任务运行后，creditLevelConfirm = creditLevel + creditRank
     */
    private String creditLevelConfirm;

    /**
     * 如果是银行人员，则其提供的服务
     * WEALTH, LOANPERSON, LOANCOMPANY
     */
    private String offer;


    /**
     * 真实姓名
     */
    private String realname;
    /**
     * 身份证号
     */
    private String idnumber;
    /**
     * 邮箱
     */
    private String email;


    /**
     * 职业身份
     */
    private String idtype;
    /**
     * 职能类别
     */
    private String theposition;
    /**
     * 行业分类
     */
    private String theindustry;
    /**
     * 单位名称
     */
    private String companyname;
    /**
     * 所属区域
     */
    private String placebelong;
    /**
     * 详细地址
     */
    private String address;


    /**
     * 照片链接
     */
    private String idPhotoFront;
    private String idPhotoBack;
    private String businesscard;
    private String workerscard;
    private String creditpaper;

    /**
     * 实名认证审核操作记录
     */
    private Timestamp idCheckApplyTime;
    private String idCheckResult;
    private String idCheckReasons;
    private String idCheckMemo;
    private Timestamp idCheckCheckTime;
    private Timestamp idCheckReleaseTime;


    public UserInfo() {
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdDetail() {
        return idDetail;
    }

    public String getWid() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }

    public void setIdDetail(String idDetail) {
        this.idDetail = idDetail;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public String getPlayerType() {
        return playerType;
    }

    public void setPlayerType(String playerType) {
        this.playerType = playerType;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getCreditLevel() {
        return creditLevel;
    }

    public void setCreditLevel(String creditLevel) {
        this.creditLevel = creditLevel;
    }

    public String getCreditLevelConfirm() {
        return creditLevelConfirm;
    }

    public void setCreditLevelConfirm(String creditLevelConfirm) {
        this.creditLevelConfirm = creditLevelConfirm;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getIdnumber() {
        return idnumber;
    }

    public void setIdnumber(String idnumber) {
        this.idnumber = idnumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdtype() {
        return idtype;
    }

    public void setIdtype(String idtype) {
        this.idtype = idtype;
    }

    public String getTheposition() {
        return theposition;
    }

    public void setTheposition(String theposition) {
        this.theposition = theposition;
    }

    public String getTheindustry() {
        return theindustry;
    }

    public void setTheindustry(String theindustry) {
        this.theindustry = theindustry;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getPlacebelong() {
        return placebelong;
    }

    public void setPlacebelong(String placebelong) {
        this.placebelong = placebelong;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdPhotoFront() {
        return idPhotoFront;
    }

    public void setIdPhotoFront(String idPhotoFront) {
        this.idPhotoFront = idPhotoFront;
    }

    public String getIdPhotoBack() {
        return idPhotoBack;
    }

    public void setIdPhotoBack(String idPhotoBack) {
        this.idPhotoBack = idPhotoBack;
    }

    public String getBusinesscard() {
        return businesscard;
    }

    public void setBusinesscard(String businesscard) {
        this.businesscard = businesscard;
    }

    public String getWorkerscard() {
        return workerscard;
    }

    public void setWorkerscard(String workerscard) {
        this.workerscard = workerscard;
    }

    public String getCreditpaper() {
        return creditpaper;
    }

    public void setCreditpaper(String creditpaper) {
        this.creditpaper = creditpaper;
    }

    public Timestamp getIdCheckApplyTime() {
        return idCheckApplyTime;
    }

    public void setIdCheckApplyTime(Timestamp idCheckApplyTime) {
        this.idCheckApplyTime = idCheckApplyTime;
    }

    public String getIdCheckResult() {
        return idCheckResult;
    }

    public void setIdCheckResult(String idCheckResult) {
        this.idCheckResult = idCheckResult;
    }

    public String getIdCheckReasons() {
        return idCheckReasons;
    }

    public void setIdCheckReasons(String idCheckReasons) {
        this.idCheckReasons = idCheckReasons;
    }

    public String getIdCheckMemo() {
        return idCheckMemo;
    }

    public void setIdCheckMemo(String idCheckMemo) {
        this.idCheckMemo = idCheckMemo;
    }

    public Timestamp getIdCheckCheckTime() {
        return idCheckCheckTime;
    }

    public void setIdCheckCheckTime(Timestamp idCheckCheckTime) {
        this.idCheckCheckTime = idCheckCheckTime;
    }

    public Timestamp getIdCheckReleaseTime() {
        return idCheckReleaseTime;
    }

    public void setIdCheckReleaseTime(Timestamp idCheckReleaseTime) {
        this.idCheckReleaseTime = idCheckReleaseTime;
    }

}
