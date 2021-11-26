package com.myworld.cauth.sitecom.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@DynamicInsert
@DynamicUpdate
@Table
public class Visitor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private Timestamp createTime;
    // 由ipAddress-yyyyMMdd组成，即统计单个ip地址的单日访问量，为唯一值
    private String idDetail;
    private String ipAddress;

    // 格式例如：201902, yearMonth是mysql禁用名称，所以使用yearMonthInt代替
    private Integer yearMonthInt;
    // 分为 MOBILE 和 BROWSE
    private String deviceType;
    // 通过 HttpServletRequest获取
    private String userAgent;
    private String referer;

    // 同一ip地址，用户登录后，跟新username记录信息
    private String username;



    // 通过手机定位获取经纬度
    private Double longitude;
    private Double latitude;

    // 通过高德地图转换为地址信息，省和市可通过ip地址转换查询
    private String province;
    private String city;
    private String district;
    private String township;
    private String street;
    private String streetNumber;
    private String formattedAddress;


    // 统计一天当中同一个ip地址的登录次数
    private Long visitCount;
    // 以long格式的时间连续连接组成，统计一天当中登录的次数，格式为： 时间+时间+时间
    @Column(name = "update_time_sum",columnDefinition="LONGTEXT")
    private String updateTimeSum;


    public Visitor() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getIdDetail() {
        return idDetail;
    }

    public void setIdDetail(String idDetail) {
        this.idDetail = idDetail;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getYearMonthInt() {
        return yearMonthInt;
    }

    public void setYearMonthInt(Integer yearMonthInt) {
        this.yearMonthInt = yearMonthInt;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTownship() {
        return township;
    }

    public void setTownship(String township) {
        this.township = township;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public Long getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(Long visitCount) {
        this.visitCount = visitCount;
    }

    public String getUpdateTimeSum() {
        return updateTimeSum;
    }

    public void setUpdateTimeSum(String updateTimeSum) {
        this.updateTimeSum = updateTimeSum;
    }
}

