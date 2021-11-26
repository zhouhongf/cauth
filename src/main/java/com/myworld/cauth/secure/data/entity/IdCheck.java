package com.myworld.cauth.secure.data.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "idcheck")
public class IdCheck implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 用户的idDetail
     */
    private String idDetail;

    /**
     * 用户角色简单分类BORROWER, LENDER
     */
    private String playerType;

    private String offer;

    /**
     * 审核提交时间
     */
    @CreationTimestamp
    @Column(name = "id_check_applytime",columnDefinition="DATETIME COMMENT '审核提交时间'")
    private Timestamp idCheckApplyTime;


    private String username;
    private String nickname;
    private String realname;
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
     * 所属区域
     */
    private String placebelong;
    /**
     * 单位名称
     */
    private String companyname;
    /**
     * 详细地址
     */
    private String address;
    /**
     * 身份证号
     */
    private String idnumber;



    /**
     * 身份证正面照链接
     */
    private String idPhotoFront;
    /**
     *身份证反面照链接
     */
    private String idPhotoBack;
    /**
     *名片照片链接
     */
    private String businesscard;
    /**
     *工牌照片链接
     */
    private String workerscard;
    /**
     *信用报告或授权书链接
     */
    private String creditpaper;



    /**
     * 审核结果
     */
    private String idCheckResult;
    /**
     * 审核时间
     */
    private Timestamp idCheckCheckTime;
    /**
     * 审核结果发布时间
     */
    private Timestamp idCheckReleaseTime;
    /**
     * 审核原因反馈
     */
    private String idCheckReasons;
    /**
     * 审核备注
     */
    private String idCheckMemo;
    /**
     * 是否已经审核过了，以是否发布为准，YES, NO
     */
    private String alreadySetup;

    public IdCheck() {
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

    public void setIdDetail(String idDetail) {
        this.idDetail = idDetail;
    }

    public String getPlayerType() {
        return playerType;
    }

    public void setPlayerType(String playerType) {
        this.playerType = playerType;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public Timestamp getIdCheckApplyTime() {
        return idCheckApplyTime;
    }

    public void setIdCheckApplyTime(Timestamp idCheckApplyTime) {
        this.idCheckApplyTime = idCheckApplyTime;
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

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
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

    public String getPlacebelong() {
        return placebelong;
    }

    public void setPlacebelong(String placebelong) {
        this.placebelong = placebelong;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdnumber() {
        return idnumber;
    }

    public void setIdnumber(String idnumber) {
        this.idnumber = idnumber;
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

    public String getIdCheckResult() {
        return idCheckResult;
    }

    public void setIdCheckResult(String idCheckResult) {
        this.idCheckResult = idCheckResult;
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

    public String getAlreadySetup() {
        return alreadySetup;
    }

    public void setAlreadySetup(String alreadySetup) {
        this.alreadySetup = alreadySetup;
    }
}
