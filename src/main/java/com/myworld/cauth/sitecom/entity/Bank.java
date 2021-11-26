package com.myworld.cauth.sitecom.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@DynamicInsert
@DynamicUpdate
public class Bank implements Serializable {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String parentName;
    private String parentAlias;
    private String idDetail;

    private String name;
    private String address;
    private String location;
    private String tel;

    @JsonIgnore
    private String parent;
    @JsonIgnore
    private String tag;
    @JsonIgnore
    private String type;
    @JsonIgnore
    private String typecode;
    @JsonIgnore
    private String bizType;

    @JsonIgnore
    private String postcode;
    @JsonIgnore
    private String website;
    @JsonIgnore
    private String email;

    @JsonIgnore
    private String pcode;
    @JsonIgnore
    private String pname;
    @JsonIgnore
    private String citycode;
    @JsonIgnore
    private String cityname;
    @JsonIgnore
    private String adcode;
    @JsonIgnore
    private String adname;

    @JsonIgnore
    private String gridcode;
    @JsonIgnore
    private String naviPoiid;
    @JsonIgnore
    private String entrLocation;
    @JsonIgnore
    private String exitLocation;
    @JsonIgnore
    private String businessArea;

    @JsonIgnore
    private String alias;

    private String photoOne;
    private String photoTwo;
    private String photoThree;


    public Bank() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentAlias() {
        return parentAlias;
    }

    public void setParentAlias(String parentAlias) {
        this.parentAlias = parentAlias;
    }

    public String getIdDetail() {
        return idDetail;
    }

    public void setIdDetail(String idDetail) {
        this.idDetail = idDetail;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypecode() {
        return typecode;
    }

    public void setTypecode(String typecode) {
        this.typecode = typecode;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPcode() {
        return pcode;
    }

    public void setPcode(String pcode) {
        this.pcode = pcode;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public String getAdname() {
        return adname;
    }

    public void setAdname(String adname) {
        this.adname = adname;
    }

    public String getGridcode() {
        return gridcode;
    }

    public void setGridcode(String gridcode) {
        this.gridcode = gridcode;
    }

    public String getNaviPoiid() {
        return naviPoiid;
    }

    public void setNaviPoiid(String naviPoiid) {
        this.naviPoiid = naviPoiid;
    }

    public String getEntrLocation() {
        return entrLocation;
    }

    public void setEntrLocation(String entrLocation) {
        this.entrLocation = entrLocation;
    }

    public String getExitLocation() {
        return exitLocation;
    }

    public void setExitLocation(String exitLocation) {
        this.exitLocation = exitLocation;
    }

    public String getBusinessArea() {
        return businessArea;
    }

    public void setBusinessArea(String businessArea) {
        this.businessArea = businessArea;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPhotoOne() {
        return photoOne;
    }

    public void setPhotoOne(String photoOne) {
        this.photoOne = photoOne;
    }

    public String getPhotoTwo() {
        return photoTwo;
    }

    public void setPhotoTwo(String photoTwo) {
        this.photoTwo = photoTwo;
    }

    public String getPhotoThree() {
        return photoThree;
    }

    public void setPhotoThree(String photoThree) {
        this.photoThree = photoThree;
    }
}

